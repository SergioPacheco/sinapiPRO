package com.sinapipro.api.safety;

import com.sinapipro.api.safety.application.SafetyReportService;
import com.sinapipro.api.safety.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SafetyReportServiceTest {

    private SafetyInspectionRepository inspectionRepo;
    private SafetyIncidentRepository incidentRepo;
    private SafetyReportService service;

    @BeforeEach
    void setUp() {
        inspectionRepo = Mockito.mock(SafetyInspectionRepository.class);
        incidentRepo = Mockito.mock(SafetyIncidentRepository.class);
        service = new SafetyReportService(inspectionRepo, incidentRepo);
    }

    @Test
    @DisplayName("should generate PDF with inspections and incidents summary")
    void shouldGeneratePdfWithData() {
        UUID budgetId = UUID.randomUUID();
        var template = new SafetyChecklistTemplate("NR-18 Checklist", "CIVIL", "[]");
        var inspection = new SafetyInspection(budgetId, template, "João Silva",
                LocalDate.of(2026, 5, 1), "PASS", "[]", null);
        var incident = new SafetyIncident(budgetId, LocalDate.of(2026, 5, 10), "HIGH",
                "Worker fell from scaffold", "Block A", "Carlos", "Maria");

        when(inspectionRepo.findByBudgetIdOrderByInspectionDateDesc(budgetId)).thenReturn(List.of(inspection));
        when(incidentRepo.findByBudgetIdOrderByIncidentDateDesc(budgetId)).thenReturn(List.of(incident));

        byte[] pdf = service.generateSafetyReportPdf(budgetId);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf)).startsWith("%PDF-1.4");
        assertThat(new String(pdf)).contains("RELATORIO DE SEGURANCA");
    }

    @Test
    @DisplayName("should generate PDF even with no data")
    void shouldGenerateEmptyReport() {
        UUID budgetId = UUID.randomUUID();
        when(inspectionRepo.findByBudgetIdOrderByInspectionDateDesc(budgetId)).thenReturn(List.of());
        when(incidentRepo.findByBudgetIdOrderByIncidentDateDesc(budgetId)).thenReturn(List.of());

        byte[] pdf = service.generateSafetyReportPdf(budgetId);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf)).contains("Inspecoes realizadas: 0");
    }

    @Test
    @DisplayName("should count PASS and FAIL inspections correctly")
    void shouldCountInspectionStatuses() {
        UUID budgetId = UUID.randomUUID();
        var template = new SafetyChecklistTemplate("NR-35", "HEIGHT", "[]");
        var pass1 = new SafetyInspection(budgetId, template, "A", LocalDate.now(), "PASS", "[]", null);
        var pass2 = new SafetyInspection(budgetId, template, "B", LocalDate.now(), "PASS", "[]", null);
        var fail1 = new SafetyInspection(budgetId, template, "C", LocalDate.now(), "FAIL", "[]", null);

        when(inspectionRepo.findByBudgetIdOrderByInspectionDateDesc(budgetId)).thenReturn(List.of(pass1, pass2, fail1));
        when(incidentRepo.findByBudgetIdOrderByIncidentDateDesc(budgetId)).thenReturn(List.of());

        byte[] pdf = service.generateSafetyReportPdf(budgetId);
        String content = new String(pdf);

        assertThat(content).contains("Aprovadas: 2");
        assertThat(content).contains("Reprovadas: 1");
    }
}
