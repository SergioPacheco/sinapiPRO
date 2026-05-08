package com.sinapipro.api.schedule.application;

import com.sinapipro.api.schedule.domain.ActivityDependency;
import com.sinapipro.api.schedule.domain.ActivityDependencyRepository;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class CriticalPathService {

    private final ScheduleActivityRepository activityRepository;
    private final ActivityDependencyRepository dependencyRepository;

    public CriticalPathService(ScheduleActivityRepository activityRepository,
                               ActivityDependencyRepository dependencyRepository) {
        this.activityRepository = activityRepository;
        this.dependencyRepository = dependencyRepository;
    }

    public CriticalPathResult calculate(UUID budgetId) {
        List<ScheduleActivity> activities = activityRepository.findByBudgetIdOrderBySortOrder(budgetId);
        List<ActivityDependency> dependencies = dependencyRepository.findByBudgetId(budgetId);

        if (activities.isEmpty()) {
            return new CriticalPathResult(List.of(), 0);
        }

        // Build adjacency structures
        Map<UUID, ScheduleActivity> activityMap = new HashMap<>();
        Map<UUID, List<UUID>> successors = new HashMap<>();
        Map<UUID, List<UUID>> predecessors = new HashMap<>();
        Map<UUID, Integer> duration = new HashMap<>();

        for (ScheduleActivity a : activities) {
            activityMap.put(a.getId(), a);
            successors.put(a.getId(), new ArrayList<>());
            predecessors.put(a.getId(), new ArrayList<>());
            duration.put(a.getId(), (int) ChronoUnit.DAYS.between(a.getPlannedStart(), a.getPlannedEnd()) + 1);
        }

        for (ActivityDependency d : dependencies) {
            UUID predId = d.getPredecessor().getId();
            UUID succId = d.getSuccessor().getId();
            if (activityMap.containsKey(predId) && activityMap.containsKey(succId)) {
                successors.get(predId).add(succId);
                predecessors.get(succId).add(predId);
            }
        }

        // Topological sort (Kahn's algorithm)
        List<UUID> topoOrder = topologicalSort(activities, predecessors, successors);

        // Forward pass: calculate Early Start (ES) and Early Finish (EF)
        Map<UUID, Integer> earlyStart = new HashMap<>();
        Map<UUID, Integer> earlyFinish = new HashMap<>();

        for (UUID id : topoOrder) {
            int es = 0;
            for (UUID predId : predecessors.get(id)) {
                es = Math.max(es, earlyFinish.getOrDefault(predId, 0));
            }
            earlyStart.put(id, es);
            earlyFinish.put(id, es + duration.get(id));
        }

        // Project duration
        int projectDuration = earlyFinish.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        // Backward pass: calculate Late Start (LS) and Late Finish (LF)
        Map<UUID, Integer> lateFinish = new HashMap<>();
        Map<UUID, Integer> lateStart = new HashMap<>();

        List<UUID> reverseOrder = new ArrayList<>(topoOrder);
        Collections.reverse(reverseOrder);

        for (UUID id : reverseOrder) {
            int lf = projectDuration;
            for (UUID succId : successors.get(id)) {
                lf = Math.min(lf, lateStart.getOrDefault(succId, projectDuration));
            }
            lateFinish.put(id, lf);
            lateStart.put(id, lf - duration.get(id));
        }

        // Calculate float and identify critical path
        List<CriticalActivity> criticalActivities = new ArrayList<>();
        for (UUID id : topoOrder) {
            int totalFloat = lateStart.get(id) - earlyStart.get(id);
            ScheduleActivity a = activityMap.get(id);
            boolean isCritical = totalFloat == 0;
            criticalActivities.add(new CriticalActivity(
                    a.getId(), a.getName(), duration.get(id),
                    earlyStart.get(id), earlyFinish.get(id),
                    lateStart.get(id), lateFinish.get(id),
                    totalFloat, isCritical));
        }

        return new CriticalPathResult(criticalActivities, projectDuration);
    }

    private List<UUID> topologicalSort(List<ScheduleActivity> activities,
                                        Map<UUID, List<UUID>> predecessors,
                                        Map<UUID, List<UUID>> successors) {
        Map<UUID, Integer> inDegree = new HashMap<>();
        for (ScheduleActivity a : activities) {
            inDegree.put(a.getId(), predecessors.get(a.getId()).size());
        }

        Queue<UUID> queue = new LinkedList<>();
        for (var entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) queue.add(entry.getKey());
        }

        List<UUID> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            result.add(current);
            for (UUID succId : successors.get(current)) {
                inDegree.put(succId, inDegree.get(succId) - 1);
                if (inDegree.get(succId) == 0) queue.add(succId);
            }
        }

        if (result.size() != activities.size()) {
            throw new IllegalStateException("Cycle detected in activity dependencies");
        }
        return result;
    }

    public record CriticalPathResult(List<CriticalActivity> activities, int projectDurationDays) {
        public List<CriticalActivity> criticalPath() {
            return activities.stream().filter(CriticalActivity::critical).toList();
        }
    }

    public record CriticalActivity(UUID id, String name, int durationDays,
                                    int earlyStart, int earlyFinish,
                                    int lateStart, int lateFinish,
                                    int totalFloat, boolean critical) {}
}
