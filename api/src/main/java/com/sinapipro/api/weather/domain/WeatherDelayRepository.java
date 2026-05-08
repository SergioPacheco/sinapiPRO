package com.sinapipro.api.weather.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WeatherDelayRepository extends JpaRepository<WeatherDelay, UUID> {
    List<WeatherDelay> findByBudgetIdOrderByDelayDateDesc(UUID budgetId);

    @Query("SELECT COALESCE(SUM(w.hoursLost), 0) FROM WeatherDelay w WHERE w.budgetId = :budgetId")
    BigDecimal sumHoursLostByBudget(UUID budgetId);

    @Query("SELECT COUNT(w) FROM WeatherDelay w WHERE w.budgetId = :budgetId AND w.fullDayLost = true")
    long countFullDaysLost(UUID budgetId);
}
