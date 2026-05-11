package com.sinapipro.api.tenant;

import com.sinapipro.api.TestcontainersConfiguration;
import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.tenant.domain.TenantContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("Multi-Tenant Isolation")
class TenantIsolationTest {

    @Autowired ProjectRepository projectRepository;
    @Autowired EntityManager entityManager;

    private static final UUID TENANT_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID TENANT_B = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Test
    @DisplayName("Tenant A não vê projetos do Tenant B quando filtro está ativo")
    void tenantIsolation() {
        // Arrange: criar projetos para dois tenants
        var projectA = createProject("Obra Alpha", TENANT_A);
        var projectB = createProject("Obra Beta", TENANT_B);
        projectRepository.save(projectA);
        projectRepository.save(projectB);
        entityManager.flush();
        entityManager.clear();

        // Act: ativar filtro do Tenant A
        TenantContext.set(TENANT_A);
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", TENANT_A);

        var results = projectRepository.findAll();

        // Assert: só vê projetos do Tenant A
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Obra Alpha");

        TenantContext.clear();
    }

    @Test
    @DisplayName("Sem filtro ativo, vê todos os projetos (admin/superuser)")
    void noFilterSeesAll() {
        var projectA = createProject("Obra Alpha", TENANT_A);
        var projectB = createProject("Obra Beta", TENANT_B);
        projectRepository.save(projectA);
        projectRepository.save(projectB);
        entityManager.flush();
        entityManager.clear();

        // Sem ativar filtro
        var results = projectRepository.findAll();
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    private Project createProject(String name, UUID tenantId) {
        var project = new Project("PRJ-" + Math.abs(name.hashCode()), name, "Cliente Teste");
        project.setTenantId(tenantId);
        return project;
    }
}
