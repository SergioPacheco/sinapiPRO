package com.sinapipro.api.team.api;

import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.registry.domain.Employee;
import com.sinapipro.api.registry.domain.EmployeeRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.team.domain.Team;
import com.sinapipro.api.team.domain.TeamMember;
import com.sinapipro.api.team.domain.TeamRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamRepository repository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public TeamController(TeamRepository repository, EmployeeRepository employeeRepository, ProjectRepository projectRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    public List<TeamResponse> list(@RequestParam(required = false) UUID projectId) {
        var teams = projectId != null ? repository.findByProjectId(projectId) : repository.findByActiveTrue();
        return teams.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    public TeamResponse getById(@PathVariable UUID id) {
        return toResponse(findTeam(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse create(@Valid @RequestBody CreateTeamRequest req) {
        var team = new Team();
        team.setName(req.name());
        team.setDescription(req.description());
        if (req.projectId() != null) {
            projectRepository.findById(req.projectId())
                    .orElseThrow(() -> new DomainNotFoundException("Project not found: " + req.projectId()));
            team.setProjectId(req.projectId());
        }
        if (req.members() != null) {
            req.members().forEach(member -> team.addMember(createMember(member)));
        }
        return toResponse(repository.save(team));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    public TeamResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTeamRequest req) {
        var team = findTeam(id);
        team.setName(req.name());
        team.setDescription(req.description());
        if (req.projectId() != null) {
            projectRepository.findById(req.projectId())
                    .orElseThrow(() -> new DomainNotFoundException("Project not found: " + req.projectId()));
        }
        team.setProjectId(req.projectId());
        team.getMembers().clear();
        if (req.members() != null) {
            req.members().forEach(member -> team.addMember(createMember(member)));
        }
        return toResponse(repository.save(team));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    public TeamResponse addMember(@PathVariable UUID id, @Valid @RequestBody MemberRequest req) {
        var team = findTeam(id);
        team.addMember(createMember(req));
        return toResponse(repository.save(team));
    }

    @DeleteMapping("/{id}/members/{employeeId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable UUID id, @PathVariable UUID employeeId) {
        var team = findTeam(id);
        team.getMembers().removeIf(member -> member.getEmployeeId().equals(employeeId));
        repository.save(team);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        var team = findTeam(id);
        team.setActive(false);
        repository.save(team);
    }

    private Team findTeam(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Team not found: " + id));
    }

    private TeamMember createMember(MemberRequest req) {
        Employee employee = employeeRepository.findById(req.employeeId())
                .orElseThrow(() -> new DomainNotFoundException("Employee not found: " + req.employeeId()));
        return new TeamMember(employee.getId(), employee.getName(), req.role());
    }

    private TeamResponse toResponse(Team team) {
        String projectName = null;
        if (team.getProjectId() != null) {
            projectName = projectRepository.findById(team.getProjectId()).map(project -> project.getName()).orElse(null);
        }
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getProjectId(),
                projectName,
                team.isActive(),
                team.getMembers().stream()
                        .map(member -> new TeamMemberResponse(member.getEmployeeId(), member.getName(), member.getRole()))
                        .toList()
        );
    }

    public record CreateTeamRequest(@NotBlank String name, String description, UUID projectId, List<MemberRequest> members) {}
    public record UpdateTeamRequest(@NotBlank String name, String description, UUID projectId, List<MemberRequest> members) {}
    public record MemberRequest(@NotNull UUID employeeId, @NotBlank String role) {}
    public record TeamMemberResponse(UUID employeeId, String name, String role) {}
    public record TeamResponse(UUID id, String name, String description, UUID projectId, String projectName,
                               boolean active, List<TeamMemberResponse> members) {}
}
