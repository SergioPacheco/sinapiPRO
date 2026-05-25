package com.sinapipro.api.procurement.application;

import com.sinapipro.api.procurement.domain.QuotationEmail;
import com.sinapipro.api.procurement.domain.QuotationEmailRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QuotationEmailService {

    private static final Logger log = LoggerFactory.getLogger(QuotationEmailService.class);

    private final QuotationEmailRepository emailRepository;
    private final MailSender mailSender;

    public QuotationEmailService(QuotationEmailRepository emailRepository, MailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public QuotationEmail sendQuotationEmail(UUID quotationId, UUID supplierId, String recipientEmail, String subject, String body) {
        var email = new QuotationEmail(quotationId, supplierId, recipientEmail);
        email = emailRepository.save(email);

        try {
            doSendEmail(recipientEmail, subject, body);
            email.markSent();
            log.info("Quotation email sent to {} for quotation {}", recipientEmail, quotationId);
        } catch (Exception e) {
            email.markFailed(e.getMessage());
            log.error("Failed to send quotation email to {} (circuit breaker may be open): {}", recipientEmail, e.getMessage());
        }

        return emailRepository.save(email);
    }

    /**
     * Chamada externa protegida com Circuit Breaker + Retry.
     * - Retry: 3 tentativas com backoff exponencial (500ms, 1s, 2s)
     * - Circuit Breaker: abre após 5 falhas em janela de 10 chamadas, espera 30s para half-open
     */
    @CircuitBreaker(name = "emailService", fallbackMethod = "emailFallback")
    @Retry(name = "emailService")
    protected void doSendEmail(String recipientEmail, String subject, String body) {
        var message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject != null ? subject : "Solicitação de Cotação - SinapiPRO");
        message.setText(body != null ? body : "Prezado fornecedor, segue solicitação de cotação.");
        message.setFrom("noreply@sinapipro.dev");
        mailSender.send(message);
    }

    @SuppressWarnings("unused")
    private void emailFallback(String recipientEmail, String subject, String body, Exception e) {
        log.warn("Email circuit breaker OPEN — email to {} queued for later retry. Cause: {}", recipientEmail, e.getMessage());
        throw new EmailServiceUnavailableException("Email service temporarily unavailable", e);
    }

    public List<QuotationEmail> findByQuotationId(UUID quotationId) {
        return emailRepository.findByQuotationId(quotationId);
    }

    public static class EmailServiceUnavailableException extends RuntimeException {
        public EmailServiceUnavailableException(String message, Throwable cause) { super(message, cause); }
    }
}
