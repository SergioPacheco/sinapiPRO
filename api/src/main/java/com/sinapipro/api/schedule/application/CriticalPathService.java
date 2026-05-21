package com.sinapipro.api.schedule.application;

import module java.base;

import com.sinapipro.api.schedule.domain.ActivityDependency;
import com.sinapipro.api.schedule.domain.ActivityDependencyRepository;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var activities = activityRepository.findByBudgetIdOrderBySortOrder(budgetId);
        var dependencies = dependencyRepository.findByBudgetId(budgetId);

        if (activities.isEmpty()) {
            return new CriticalPathResult(List.of(), 0);
        }

        // Build adjacency structures
        var activityMap = new HashMap<UUID, ScheduleActivity>();
        var successors = new HashMap<UUID, List<UUID>>();
        var predecessors = new HashMap<UUID, List<UUID>>();
        var duration = new HashMap<UUID, Integer>();

        for (var a : activities) {
            activityMap.put(a.getId(), a);
            successors.put(a.getId(), new ArrayList<>());
            predecessors.put(a.getId(), new ArrayList<>());
            duration.put(a.getId(), (int) ChronoUnit.DAYS.between(a.getPlannedStart(), a.getPlannedEnd()) + 1);
        }

        for (var d : dependencies) {
            var predId = d.getPredecessor().getId();
            var succId = d.getSuccessor().getId();
            if (activityMap.containsKey(predId) && activityMap.containsKey(succId)) {
                successors.get(predId).add(succId);
                predecessors.get(succId).add(predId);
            }
        }

        // Topological sort (Kahn's algorithm)
        var topoOrder = topologicalSort(activities, predecessors, successors);

        // Forward pass: Early Start (ES) and Early Finish (EF)
        var earlyStart = new HashMap<UUID, Integer>();
        var earlyFinish = new HashMap<UUID, Integer>();

        for (var id : topoOrder) {
            var es = 0;
            for (var predId : predecessors.get(id)) {
                es = Math.max(es, earlyFinish.getOrDefault(predId, 0));
            }
            earlyStart.put(id, es);
            earlyFinish.put(id, es + duration.get(id));
        }

        int projectDuration = earlyFinish.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        // Backward pass: Late Start (LS) and Late Finish (LF)
        var lateFinish = new HashMap<UUID, Integer>();
        var lateStart = new HashMap<UUID, Integer>();

        var reverseOrder = new ArrayList<>(topoOrder);
        Collections.reverse(reverseOrder);

        for (var id : reverseOrder) {
            var lf = projectDuration;
            for (var succId : successors.get(id)) {
                lf = Math.min(lf, lateStart.getOrDefault(succId, projectDuration));
            }
            lateFinish.put(id, lf);
            lateStart.put(id, lf - duration.get(id));
        }

        // Calculate float and identify critical path
        var criticalActivities = new ArrayList<CriticalActivity>();
        for (var id : topoOrder) {
            var totalFloat = lateStart.get(id) - earlyStart.get(id);
            var a = activityMap.get(id);
            var isCritical = totalFloat == 0;
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
        var inDegree = new HashMap<UUID, Integer>();
        for (var a : activities) {
            inDegree.put(a.getId(), predecessors.get(a.getId()).size());
        }

        var queue = new LinkedList<UUID>();
        for (var entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) queue.add(entry.getKey());
        }

        var result = new ArrayList<UUID>();
        while (!queue.isEmpty()) {
            var current = queue.poll();
            result.add(current);
            for (var succId : successors.get(current)) {
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
        public List<UUID> criticalActivityIds() {
            return activities.stream().filter(CriticalActivity::critical).map(CriticalActivity::id).toList();
        }
    }

    public record CriticalActivity(UUID id, String name, int durationDays,
                                    int earlyStart, int earlyFinish,
                                    int lateStart, int lateFinish,
                                    int totalFloat, boolean critical) {}
}
