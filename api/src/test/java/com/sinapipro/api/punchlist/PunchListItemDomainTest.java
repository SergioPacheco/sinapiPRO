package com.sinapipro.api.punchlist;

import com.sinapipro.api.punchlist.domain.PunchListItem;
import com.sinapipro.api.punchlist.domain.PunchListStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PunchListItemDomainTest {

    private PunchListItem createItem() {
        return new PunchListItem(UUID.randomUUID(), "Block A - 3rd floor",
                "Paint peeling on wall", "FINISHING", "HIGH",
                "painter@co.com", LocalDate.now().plusDays(3), "inspector@co.com");
    }

    @Test
    @DisplayName("new item should have OPEN status")
    void newItemShouldBeOpen() {
        var item = createItem();
        assertThat(item.getStatus()).isEqualTo(PunchListStatus.OPEN);
        assertThat(item.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("should transition OPEN → IN_PROGRESS")
    void shouldMarkInProgress() {
        var item = createItem();
        item.markInProgress();
        assertThat(item.getStatus()).isEqualTo(PunchListStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("should transition OPEN → COMPLETED")
    void shouldCompleteFromOpen() {
        var item = createItem();
        item.complete();
        assertThat(item.getStatus()).isEqualTo(PunchListStatus.COMPLETED);
        assertThat(item.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("should transition IN_PROGRESS → COMPLETED")
    void shouldCompleteFromInProgress() {
        var item = createItem();
        item.markInProgress();
        item.complete();
        assertThat(item.getStatus()).isEqualTo(PunchListStatus.COMPLETED);
    }

    @Test
    @DisplayName("should transition COMPLETED → VERIFIED")
    void shouldVerify() {
        var item = createItem();
        item.complete();
        item.verify();
        assertThat(item.getStatus()).isEqualTo(PunchListStatus.VERIFIED);
    }

    @Test
    @DisplayName("should not mark IN_PROGRESS from non-OPEN")
    void shouldNotStartFromNonOpen() {
        var item = createItem();
        item.markInProgress();

        assertThatThrownBy(() -> item.markInProgress())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("should not verify non-COMPLETED item")
    void shouldNotVerifyNonCompleted() {
        var item = createItem();

        assertThatThrownBy(() -> item.verify())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("should not complete VERIFIED item")
    void shouldNotCompleteVerified() {
        var item = createItem();
        item.complete();
        item.verify();

        assertThatThrownBy(() -> item.complete())
                .isInstanceOf(IllegalStateException.class);
    }
}
