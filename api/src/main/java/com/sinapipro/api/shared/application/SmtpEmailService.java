package com.sinapipro.api.shared.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.mail.host")
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);
    private final JavaMailSender mailSender;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("noreply@sinapipro.dev");
            mailSender.send(msg);
            log.info("Email sent to {} subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email send failed", e);
        }
    }

    @Override
    public void sendWithAttachment(String to, String subject, String htmlBody, String fileName, byte[] attachment) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("noreply@sinapipro.dev");
            helper.addAttachment(fileName, new ByteArrayResource(attachment));
            mailSender.send(msg);
            log.info("Email with attachment sent to {} subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email send failed", e);
        }
    }
}
