package com.sinapipro.api.schedule.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ActivityDependencyRepository extends JpaRepository<ActivityDependency, UUID> {

    @Query("SELECT d FROM ActivityDependency d WHERE d.predecessor.budget.id = :budgetId OR d.successor.budget.id = :budgetId")
    List<ActivityDependency> findByBudgetId(UUID budgetId);

    List<ActivityDependency> findBySuccessorId(UUID successorId);
}
