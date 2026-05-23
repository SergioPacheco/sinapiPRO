package com.sinapipro.api.notification.application;

import com.sinapipro.api.notification.domain.Notification;
import com.sinapipro.api.notification.domain.NotificationRepository;
import com.sinapipro.api.registry.domain.EmployeeEpiDeliveryRepository;
import com.sinapipro.api.registry.domain.EmployeeMedicalExamRepository;
import com.sinapipro.api.registry.domain.EmployeeTrainingRepository;
import com.sinapipro.api.supplier.domain.SupplierDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Scheduler that checks for expiring documents, trainings, EPIs and medical exams
 * and generates WARNING notifications 30 days before expiry.
 */
@Component
public class ExpiryNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpiryNotificationScheduler.class);
    private static final int DAYS_AHEAD = 30;

    private final NotificationRepository notificationRepository;
    private final SupplierDocumentRepository supplierDocumentRepository;
    private final EmployeeTrainingRepository trainingRepository;
    private final EmployeeEpiDeliveryRepository epiRepository;
    private final EmployeeMedicalExamRepository medicalExamRepository;

    public ExpiryNotificationScheduler(NotificationRepository notificationRepository,
                                       SupplierDocumentRepository supplierDocumentRepository,
                                       EmployeeTrainingRepository trainingRepository,
                                       EmployeeEpiDeliveryRepository epiRepository,
                                       EmployeeMedicalExamRepository medicalExamRepository) {
        this.notificationRepository = notificationRepository;
        this.supplierDocumentRepository = supplierDocumentRepository;
        this.trainingRepository = trainingRepository;
        this.epiRepository = epiRepository;
        this.medicalExamRepository = medicalExamRepository;
    }

    @Scheduled(cron = "0 0 7 * * *") // Daily at 7 AM
    @Transactional
    public void checkExpirations() {
        var today = LocalDate.now();
        var limit = today.plusDays(DAYS_AHEAD);
        int count = 0;

        for (var doc : supplierDocumentRepository.findExpiring(today, limit)) {
            notificationRepository.save(new Notification(null, "EXPIRY", "WARNING",
                    "Documento de fornecedor vencendo",
                    doc.getDocumentType() + " #" + doc.getNumber() + " vence em " + doc.getExpiryDate(),
                    "SUPPLIER_DOCUMENT", doc.getId(), null));
            count++;
        }

        for (var t : trainingRepository.findExpiring(today, limit)) {
            notificationRepository.save(new Notification(null, "EXPIRY", "WARNING",
                    "Treinamento vencendo",
                    t.getTrainingName() + " vence em " + t.getExpiryDate(),
                    "EMPLOYEE_TRAINING", t.getId(), null));
            count++;
        }

        for (var e : epiRepository.findExpiring(today, limit)) {
            notificationRepository.save(new Notification(null, "EXPIRY", "WARNING",
                    "EPI vencendo",
                    e.getEpiDescription() + " (CA " + e.getCaNumber() + ") vence em " + e.getExpiryDate(),
                    "EMPLOYEE_EPI", e.getId(), null));
            count++;
        }

        for (var m : medicalExamRepository.findExpiring(today, limit)) {
            notificationRepository.save(new Notification(null, "EXPIRY", "WARNING",
                    "Exame médico vencendo",
                    m.getExamType() + " vence em " + m.getExpiryDate(),
                    "EMPLOYEE_MEDICAL_EXAM", m.getId(), null));
            count++;
        }

        if (count > 0) log.info("Generated {} expiry notifications", count);
    }
}
