package com.sinapipro.api.rfi;

import com.sinapipro.api.rfi.domain.Rfi;
import com.sinapipro.api.rfi.domain.RfiStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RfiDomainTest {

    private Rfi createRfi() {
        return new Rfi(UUID.randomUUID(), 1, "Foundation specs",
                "What is the concrete strength?", "HIGH",
                "eng@company.com", "arch@company.com", LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("new RFI should have OPEN status")
    void newRfiShouldBeOpen() {
        var rfi = createRfi();
        assertThat(rfi.getStatus()).isEqualTo(RfiStatus.OPEN);
        assertThat(rfi.getAnswer()).isNull();
    }

    @Test
    @DisplayName("should respond to RFI and change status to ANSWERED")
    void shouldRespondToRfi() {
        var rfi = createRfi();
        rfi.respond("Use 30 MPa concrete");

        assertThat(rfi.getStatus()).isEqualTo(RfiStatus.ANSWERED);
        assertThat(rfi.getAnswer()).isEqualTo("Use 30 MPa concrete");
        assertThat(rfi.getAnsweredAt()).isNotNull();
    }

    @Test
    @DisplayName("should close an ANSWERED RFI")
    void shouldCloseAnsweredRfi() {
        var rfi = createRfi();
        rfi.respond("Answer");
        rfi.close();

        assertThat(rfi.getStatus()).isEqualTo(RfiStatus.CLOSED);
    }

    @Test
    @DisplayName("should not respond to non-OPEN RFI")
    void shouldNotRespondToNonOpen() {
        var rfi = createRfi();
        rfi.respond("Answer");

        assertThatThrownBy(() -> rfi.respond("Another answer"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("should not close non-ANSWERED RFI")
    void shouldNotCloseNonAnswered() {
        var rfi = createRfi();

        assertThatThrownBy(() -> rfi.close())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("should detect overdue RFI")
    void shouldDetectOverdue() {
        var rfi = new Rfi(UUID.randomUUID(), 2, "Overdue RFI", "Question",
                "HIGH", "eng@co.com", "arch@co.com", LocalDate.now().minusDays(1));

        assertThat(rfi.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("should not be overdue if due date is in the future")
    void shouldNotBeOverdueIfFuture() {
        var rfi = createRfi();
        assertThat(rfi.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("answered RFI should not be overdue even if past due date")
    void answeredShouldNotBeOverdue() {
        var rfi = new Rfi(UUID.randomUUID(), 3, "Late RFI", "Q",
                "LOW", "a@b.com", "c@d.com", LocalDate.now().minusDays(5));
        rfi.respond("Answer");

        assertThat(rfi.isOverdue()).isFalse();
    }
}
