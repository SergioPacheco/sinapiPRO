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
@org.springframework.context.annotation.Profile("dev")
public class DemoDataLoader implements ApplicationRunner {

    private final EntityManager em;
    private final TenantRepository tenantRepo;
    private final ProjectRepository projectRepo;
    private final com.sinapipro.api.sinapi.application.SinapiImportService sinapiImportService;

    public DemoDataLoader(EntityManager em, TenantRepository tenantRepo, ProjectRepository projectRepo,
                          com.sinapipro.api.sinapi.application.SinapiImportService sinapiImportService) {
        this.em = em; this.tenantRepo = tenantRepo; this.projectRepo = projectRepo;
        this.sinapiImportService = sinapiImportService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (projectRepo.count() > 0) return;

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
        // Importar ZIP SINAPI completo PRIMEIRO (analítico + insumos com preços)
        try {
            var zipPath = java.nio.file.Path.of("/app/sinapi/SINAPI_ref_Insumos_Composicoes_SP_202412_NaoDesonerado.zip");
            if (!java.nio.file.Files.exists(zipPath)) {
                zipPath = java.nio.file.Path.of("docs/reference/sinapi/SINAPI_ref_Insumos_Composicoes_SP_202412_NaoDesonerado.zip");
            }
            if (java.nio.file.Files.exists(zipPath)) {
                sinapiImportService.importZip(java.nio.file.Files.newInputStream(zipPath), zipPath.getFileName().toString());
                em.flush(); em.clear();
                TenantContext.set(tenantId);
            }
        } catch (Exception e) {
            // ZIP not available - skip
        }

        // Buscar composições SINAPI reais do banco (carregadas pelo ZIP acima)
        var compositions = em.createQuery("SELECT c FROM Composition c WHERE c.sinapiCode IN :codes", Composition.class)
                .setParameter("codes", List.of("97141", "97631", "87878", "87529", "87879", "88489", "91926", "98555", "74209", "94964",
                        "87292", "87523", "87548", "87263", "87271", "87275", "87277", "87279", "87281", "87283"))
                .getResultList();

        // Se V13 ainda não rodou (composições não existem), criar manualmente
        if (compositions.isEmpty()) {
            var compDescs = List.of("Limpeza mecanizada de terreno", "Escavação manual de vala", "Concreto fck=25MPa bombeado",
                    "Alvenaria de bloco cerâmico 14cm", "Revestimento cerâmico piso", "Pintura látex PVA 2 demãos",
                    "Instalação elétrica ponto luz", "Impermeabilização com manta asfáltica");
            var compUnits = List.of("m²", "m³", "m³", "m²", "m²", "m²", "un", "m²");
            var compGroups = List.of("PRELIMINARES", "INFRAESTRUTURA", "SUPERESTRUTURA", "ALVENARIA", "REVESTIMENTO", "PINTURA", "ELÉTRICA", "IMPERMEABILIZAÇÃO");
            for (int i = 0; i < 8; i++) {
                var c = new Composition(String.valueOf(70000 + faker.number().numberBetween(1000, 9999)), compDescs.get(i), compUnits.get(i), compGroups.get(i));
                em.persist(c); compositions.add(c);
            }
        }

        // Insumos já carregados pela V13 (SINAPI SP 12/2024)
        // Buscar alguns para vincular às composições manuais (se necessário)
        var materials = em.createQuery("SELECT m FROM Material m WHERE m.sinapiCode IN :codes", Material.class)
                .setParameter("codes", List.of("00000043", "00000370", "00000553"))
                .getResultList();

        // Composições SINAPI com itens analíticos já carregadas acima;

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

        // Orçamento GRANDE (Obra 1) - usando composições SINAPI reais
        var budget = new Budget("ORC-2024-001", "Orçamento Executivo - " + project1.getName(), clients.get(0).getName(),
                BigDecimal.valueOf(8500000), BudgetStatus.IN_EXECUTION, LocalDate.of(2024, 6, 1), LocalDate.of(2025, 12, 31), Map.of());
        budget.setProjectId(project1.getId()); budget.setActive(true); budget.setState("SP"); budget.setReferenceDate(LocalDate.of(2024, 12, 1));
        em.persist(budget);

        // 10 Etapas com múltiplos itens cada (total ~50 itens)
        var stageData = List.of(
            "01. Serviços Preliminares", "02. Infraestrutura", "03. Superestrutura",
            "04. Alvenaria e Vedação", "05. Instalações Hidráulicas", "06. Instalações Elétricas",
            "07. Revestimento", "08. Pisos e Pavimentação", "09. Pintura", "10. Limpeza e Entrega");

        int compIdx = 0;
        for (int s = 0; s < stageData.size(); s++) {
            var stage = new BudgetStage(budget, null, stageData.get(s), s + 1);
            em.persist(stage);
            // 3-7 itens por etapa usando composições disponíveis
            int itemsPerStage = 3 + (s % 5);
            for (int i = 0; i < itemsPerStage && compIdx < compositions.size(); i++) {
                var comp = compositions.get(compIdx % compositions.size());
                var qty = BigDecimal.valueOf(faker.number().numberBetween(50, 5000));
                var cost = BigDecimal.valueOf(faker.number().numberBetween(15, 800)).add(BigDecimal.valueOf(faker.number().numberBetween(0, 99)).movePointLeft(2));
                em.persist(new BudgetItem(stage, comp, qty, cost, new BigDecimal("0.2500")));
                compIdx++;
            }
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

        // === CONTRATOS ===
        var contract1 = new com.sinapipro.api.contract.domain.Contract(budget, suppliers.get(0), "CTR-001",
                "Fornecimento de concreto usinado", BigDecimal.valueOf(850000), new BigDecimal("0.05"),
                LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 30));
        em.persist(contract1);
        var contract2 = new com.sinapipro.api.contract.domain.Contract(budget, suppliers.get(1), "CTR-002",
                "Mão de obra - Alvenaria e Revestimento", BigDecimal.valueOf(1200000), new BigDecimal("0.05"),
                LocalDate.of(2024, 7, 1), LocalDate.of(2025, 10, 31));
        em.persist(contract2);

        // === CRONOGRAMA (10 atividades) ===
        var actNames = List.of("Mobilização", "Fundações", "Estrutura 1º pav", "Estrutura 2º pav", "Alvenaria",
                "Instalações hidráulicas", "Instalações elétricas", "Revestimento", "Pintura", "Limpeza final");
        var actStart = LocalDate.of(2024, 6, 1);
        for (int i = 0; i < 10; i++) {
            var start = actStart.plusWeeks(i * 6);
            var end = start.plusWeeks(5);
            var weight = BigDecimal.valueOf(10);
            var act = new com.sinapipro.api.schedule.domain.ScheduleActivity(budget, actNames.get(i), start, end, weight, i + 1);
            if (i < 4) act.updateProgress(BigDecimal.valueOf(100), start, end.minusDays(2));
            else if (i == 4) act.updateProgress(BigDecimal.valueOf(60), start, null);
            em.persist(act);
        }

        // === DIÁRIO DE OBRA (últimos 5 dias) ===
        var weathers = List.of("Ensolarado", "Nublado", "Chuvoso", "Parcialmente nublado", "Ensolarado");
        for (int i = 0; i < 5; i++) {
            var log = new com.sinapipro.api.dailylog.domain.DailyLog(budget, LocalDate.now().minusDays(4 - i),
                    weathers.get(i), weathers.get((i + 1) % 5),
                    "Atividades normais. " + faker.lorem().sentence());
            em.persist(log);
        }

        // === SEGURANÇA ===
        var template = new com.sinapipro.api.safety.domain.SafetyChecklistTemplate("NR-18 Canteiro", "CIVIL", "[{\"item\":\"EPI completo\"},{\"item\":\"Sinalização\"},{\"item\":\"Proteção periférica\"}]");
        em.persist(template);
        var inspection = new com.sinapipro.api.safety.domain.SafetyInspection(budget.getId(), template, employees.get(2).getName(),
                LocalDate.now().minusDays(3), "PASS", "[{\"item\":\"EPI completo\",\"ok\":true},{\"item\":\"Sinalização\",\"ok\":true}]", null);
        em.persist(inspection);
        var incident = new com.sinapipro.api.safety.domain.SafetyIncident(budget.getId(), LocalDate.now().minusDays(7), "MEDIUM",
                "Queda de material do 3º andar - sem vítimas", "Bloco A - 3º pavimento", null, employees.get(2).getName());
        incident.resolve("Instalada tela de proteção e reforçada sinalização");
        em.persist(incident);

        // === RFI ===
        var rfi1 = new com.sinapipro.api.rfi.domain.Rfi(budget.getId(), 1, "Especificação do concreto para laje",
                "Qual o fck mínimo para a laje do 5º pavimento?", "HIGH",
                employees.get(0).getName(), "Projetista", LocalDate.now().plusDays(5));
        em.persist(rfi1);
        var rfi2 = new com.sinapipro.api.rfi.domain.Rfi(budget.getId(), 2, "Detalhe da impermeabilização",
                "Confirmar se a manta deve ter 4mm ou 3mm na área da piscina", "MEDIUM",
                employees.get(0).getName(), "Projetista", LocalDate.now().minusDays(2));
        rfi2.respond("Utilizar manta de 4mm conforme projeto revisão 3");
        em.persist(rfi2);

        // === PUNCH LIST ===
        var punchItems = List.of(
            new String[]{"Bloco A - Apt 101", "Rejunte do piso com falha", "ACABAMENTO", "HIGH"},
            new String[]{"Bloco A - Hall", "Pintura com bolhas na parede norte", "PINTURA", "MEDIUM"},
            new String[]{"Bloco B - Apt 201", "Tomada sem espelho", "ELÉTRICA", "LOW"},
            new String[]{"Área comum - Piscina", "Ralo entupido", "HIDRÁULICA", "HIGH"}
        );
        for (int i = 0; i < punchItems.size(); i++) {
            var p = punchItems.get(i);
            var item = new com.sinapipro.api.punchlist.domain.PunchListItem(budget.getId(), p[0], p[1], p[2], p[3],
                    employees.get(3).getName(), LocalDate.now().plusDays(7), employees.get(0).getName());
            if (i == 0) item.markInProgress();
            if (i == 3) { item.complete(); }
            em.persist(item);
        }

        // === EQUIPAMENTOS ===
        var equips = List.of(
            new com.sinapipro.api.equipment.domain.Equipment("EQ-001", "Retroescavadeira CAT 416F2", "HEAVY", "Caterpillar", "416F2", 2022, null, BigDecimal.valueOf(180)),
            new com.sinapipro.api.equipment.domain.Equipment("EQ-002", "Betoneira 400L", "LIGHT", "CSM", "CS400", 2023, null, BigDecimal.valueOf(25)),
            new com.sinapipro.api.equipment.domain.Equipment("EQ-003", "Caminhão Basculante", "VEHICLE", "Mercedes", "Atego 1719", 2021, "ABC-1234", BigDecimal.valueOf(120)),
            new com.sinapipro.api.equipment.domain.Equipment("EQ-004", "Grua Torre 40m", "HEAVY", "Liebherr", "65K", 2020, null, BigDecimal.valueOf(350))
        );
        equips.forEach(em::persist);

        // === NOTIFICAÇÕES ===
        em.persist(new com.sinapipro.api.notification.domain.Notification(budget.getId(), "MEASUREMENT_PENDING", "WARNING",
                "Medição #1 aguardando aprovação", "A medição de Jun/2024 está pendente de aprovação há 5 dias",
                "MEASUREMENT", med.getId(), employees.get(0).getName()));
        em.persist(new com.sinapipro.api.notification.domain.Notification(budget.getId(), "CONTRACT_EXPIRING", "INFO",
                "Contrato CTR-001 vence em 30 dias", "O contrato de concreto usinado vence em " + contract1.getEndDate(),
                "CONTRACT", contract1.getId(), null));
        em.persist(new com.sinapipro.api.notification.domain.Notification(null, "RFI_OVERDUE", "WARNING",
                "RFI #2 vencida", "A RFI sobre impermeabilização está vencida há 2 dias",
                "RFI", rfi2.getId(), employees.get(0).getName()));

        // === TABELAS AUXILIARES (via native queries para simplicidade) ===

        // Unidades de medida
        var units = List.of("m²|Metro quadrado", "m³|Metro cúbico", "m|Metro linear", "un|Unidade", "kg|Quilograma",
                "l|Litro", "h|Hora", "vb|Verba", "t|Tonelada", "cx|Caixa", "sc|Saco", "gl|Galão");
        for (var u : units) {
            var parts = u.split("\\|");
            em.createNativeQuery("INSERT INTO unit_of_measure (id, symbol, description, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), ?1, ?2, ?3, now(), now()) ON CONFLICT DO NOTHING")
                    .setParameter(1, parts[0]).setParameter(2, parts[1]).setParameter(3, tenantId).executeUpdate();
        }

        // Categorias de insumo
        var inputCats = List.of("MATERIAL", "MAO_DE_OBRA", "EQUIPAMENTO", "SERVICO_TERCEIRO", "TRANSPORTE", "ADMINISTRATIVO");
        for (var cat : inputCats) {
            em.createNativeQuery("INSERT INTO input_category (id, name, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), ?1, ?2, now(), now()) ON CONFLICT DO NOTHING")
                    .setParameter(1, cat).setParameter(2, tenantId).executeUpdate();
        }

        // Tipos de hora
        var hourTypes = List.of("NORMAL|1.00", "EXTRA_50|1.50", "EXTRA_100|2.00", "NOTURNA|1.20", "FERIADO|2.00");
        for (var ht : hourTypes) {
            var parts = ht.split("\\|");
            em.createNativeQuery("INSERT INTO hour_type (id, name, multiplier, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), ?1, ?2, ?3, now(), now()) ON CONFLICT DO NOTHING")
                    .setParameter(1, parts[0]).setParameter(2, new BigDecimal(parts[1])).setParameter(3, tenantId).executeUpdate();
        }

        // Centros de custo
        var costCodes = List.of("01|Administração", "02|Terreno", "03|Projetos", "04|Fundações", "05|Estrutura",
                "06|Alvenaria", "07|Instalações", "08|Revestimento", "09|Pintura", "10|Limpeza");
        for (var cc : costCodes) {
            var parts = cc.split("\\|");
            em.createNativeQuery("INSERT INTO cost_code (id, code, name, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), ?1, ?2, ?3, now(), now()) ON CONFLICT DO NOTHING")
                    .setParameter(1, parts[0]).setParameter(2, parts[1]).setParameter(3, tenantId).executeUpdate();
        }

        // Plano de contas
        var accounts = List.of("1|RECEITAS|null", "1.1|Receita de Medições|1", "1.2|Receita de Vendas|1",
                "2|DESPESAS|null", "2.1|Materiais|2", "2.2|Mão de Obra|2", "2.3|Equipamentos|2", "2.4|Serviços Terceiros|2",
                "3|CUSTOS INDIRETOS|null", "3.1|Administração|3", "3.2|Impostos|3");
        for (var acc : accounts) {
            var parts = acc.split("\\|");
            em.createNativeQuery("INSERT INTO chart_of_accounts (id, code, name, parent_code, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), ?1, ?2, ?3, ?4, now(), now()) ON CONFLICT DO NOTHING")
                    .setParameter(1, parts[0]).setParameter(2, parts[1]).setParameter(3, "null".equals(parts[2]) ? null : parts[2]).setParameter(4, tenantId).executeUpdate();
        }

        // BDI Config
        em.createNativeQuery("INSERT INTO bdi_config (id, name, administration, profit, financial_cost, risk, taxes, total_bdi, is_default, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'BDI Padrão Obras', 4.00, 8.00, 1.20, 0.97, 11.26, 25.00, true, ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO bdi_config (id, name, administration, profit, financial_cost, risk, taxes, total_bdi, is_default, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'BDI Diferenciado (Equipamentos)', 3.00, 6.50, 1.00, 0.50, 11.26, 22.00, false, ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();

        // Encargos sociais
        em.createNativeQuery("INSERT INTO social_charges_config (id, name, inss, fgts, thirteenth, vacation, notice, total_pct, is_default, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Encargos Horista', 20.00, 8.00, 8.33, 11.11, 2.78, 80.00, true, ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO social_charges_config (id, name, inss, fgts, thirteenth, vacation, notice, total_pct, is_default, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Encargos Mensalista', 20.00, 8.00, 8.33, 11.11, 0.00, 68.00, false, ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();

        // Índices monetários (INCC, IPCA, CUB)
        var indices = List.of("INCC|Índice Nacional da Construção Civil", "IPCA|Índice de Preços ao Consumidor", "CUB|Custo Unitário Básico");
        for (var idx : indices) {
            var parts = idx.split("\\|");
            em.createNativeQuery("INSERT INTO monetary_index (id, code, name, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), ?1, ?2, ?3, now(), now()) ON CONFLICT DO NOTHING")
                    .setParameter(1, parts[0]).setParameter(2, parts[1]).setParameter(3, tenantId).executeUpdate();
        }

        // Alçadas de aprovação
        em.createNativeQuery("INSERT INTO payment_authority_level (id, name, max_amount, approver_role, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Nível 1 - Mestre', 5000.00, 'MESTRE_OBRA', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO payment_authority_level (id, name, max_amount, approver_role, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Nível 2 - Engenheiro', 50000.00, 'ENGENHEIRO', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO payment_authority_level (id, name, max_amount, approver_role, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Nível 3 - Diretor', 999999999.00, 'ADMIN', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();

        // Configurações do sistema
        em.createNativeQuery("INSERT INTO app_settings (id, key, value, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'company.name', 'SinapiPRO Construtora Demo', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO app_settings (id, key, value, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'company.cnpj', '12.345.678/0001-90', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO app_settings (id, key, value, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'sinapi.reference_month', '2026-03', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO app_settings (id, key, value, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'sinapi.state', 'SC', ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();

        // Equipe
        em.createNativeQuery("INSERT INTO team (id, name, project_id, leader_id, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Equipe Estrutura', ?1, ?2, ?3, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, project1.getId()).setParameter(2, employees.get(1).getId()).setParameter(3, tenantId).executeUpdate();

        // Templates de relatório
        em.createNativeQuery("INSERT INTO report_template (id, name, type, description, template_path, active, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Planilha Orçamentária', 'BUDGET', 'Planilha completa com BDI', 'templates/budget-worksheet.jte', true, ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();
        em.createNativeQuery("INSERT INTO report_template (id, name, type, description, template_path, active, tenant_id, created_at, updated_at) VALUES (gen_random_uuid(), 'Boletim de Medição', 'MEASUREMENT', 'Boletim para aprovação', 'templates/measurement-report.jte', true, ?1, now(), now()) ON CONFLICT DO NOTHING")
                .setParameter(1, tenantId).executeUpdate();

        TenantContext.clear();
    }
}
