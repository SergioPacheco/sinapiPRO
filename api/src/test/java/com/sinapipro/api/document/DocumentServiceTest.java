package com.sinapipro.api.document;

import com.sinapipro.api.document.application.DocumentService;
import com.sinapipro.api.document.domain.Document;
import com.sinapipro.api.document.domain.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DocumentServiceTest {

    private DocumentRepository repository;
    private DocumentService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(DocumentRepository.class);
        service = new DocumentService(repository, tempDir.toString());
    }

    @Test
    @DisplayName("should upload PDF file successfully")
    void shouldUploadPdf() throws Exception {
        UUID budgetId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        var file = new MockMultipartFile("file", "report.pdf", "application/pdf", "fake-pdf-content".getBytes());

        when(repository.countByEntityTypeAndEntityIdAndFileName("MEASUREMENT", entityId, "report.pdf")).thenReturn(0);
        when(repository.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

        var doc = service.upload(budgetId, "MEASUREMENT", entityId, "Monthly Report", file, "admin");

        assertThat(doc.getFileName()).isEqualTo("report.pdf");
        assertThat(doc.getVersion()).isEqualTo(1);
        assertThat(doc.getContentType()).isEqualTo("application/pdf");
    }

    @Test
    @DisplayName("should increment version for same file name")
    void shouldIncrementVersion() throws Exception {
        UUID budgetId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        var file = new MockMultipartFile("file", "report.pdf", "application/pdf", "content".getBytes());

        when(repository.countByEntityTypeAndEntityIdAndFileName("BUDGET", entityId, "report.pdf")).thenReturn(2);
        when(repository.save(any(Document.class))).thenAnswer(inv -> inv.getArgument(0));

        var doc = service.upload(budgetId, "BUDGET", entityId, "Report v3", file, "admin");

        assertThat(doc.getVersion()).isEqualTo(3);
    }

    @Test
    @DisplayName("should reject disallowed file types")
    void shouldRejectDisallowedTypes() {
        UUID budgetId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        var file = new MockMultipartFile("file", "virus.exe", "application/x-msdownload", "malware".getBytes());

        assertThatThrownBy(() -> service.upload(budgetId, "DOC", entityId, "Bad", file, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not allowed");
    }
}
