package com.sinapipro.api.team.api;

import com.sinapipro.api.team.domain.Team;
import com.sinapipro.api.team.domain.TeamMember;
import com.sinapipro.api.team.domain.TeamRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamRepository repository;

    public TeamController(TeamRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Team> list(@RequestParam(required = false) UUID projectId) {
        if (projectId != null) return repository.findByProjectId(projectId);
        return repository.findByActiveTrue();
    }

    @GetMapping("/{id}")
    public Team getById(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Team create(@Valid @RequestBody CreateTeamRequest req) {
        var team = new Team();
        team.setName(req.name());
        team.setDescription(req.description());
        team.setProjectId(req.projectId());
        if (req.members() != null) {
            req.members().forEach(m -> team.addMember(new TeamMember(m.employeeId(), m.name(), m.role())));
        }
        return repository.save(team);
    }

    @PostMapping("/{id}/members")
    public Team addMember(@PathVariable UUID id, @Valid @RequestBody MemberRequest req) {
        var team = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        team.addMember(new TeamMember(req.employeeId(), req.name(), req.role()));
        return repository.save(team);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        var team = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        team.setActive(false);
        repository.save(team);
    }

    record CreateTeamRequest(@NotBlank String name, String description, UUID projectId, List<MemberRequest> members) {}
    record MemberRequest(UUID employeeId, @NotBlank String name, @NotBlank String role) {}
}
