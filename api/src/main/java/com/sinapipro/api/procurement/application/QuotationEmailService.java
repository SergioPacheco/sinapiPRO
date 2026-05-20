package com.sinapipro.api.procurement.application;

import com.sinapipro.api.procurement.domain.QuotationEmail;
import com.sinapipro.api.procurement.domain.QuotationEmailRepository;
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
            var message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject(subject != null ? subject : "Solicitação de Cotação - SinapiPRO");
            message.setText(body != null ? body : "Prezado fornecedor, segue solicitação de cotação. Acesse o sistema para responder.");
            message.setFrom("noreply@sinapipro.dev");
            mailSender.send(message);
            email.markSent();
            log.info("Quotation email sent to {} for quotation {}", recipientEmail, quotationId);
        } catch (Exception e) {
            email.markFailed(e.getMessage());
            log.error("Failed to send quotation email to {}: {}", recipientEmail, e.getMessage());
        }

        return emailRepository.save(email);
    }

    public List<QuotationEmail> findByQuotationId(UUID quotationId) {
        return emailRepository.findByQuotationId(quotationId);
    }
}
