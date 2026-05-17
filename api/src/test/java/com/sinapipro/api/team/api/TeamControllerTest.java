package com.sinapipro.api.team.api;

import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.registry.domain.Employee;
import com.sinapipro.api.registry.domain.EmployeeRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.team.domain.Team;
import com.sinapipro.api.team.domain.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock TeamRepository teamRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock ProjectRepository projectRepository;

    private TeamController controller;

    @BeforeEach
    void setUp() {
        controller = new TeamController(teamRepository, employeeRepository, projectRepository);
    }

    @Test
    @DisplayName("should create team with real employee members")
    void shouldCreateTeamWithRealMembers() {
        UUID employeeId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Employee employee = createEmployee(employeeId, "EMP-001", "Joao da Silva", "Pedreiro");
        Project project = new Project("OBR-001", "Residencial Azul", "Cliente");
        ReflectionTestUtils.setField(project, "id", projectId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            ReflectionTestUtils.setField(team, "id", UUID.randomUUID());
            return team;
        });

        var response = controller.create(new TeamController.CreateTeamRequest(
                "Equipe de Acabamento",
                "Equipe principal da fase de acabamento",
                projectId,
                List.of(new TeamController.MemberRequest(employeeId, "Lider de frente"))
        ));

        assertThat(response.projectId()).isEqualTo(projectId);
        assertThat(response.projectName()).isEqualTo("Residencial Azul");
        assertThat(response.members()).hasSize(1);
        assertThat(response.members().getFirst().name()).isEqualTo("Joao da Silva");
    }

    @Test
    @DisplayName("should reject team creation when employee does not exist")
    void shouldRejectCreationWhenEmployeeMissing() {
        UUID missingEmployeeId = UUID.randomUUID();
        when(employeeRepository.findById(missingEmployeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.create(new TeamController.CreateTeamRequest(
                "Equipe X",
                null,
                null,
                List.of(new TeamController.MemberRequest(missingEmployeeId, "Membro"))
        ))).isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Employee not found");
    }

    @Test
    @DisplayName("should update team members from employee references")
    void shouldUpdateTeamMembers() {
        UUID teamId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Employee employee = createEmployee(employeeId, "EMP-002", "Maria Lima", "Encarregada");
        Team team = new Team();
        team.setName("Equipe antiga");
        ReflectionTestUtils.setField(team, "id", teamId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var updated = controller.update(teamId, new TeamController.UpdateTeamRequest(
                "Equipe civil",
                "Atualizada",
                null,
                List.of(new TeamController.MemberRequest(employeeId, "Encarregada da equipe"))
        ));

        assertThat(updated.name()).isEqualTo("Equipe civil");
        assertThat(updated.members()).hasSize(1);
        assertThat(updated.members().getFirst().name()).isEqualTo("Maria Lima");
    }

    private Employee createEmployee(UUID id, String code, String name, String role) {
        Employee employee = new Employee(
                code, name, "123.456.789-00", role, role,
                "EMPLOYEE", "ACTIVE", "x@y.com", "(48) 3333-0000", "(48) 99999-0000",
                "Contato", "(48) 98888-1111", "Rua A", "Florianopolis", "SC", "88000-000",
                "CC-01", null, null, new BigDecimal("35"), LocalDate.of(2026, 1, 1), null
        );
        ReflectionTestUtils.setField(employee, "id", id);
        return employee;
    }
}
