package com.sinapipro.api.shared.application;

/**
 * Email abstraction. Used by QuotationEmailService, NotificationService, etc.
 */
public interface EmailService {
    void send(String to, String subject, String htmlBody);
    void sendWithAttachment(String to, String subject, String htmlBody, String fileName, byte[] attachment);
}
