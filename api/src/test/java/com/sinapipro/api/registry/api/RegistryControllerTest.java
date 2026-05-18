package com.sinapipro.api.registry.api;

import com.sinapipro.api.registry.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistryControllerTest {

    @Mock ClientRepository clientRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UnitOfMeasureRepository unitRepository;
    @Mock PaymentMethodRepository paymentMethodRepository;
    @Mock BankAccountRepository bankAccountRepository;
    @Mock ContractorRepository contractorRepository;
    @Mock InspectorRepository inspectorRepository;
    @Mock BdiTemplateRepository bdiTemplateRepository;
    @Mock SocialChargeRepository socialChargeRepository;
    @Mock PaymentConditionRepository paymentConditionRepository;
    @Mock CostCenterRepository costCenterRepository;
    @Mock FinanceCategoryRepository financeCategoryRepository;
    @Mock ProjectTypeRepository projectTypeRepository;
    @Mock DefaultStageRepository defaultStageRepository;
    @Mock IncidentTypeRepository incidentTypeRepository;
    @Mock EpiRepository epiRepository;
    @Mock ReportTemplateRepository reportTemplateRepository;

    private RegistryController controller;

    @BeforeEach
    void setUp() {
        controller = new RegistryController(clientRepository, employeeRepository, unitRepository,
                paymentMethodRepository, bankAccountRepository, contractorRepository, inspectorRepository,
                bdiTemplateRepository, socialChargeRepository, paymentConditionRepository, costCenterRepository,
                financeCategoryRepository, projectTypeRepository, defaultStageRepository, incidentTypeRepository,
                epiRepository, reportTemplateRepository);
    }

    @Test
    @DisplayName("should create employee with richer master data")
    void shouldCreateEmployeeWithMasterData() {
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            ReflectionTestUtils.setField(employee, "id", UUID.randomUUID());
            return employee;
        });

        var response = controller.createEmployee(new RegistryController.CreateEmployeeRequest(
                "EMP-0001",
                "Joao da Silva",
                "123.456.789-00",
                "Pedreiro",
                "Alvenaria estrutural",
                "EMPLOYEE",
                "ACTIVE",
                "joao@empresa.com.br",
                "(48) 3333-0000",
                "(48) 99999-0000",
                "Maria da Silva",
                "(48) 98888-1111",
                "Rua A, 100",
                "Florianopolis",
                "SC",
                "88000-000",
                "OBRA-FUNDACOES",
                null,
                "Profissional certificado NR-35",
                new BigDecimal("42.75"),
                LocalDate.of(2026, 1, 10),
                null
        ));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().employeeCode()).isEqualTo("EMP-0001");
        assertThat(response.getBody().specialty()).isEqualTo("Alvenaria estrutural");
        assertThat(response.getBody().employmentStatus()).isEqualTo("ACTIVE");
        assertThat(response.getBody().costCenter()).isEqualTo("OBRA-FUNDACOES");
    }

    @Test
    @DisplayName("should update employee master data")
    void shouldUpdateEmployeeMasterData() {
        UUID employeeId = UUID.randomUUID();
        Employee employee = new Employee(
                "EMP-0001", "Joao da Silva", "123.456.789-00", "Pedreiro", "Alvenaria estrutural",
                "EMPLOYEE", "ACTIVE", "joao@empresa.com.br", "(48) 3333-0000", "(48) 99999-0000",
                "Maria da Silva", "(48) 98888-1111", "Rua A, 100", "Florianopolis", "SC", "88000-000",
                "OBRA-FUNDACOES", null, "Profissional certificado NR-35", new BigDecimal("42.75"),
                LocalDate.of(2026, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var updated = controller.updateEmployee(employeeId, new RegistryController.UpdateEmployeeRequest(
                "Joao da Silva",
                "123.456.789-00",
                "Encarregado",
                "Acabamento fino",
                "EMPLOYEE",
                "ON_LEAVE",
                "joao@empresa.com.br",
                "(48) 3333-0000",
                "(48) 99999-0000",
                "Maria da Silva",
                "(48) 98888-1111",
                "Rua B, 200",
                "Sao Jose",
                "SC",
                "88100-000",
                "OBRA-ACABAMENTO",
                null,
                "Afastado por 15 dias",
                new BigDecimal("48.50"),
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 6, 1)
        ));

        assertThat(updated.role()).isEqualTo("Encarregado");
        assertThat(updated.specialty()).isEqualTo("Acabamento fino");
        assertThat(updated.employmentStatus()).isEqualTo("ON_LEAVE");
        assertThat(updated.city()).isEqualTo("Sao Jose");
        assertThat(updated.terminationDate()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    @DisplayName("should reject employee detail when id does not exist")
    void shouldRejectEmployeeDetailWhenMissing() {
        UUID employeeId = UUID.randomUUID();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getEmployee(employeeId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Employee not found");
    }
}
