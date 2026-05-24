package com.sinapipro.api.config;

import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.project.domain.*;
import com.sinapipro.api.registry.domain.*;
import com.sinapipro.api.sinapi.domain.*;
import com.sinapipro.api.supplier.domain.*;
import com.sinapipro.api.tenant.domain.*;
import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class DemoDataLoader implements ApplicationRunner {

    private final EntityManager em;
    private final TenantRepository tenantRepo;
    private final ProjectRepository projectRepo;

    public DemoDataLoader(EntityManager em, TenantRepository tenantRepo, ProjectRepository projectRepo) {
        this.em = em; this.tenantRepo = tenantRepo; this.projectRepo = projectRepo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (projectRepo.count() > 0) return;

        // Criar coluna search_vector (tsvector não é criada pelo Hibernate ddl-auto)
        try { em.createNativeQuery("ALTER TABLE composition ADD COLUMN IF NOT EXISTS search_vector tsvector").executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("ALTER TABLE material ADD COLUMN IF NOT EXISTS search_vector tsvector").executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("CREATE INDEX IF NOT EXISTS idx_composition_search ON composition USING gin(search_vector)").executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("CREATE INDEX IF NOT EXISTS idx_material_search ON material USING gin(search_vector)").executeUpdate(); } catch (Exception ignored) {}
        var faker = new Faker(new Locale("pt", "BR"));
        var tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // Tenant
        em.createNativeQuery("INSERT INTO tenant (id, name, plan, active, max_users, max_projects, created_at, updated_at) VALUES (?1, ?2, 'PROFESSIONAL', true, 50, 100, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).setParameter(2, faker.company().name() + " Construtora Ltda").executeUpdate();
        em.flush(); em.clear();
        TenantContext.set(tenantId);

        // 5 Clientes
        var clients = new ArrayList<Client>();
        for (int i = 0; i < 5; i++) {
            var isPJ = i < 3;
            var c = new Client(
                    isPJ ? faker.company().name() : faker.name().fullName(),
                    isPJ ? faker.cnpj().valid() : faker.cpf().valid(),
                    faker.internet().emailAddress(),
                    faker.phoneNumber().cellPhone(),
                    faker.address().streetAddress(),
                    faker.address().city(), "SC", null);
            em.persist(c); clients.add(c);
        }

        // 5 Fornecedores
        var suppliers = new ArrayList<Supplier>();
        var categories = List.of("MATERIAL", "SERVICO", "MATERIAL", "EQUIPAMENTO", "SERVICO");
        for (int i = 0; i < 5; i++) {
            var s = new Supplier("FORN-" + String.format("%03d", i + 1), faker.company().name(), faker.company().name(),
                    faker.cnpj().valid(), faker.internet().emailAddress(), faker.phoneNumber().cellPhone(),
                    faker.name().fullName(), "www." + faker.internet().domainName(), categories.get(i), "APPROVED",
                    faker.number().numberBetween(15, 45), faker.number().numberBetween(3, 15),
                    faker.address().streetAddress(), faker.address().city(), "SC", faker.address().zipCode(),
                    null, faker.number().numberBetween(6, 10), true);
            em.persist(s); suppliers.add(s);
        }

        // 5 Funcionários
        var employees = new ArrayList<Employee>();
        var roles = List.of("Engenheiro Civil", "Mestre de Obras", "Técnico Segurança", "Pedreiro", "Eletricista");
        var specialties = List.of("Estruturas", "Execução", "SST", "Alvenaria", "Instalações");
        for (int i = 0; i < 5; i++) {
            var e = new Employee("EMP-" + String.format("%03d", i + 1), faker.name().fullName(), faker.cpf().valid(),
                    roles.get(i), specialties.get(i), "EMPLOYEE", "ACTIVE", faker.internet().emailAddress(),
                    faker.phoneNumber().phoneNumber(), faker.phoneNumber().cellPhone(), null, null,
                    faker.address().city(), faker.address().city(), "SC", faker.address().zipCode(),
                    null, null, null, BigDecimal.valueOf(faker.number().numberBetween(25, 90)),
                    LocalDate.now().minusMonths(faker.number().numberBetween(6, 36)), null);
            em.persist(e); employees.add(e);
        }

        // Conta bancária
        var bank = new BankAccount("001", "Banco do Brasil", faker.number().digits(4) + "-" + faker.number().digit(),
                faker.number().digits(5) + "-" + faker.number().digit(), "CHECKING", faker.company().name());
        em.persist(bank);

        // 8 Composições SINAPI com insumos
        var compDescs = List.of("Limpeza mecanizada de terreno", "Escavação manual de vala", "Concreto fck=25MPa bombeado",
                "Alvenaria de bloco cerâmico 14cm", "Revestimento cerâmico piso", "Pintura látex PVA 2 demãos",
                "Instalação elétrica ponto luz", "Impermeabilização com manta asfáltica");
        var compUnits = List.of("m²", "m³", "m³", "m²", "m²", "m²", "un", "m²");
        var compGroups = List.of("PRELIMINARES", "INFRAESTRUTURA", "SUPERESTRUTURA", "ALVENARIA", "REVESTIMENTO", "PINTURA", "ELÉTRICA", "IMPERMEABILIZAÇÃO");
        var compositions = new ArrayList<Composition>();
        for (int i = 0; i < 8; i++) {
            var c = new Composition(String.valueOf(70000 + faker.number().numberBetween(1000, 9999)), compDescs.get(i), compUnits.get(i), compGroups.get(i));
            em.persist(c); compositions.add(c);
        }

        // Insumos (materiais e mão de obra) vinculados às composições
        var mat = List.of(
            new String[]{"00000043", "Cimento Portland CP II-32", "kg"},
            new String[]{"00000370", "Areia média lavada", "m³"},
            new String[]{"00000553", "Brita 1", "m³"},
            new String[]{"00000025", "Aço CA-50 10mm", "kg"},
            new String[]{"00004750", "Bloco cerâmico 14x19x39cm", "un"},
            new String[]{"00001379", "Argamassa colante AC-II", "kg"},
            new String[]{"00003777", "Tinta látex PVA branca", "l"},
            new String[]{"00006111", "Manta asfáltica 4mm", "m²"},
            new String[]{"00034794", "Servente", "h"},
            new String[]{"00034795", "Pedreiro", "h"},
            new String[]{"00034796", "Eletricista", "h"},
            new String[]{"00005075", "Fio 2,5mm²", "m"}
        );
        var materials = new ArrayList<Material>();
        for (var m : mat) {
            var material = new Material(m[0], m[1], m[2], "SINAPI");
            em.persist(material); materials.add(material);
        }

        // Vincular insumos às composições (coeficientes realistas)
        // Comp 0: Limpeza - servente
        em.persist(new CompositionItem(compositions.get(0), materials.get(8), new BigDecimal("0.050"), ItemType.LABOR));
        // Comp 1: Escavação - servente
        em.persist(new CompositionItem(compositions.get(1), materials.get(8), new BigDecimal("3.200"), ItemType.LABOR));
        // Comp 2: Concreto - cimento + areia + brita + servente + pedreiro
        em.persist(new CompositionItem(compositions.get(2), materials.get(0), new BigDecimal("320.000"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(2), materials.get(1), new BigDecimal("0.660"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(2), materials.get(2), new BigDecimal("0.880"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(2), materials.get(8), new BigDecimal("6.000"), ItemType.LABOR));
        em.persist(new CompositionItem(compositions.get(2), materials.get(9), new BigDecimal("2.000"), ItemType.LABOR));
        // Comp 3: Alvenaria - bloco + argamassa + pedreiro + servente
        em.persist(new CompositionItem(compositions.get(3), materials.get(4), new BigDecimal("25.000"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(3), materials.get(5), new BigDecimal("8.500"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(3), materials.get(9), new BigDecimal("1.200"), ItemType.LABOR));
        em.persist(new CompositionItem(compositions.get(3), materials.get(8), new BigDecimal("0.600"), ItemType.LABOR));
        // Comp 4: Revestimento - argamassa + pedreiro + servente
        em.persist(new CompositionItem(compositions.get(4), materials.get(5), new BigDecimal("5.000"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(4), materials.get(9), new BigDecimal("0.800"), ItemType.LABOR));
        em.persist(new CompositionItem(compositions.get(4), materials.get(8), new BigDecimal("0.400"), ItemType.LABOR));
        // Comp 5: Pintura - tinta + servente
        em.persist(new CompositionItem(compositions.get(5), materials.get(6), new BigDecimal("0.350"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(5), materials.get(8), new BigDecimal("0.330"), ItemType.LABOR));
        // Comp 6: Elétrica - fio + eletricista + servente
        em.persist(new CompositionItem(compositions.get(6), materials.get(11), new BigDecimal("12.000"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(6), materials.get(10), new BigDecimal("1.500"), ItemType.LABOR));
        em.persist(new CompositionItem(compositions.get(6), materials.get(8), new BigDecimal("0.750"), ItemType.LABOR));
        // Comp 7: Impermeabilização - manta + servente
        em.persist(new CompositionItem(compositions.get(7), materials.get(7), new BigDecimal("1.100"), ItemType.MATERIAL));
        em.persist(new CompositionItem(compositions.get(7), materials.get(8), new BigDecimal("0.500"), ItemType.LABOR));

        // 2 Projetos
        var project1 = new Project("OBR-2024-001", "Residencial " + faker.address().cityName(), clients.get(0).getName());
        project1.update("Residencial " + faker.address().cityName(), "Edifício residencial 12 pavimentos", clients.get(0).getName(),
                clients.get(0).getDocument(), faker.address().streetAddress(), faker.address().city(), "SC",
                employees.get(0).getName(), "SC-" + faker.number().digits(10), LocalDate.of(2024, 6, 1), LocalDate.of(2025, 12, 31),
                BigDecimal.valueOf(faker.number().numberBetween(3000, 6000)), BigDecimal.valueOf(faker.number().numberBetween(5000000, 12000000)),
                clients.get(0).getId(), employees.get(0).getId(), null, null, "ALV-" + faker.number().digits(8), LocalDate.of(2025, 12, 31), "SC-" + faker.number().digits(9), faker.address().zipCode());
        project1.updateStatus(ProjectStatus.IN_PROGRESS);
        em.persist(project1);

        var project2 = new Project("OBR-2025-002", "Escola Municipal " + faker.address().cityName(), clients.get(2).getName());
        project2.update("Escola Municipal " + faker.address().cityName(), "Construção de escola com 12 salas", clients.get(2).getName(),
                clients.get(2).getDocument(), faker.address().streetAddress(), "Joinville", "SC",
                employees.get(0).getName(), null, LocalDate.of(2025, 1, 15), LocalDate.of(2026, 6, 30),
                BigDecimal.valueOf(2200), BigDecimal.valueOf(4200000), null, employees.get(0).getId(), null, null, null, null, null, "89221-005");
        em.persist(project2);

        // Orçamento completo (Obra 1)
        var budget = new Budget("ORC-2024-001", "Orçamento " + project1.getName(), clients.get(0).getName(),
                BigDecimal.valueOf(8500000), BudgetStatus.IN_EXECUTION, LocalDate.of(2024, 6, 1), LocalDate.of(2025, 12, 31), Map.of());
        budget.setProjectId(project1.getId()); budget.setActive(true); budget.setState("SC"); budget.setReferenceDate(LocalDate.of(2026, 3, 1));
        em.persist(budget);

        // 5 Etapas com itens
        var stageNames = List.of("01. Serviços Preliminares", "02. Superestrutura", "03. Alvenaria", "04. Revestimento", "05. Pintura");
        for (int i = 0; i < 5; i++) {
            var stage = new BudgetStage(budget, null, stageNames.get(i), i + 1);
            em.persist(stage);
            var qty = BigDecimal.valueOf(faker.number().numberBetween(500, 9000));
            var cost = BigDecimal.valueOf(faker.number().numberBetween(15, 600)).add(BigDecimal.valueOf(faker.number().numberBetween(0, 99)).movePointLeft(2));
            em.persist(new BudgetItem(stage, compositions.get(i), qty, cost, new BigDecimal("0.2500")));
        }

        // Medição
        var med = new Measurement(budget, 1, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), new BigDecimal("0.05"));
        em.persist(med);

        // Pedido de compra
        var po = new PurchaseOrder(budget, suppliers.get(0), null, "PED-001",
                faker.commerce().productName() + " - " + faker.number().numberBetween(100, 5000) + " unidades",
                BigDecimal.valueOf(faker.number().numberBetween(500, 3000)), BigDecimal.valueOf(faker.number().numberBetween(20, 200)), null);
        po.approve(); em.persist(po);

        // Contas a pagar
        for (int i = 0; i < 3; i++) {
            var pay = new Payable(budget.getId(), suppliers.get(i).getId(),
                    "NF " + faker.number().digits(5) + " - " + faker.commerce().productName(),
                    BigDecimal.valueOf(faker.number().numberBetween(15000, 200000)),
                    LocalDate.now().plusDays(faker.number().numberBetween(-10, 30)), categories.get(i));
            pay.setProjectId(project1.getId());
            em.persist(pay);
        }

        // Contas a receber
        var rec1 = new Receivable(budget.getId(), "Medição #1 - Jun/2024", BigDecimal.valueOf(425000), LocalDate.of(2024, 7, 15), "MEDICAO");
        rec1.setProjectId(project1.getId()); rec1.receive(BigDecimal.valueOf(425000), LocalDate.of(2024, 7, 16));
        em.persist(rec1);

        var rec2 = new Receivable(budget.getId(), "Medição #2 - Jul/2024", BigDecimal.valueOf(680000), LocalDate.of(2024, 8, 15), "MEDICAO");
        rec2.setProjectId(project1.getId());
        em.persist(rec2);

        // Atualizar search_vector para full-text search
        try { em.createNativeQuery("UPDATE composition SET search_vector = to_tsvector('portuguese', coalesce(sinapi_code,'') || ' ' || coalesce(description,''))").executeUpdate(); } catch (Exception ignored) {}
        try { em.createNativeQuery("UPDATE material SET search_vector = to_tsvector('portuguese', coalesce(sinapi_code,'') || ' ' || coalesce(description,''))").executeUpdate(); } catch (Exception ignored) {}

        TenantContext.clear();
    }
}
