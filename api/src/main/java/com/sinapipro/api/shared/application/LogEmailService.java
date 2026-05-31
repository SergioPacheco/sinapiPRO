package com.sinapipro.api.shared.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * Fallback: logs emails instead of sending. Active when spring.mail.host is not configured.
 */
@Service
@ConditionalOnMissingBean(SmtpEmailService.class)
public class LogEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LogEmailService.class);

    @Override
    public void send(String to, String subject, String htmlBody) {
        log.info("[EMAIL-DEV] To: {} | Subject: {} | Body length: {}", to, subject, htmlBody.length());
    }

    @Override
    public void sendWithAttachment(String to, String subject, String htmlBody, String fileName, byte[] attachment) {
        log.info("[EMAIL-DEV] To: {} | Subject: {} | Attachment: {} ({}B)", to, subject, fileName, attachment.length);
    }
}
