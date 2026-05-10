-- ============================================================
-- SEED COMPLETA: Demonstra o fluxo completo de uma obra
-- Obra: Residencial Parque das Flores (em execução)
-- ============================================================

-- Ensure project table exists (normally created by Hibernate ddl-auto)
CREATE TABLE IF NOT EXISTS project (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    description varchar(1000),
    customer_name varchar(200) NOT NULL,
    customer_document varchar(20),
    address varchar(300),
    city varchar(100),
    state varchar(2),
    responsible_engineer varchar(200),
    art_number varchar(50),
    start_date date,
    expected_end_date date,
    actual_end_date date,
    status varchar(20) NOT NULL DEFAULT 'PLANNING',
    total_area numeric(14,2),
    total_budget numeric(18,2),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- === PROJETO ===
INSERT INTO project (id, code, name, description, customer_name, customer_document, address, city, state,
    responsible_engineer, art_number, start_date, expected_end_date, status, total_area, total_budget, created_at, updated_at)
VALUES
    ('a0000001-0000-0000-0000-000000000001', 'OBR-2026-001', 'Residencial Parque das Flores',
     'Condomínio residencial com 4 blocos, 64 unidades, área de lazer completa',
     'Construtora Horizonte Ltda', '12.345.678/0001-90',
     'Rua das Acácias, 500 - Lagoa Nova', 'Natal', 'RN',
     'Eng. Carlos Alberto Silva', 'ART-2026-001234',
     '2026-01-15', '2027-06-30', 'IN_PROGRESS', 8500.00, 12500000.00, now(), now()),
    ('a0000001-0000-0000-0000-000000000002', 'OBR-2026-002', 'Reforma Hospital Regional',
     'Reforma e ampliação do bloco cirúrgico',
     'Governo do Estado RN', '08.241.739/0001-05',
     'Av. Senador Salgado Filho, 1800', 'Natal', 'RN',
     'Eng. Maria Fernanda Costa', 'ART-2026-005678',
     '2026-03-01', '2026-12-20', 'IN_PROGRESS', 2200.00, 4800000.00, now(), now()),
    ('a0000001-0000-0000-0000-000000000003', 'OBR-2026-003', 'Galpão Industrial TechPark',
     'Galpão logístico com escritórios e docas',
     'TechPark Logística S.A.', '45.678.901/0001-23',
     'Distrito Industrial, Lote 45', 'Parnamirim', 'RN',
     'Eng. Roberto Mendes', null,
     '2026-05-01', '2026-11-30', 'PLANNING', 5000.00, 3200000.00, now(), now());

-- === CLIENTES ===
INSERT INTO client (id, name, document, email, phone, address, city, state, notes) VALUES
    ('c0000001-0000-0000-0000-000000000001', 'Construtora Horizonte Ltda', '12.345.678/0001-90', 'contato@horizonte.com.br', '(84) 3211-5000', 'Av. Prudente de Morais, 1200', 'Natal', 'RN', 'Cliente desde 2020'),
    ('c0000001-0000-0000-0000-000000000002', 'Governo do Estado RN', '08.241.739/0001-05', 'licitacoes@rn.gov.br', '(84) 3232-1000', 'Centro Administrativo', 'Natal', 'RN', 'Contrato via licitação'),
    ('c0000001-0000-0000-0000-000000000003', 'TechPark Logística S.A.', '45.678.901/0001-23', 'obras@techpark.com.br', '(84) 3344-2000', 'Distrito Industrial', 'Parnamirim', 'RN', null);

-- === FUNCIONÁRIOS ===
INSERT INTO employee (id, name, document, role, type, email, phone, hourly_rate, admission_date) VALUES
    ('e0000001-0000-0000-0000-000000000001', 'João Carlos Pereira', '123.456.789-00', 'Mestre de Obras', 'EMPLOYEE', 'joao@obra.com', '(84) 99900-1001', 45.00, '2024-03-15'),
    ('e0000001-0000-0000-0000-000000000002', 'Maria Silva Santos', '234.567.890-11', 'Engenheira Civil', 'EMPLOYEE', 'maria@obra.com', '(84) 99900-1002', 120.00, '2023-08-01'),
    ('e0000001-0000-0000-0000-000000000003', 'Pedro Oliveira', '345.678.901-22', 'Pedreiro', 'EMPLOYEE', null, '(84) 99900-1003', 28.00, '2025-01-10'),
    ('e0000001-0000-0000-0000-000000000004', 'Terraplanagem Norte Ltda', '56.789.012/0001-34', 'Terraplanagem', 'CONTRACTOR', 'terra@norte.com', '(84) 3300-4000', null, '2026-01-20'),
    ('e0000001-0000-0000-0000-000000000005', 'Elétrica Potiguar ME', '67.890.123/0001-45', 'Instalações Elétricas', 'CONTRACTOR', 'eletrica@potiguar.com', '(84) 3300-5000', null, '2026-02-01');

-- === FORNECEDORES (extras) ===
INSERT INTO supplier (id, code, name, trade_name, tax_id, email, phone, rating, active, created_at, updated_at) VALUES
    ('50000001-0000-0000-0000-000000000001', 'SUP-001', 'Cimento Nassau S.A.', 'Nassau', '11.222.333/0001-44', 'vendas@nassau.com.br', '(81) 3400-1000', 5, true, now(), now()),
    ('50000001-0000-0000-0000-000000000002', 'SUP-002', 'Gerdau Aços Longos', 'Gerdau', '22.333.444/0001-55', 'comercial@gerdau.com.br', '(51) 3400-2000', 5, true, now(), now()),
    ('50000001-0000-0000-0000-000000000003', 'SUP-003', 'Madeireira Tropical', 'Tropical', '33.444.555/0001-66', 'vendas@tropical.com.br', '(84) 3300-3000', 4, true, now(), now()),
    ('50000001-0000-0000-0000-000000000004', 'SUP-004', 'Hidráulica Total Ltda', 'Hidráulica Total', '44.555.666/0001-77', 'orcamento@hidraulica.com.br', '(84) 3300-4000', 4, true, now(), now());

-- === FORMAS DE PAGAMENTO ===
INSERT INTO payment_method (id, name, installments) VALUES
    ('00000001-0000-0000-0000-000000000001', 'À Vista', 1),
    ('00000001-0000-0000-0000-000000000002', '30/60/90 dias', 3),
    ('00000001-0000-0000-0000-000000000003', 'Boleto 28 dias', 1),
    ('00000001-0000-0000-0000-000000000004', '30/60 dias', 2);

-- === CONTAS BANCÁRIAS ===
INSERT INTO bank_account (id, bank_code, bank_name, agency, account_number, account_type, holder_name) VALUES
    ('ba000001-0000-0000-0000-000000000001', '001', 'Banco do Brasil', '3456-7', '12345-6', 'CHECKING', 'SinapiPRO Engenharia Ltda'),
    ('ba000001-0000-0000-0000-000000000002', '104', 'Caixa Econômica', '0891', '00012345-0', 'CHECKING', 'SinapiPRO Engenharia Ltda');

-- === EQUIPAMENTOS ===
INSERT INTO equipment (id, code, name, type, brand, model, year, license_plate, hourly_cost, status, current_hours, current_km, next_maintenance_hours, next_maintenance_date, created_at, updated_at) VALUES
    ('e1000001-0000-0000-0000-000000000001', 'EQ-001', 'Retroescavadeira CAT 416F2', 'RETROESCAVADEIRA', 'Caterpillar', '416F2', 2022, null, 180.00, 'AVAILABLE', 1250.00, 0, 1500.00, '2026-07-01', now(), now()),
    ('e1000001-0000-0000-0000-000000000002', 'EQ-002', 'Betoneira 400L', 'BETONEIRA', 'CSM', 'CS 400', 2023, null, 35.00, 'AVAILABLE', 800.00, 0, 1000.00, null, now(), now()),
    ('e1000001-0000-0000-0000-000000000003', 'EQ-003', 'Caminhão Basculante', 'CAMINHAO', 'Mercedes-Benz', 'Atego 1719', 2021, 'RNX-4B56', 95.00, 'AVAILABLE', 0, 45000.00, null, '2026-08-15', now(), now());

-- === CONTRATO (vinculado ao projeto 1) ===
INSERT INTO contract (id, budget_id, supplier_id, number, description, original_value, retention_pct, status, start_date, end_date, created_at, updated_at) VALUES
    ('c1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000002',
     'CTR-2026-001', 'Fornecimento e montagem de estrutura metálica - Blocos A e B',
     1850000.00, 0.05, 'ACTIVE', '2026-02-01', '2026-10-30', now(), now());

-- === MEDIÇÕES (vinculadas ao budget do projeto 1) ===
INSERT INTO measurement (id, budget_id, number, period_start, period_end, status, retention_pct, created_at, updated_at) VALUES
    ('ae000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 1, '2026-02-01', '2026-02-28', 'PAID', 0.05, now(), now()),
    ('ae000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 2, '2026-03-01', '2026-03-31', 'APPROVED', 0.05, now(), now()),
    ('ae000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 3, '2026-04-01', '2026-04-30', 'SUBMITTED', 0.05, now(), now()),
    ('ae000001-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 4, '2026-05-01', '2026-05-31', 'DRAFT', 0.05, now(), now());

-- === ITENS DA MEDIÇÃO ===
INSERT INTO measurement_item (id, measurement_id, description, quantity, unit_price) VALUES
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000001', 'Fundação - Estacas', 120.00, 850.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000001', 'Fundação - Blocos', 45.00, 1200.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000002', 'Estrutura - Pilares Bloco A', 32.00, 2800.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000002', 'Estrutura - Vigas Bloco A', 64.00, 1500.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000003', 'Estrutura - Lajes Bloco A', 16.00, 4200.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000004', 'Alvenaria - Bloco A Térreo', 280.00, 95.00);

-- === CRONOGRAMA (atividades do projeto 1) ===
INSERT INTO schedule_activity (id, budget_id, name, planned_start, planned_end, weight, progress_pct, sort_order, created_at, updated_at) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Terraplanagem', '2026-01-15', '2026-02-15', 0.05, 100, 1, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Fundações', '2026-02-01', '2026-04-15', 0.15, 100, 2, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Estrutura', '2026-03-15', '2026-08-30', 0.25, 45, 3, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Alvenaria', '2026-06-01', '2026-10-30', 0.15, 10, 4, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Instalações Elétricas', '2026-07-01', '2026-12-15', 0.12, 0, 5, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Instalações Hidráulicas', '2026-07-15', '2026-12-30', 0.10, 0, 6, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Revestimentos', '2026-09-01', '2027-03-30', 0.10, 0, 7, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Pintura e Acabamentos', '2027-02-01', '2027-05-30', 0.05, 0, 8, now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Área de Lazer', '2027-03-01', '2027-06-15', 0.03, 0, 9, now(), now());

-- === DIÁRIO DE OBRA ===
INSERT INTO daily_log (id, budget_id, log_date, weather_morning, weather_afternoon, observations, created_at, updated_at) VALUES
    ('d1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '2026-05-09', 'Ensolarado', 'Parcialmente nublado', 'Concretagem do 3o pavimento Bloco A concluída. Equipe de armação iniciou 4o pavimento.', now(), now()),
    ('d1000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', '2026-05-08', 'Nublado', 'Chuvoso', 'Chuva no período da tarde paralisou concretagem por 2h. Equipe realocada para armação interna.', now(), now());

INSERT INTO daily_log_labor (id, daily_log_id, worker_name, role, hours) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'João Carlos Pereira', 'Mestre de Obras', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Pedro Oliveira', 'Pedreiro', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Antônio Souza', 'Armador', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Francisco Lima', 'Carpinteiro', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'José Ferreira', 'Servente', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'João Carlos Pereira', 'Mestre de Obras', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'Pedro Oliveira', 'Pedreiro', 6.00);

INSERT INTO daily_log_equipment (id, daily_log_id, equipment_name, hours_used, hours_idle) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Grua Torre 40m', 8.00, 0.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Betoneira 400L', 6.00, 2.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'Grua Torre 40m', 6.00, 2.00);

INSERT INTO daily_log_occurrence (id, daily_log_id, type, description) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'PARALISACAO', 'Chuva forte das 14h às 16h. Concretagem suspensa por segurança.');

-- === CONTAS A PAGAR ===
INSERT INTO payable (id, budget_id, supplier_id, description, amount, due_date, status, category, created_at, updated_at) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000001', 'NF 4521 - Cimento CP-II (500 sacos)', 22500.00, '2026-05-15', 'PENDING', 'Material', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000002', 'NF 8901 - Aço CA-50 (8 ton)', 56000.00, '2026-05-20', 'PENDING', 'Material', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', null, 'Aluguel de grua - Maio/2026', 18000.00, '2026-05-10', 'PENDING', 'Equipamento', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', null, 'Folha de pagamento - Abril/2026', 85000.00, '2026-05-05', 'PAID', 'Mão de Obra', now(), now());

-- === CONTAS A RECEBER ===
INSERT INTO receivable (id, budget_id, description, amount, due_date, status, category, created_at, updated_at) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Medição #1 - Fundações', 102000.00, '2026-03-30', 'PAID', 'Medição', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Medição #2 - Estrutura', 185600.00, '2026-04-30', 'PAID', 'Medição', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Medição #3 - Estrutura (cont.)', 67200.00, '2026-05-30', 'PENDING', 'Medição', now(), now());

-- === PEDIDO DE COMPRA ===
INSERT INTO purchase_request (id, budget_id, description, quantity, unit, status, requested_by, created_at, updated_at) VALUES
    ('00000002-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Cimento CP-II 50kg', 500.00, 'saco', 'CLOSED', 'João Carlos', now(), now()),
    ('00000002-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Aço CA-50 10mm', 8000.00, 'kg', 'OPEN', 'Maria Silva', now(), now());

INSERT INTO purchase_order (id, budget_id, supplier_id, number, description, quantity, unit_price, status, expected_delivery_date, created_at, updated_at) VALUES
    ('00000003-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000001',
     'PED-2026-001', 'Cimento CP-II 50kg', 500.00, 45.00, 'RECEIVED', '2026-05-01', now(), now()),
    ('00000003-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000002',
     'PED-2026-002', 'Aço CA-50 10mm (8 ton)', 8000.00, 7.00, 'APPROVED', '2026-05-18', now(), now()),
    ('00000003-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000004',
     'PED-2026-003', 'Tubos PVC 100mm', 200.00, 32.50, 'PENDING', '2026-05-25', now(), now());

-- === ESTOQUE ===
INSERT INTO stock_item (id, budget_id, description, unit, current_quantity, min_quantity, location, created_at, updated_at) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Cimento CP-II 50kg', 'saco', 120.00, 50.00, 'Almoxarifado A', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Aço CA-50 10mm', 'kg', 2500.00, 1000.00, 'Pátio de Aço', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Tijolo Cerâmico 9x14x19', 'un', 8000.00, 5000.00, 'Almoxarifado B', now(), now()),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Areia Média', 'm3', 15.00, 20.00, 'Pátio', now(), now());

-- === SEGURANÇA ===
INSERT INTO safety_checklist_template (id, name, category, items, active, created_at) VALUES
    ('50000002-0000-0000-0000-000000000001', 'Inspeção Diária de Canteiro', 'CANTEIRO',
     '[{"item":"EPIs em uso","required":true},{"item":"Sinalização adequada","required":true},{"item":"Proteção periférica","required":true},{"item":"Ordem e limpeza","required":true},{"item":"Extintores acessíveis","required":true}]',
     true, now());

INSERT INTO safety_inspection (id, budget_id, template_id, inspector, inspection_date, status, results, notes, created_at) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '50000002-0000-0000-0000-000000000001',
     'Técnico Marcos Almeida', '2026-05-09', 'PASS',
     '[{"item":"EPIs em uso","ok":true},{"item":"Sinalização adequada","ok":true},{"item":"Proteção periférica","ok":true},{"item":"Ordem e limpeza","ok":true},{"item":"Extintores acessíveis","ok":true}]',
     'Canteiro em conformidade', now());

-- === COMERCIAL (Empreendimento) ===
INSERT INTO development (id, name, address, city, state, total_units, status, launch_date, created_at, updated_at) VALUES
    ('d0000001-0000-0000-0000-000000000001', 'Residencial Parque das Flores', 'Rua das Acácias, 500', 'Natal', 'RN', 64, 'LAUNCHED', '2026-01-10', now(), now());

INSERT INTO development_unit (id, development_id, code, type, area, price, status, floor, bedrooms) VALUES
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'A-101', 'Apartamento', 72.00, 380000.00, 'SOLD', 1, 2),
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'A-102', 'Apartamento', 72.00, 380000.00, 'SOLD', 1, 2),
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'A-201', 'Apartamento', 72.00, 395000.00, 'RESERVED', 2, 2),
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'A-202', 'Apartamento', 72.00, 395000.00, 'AVAILABLE', 2, 2),
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'A-301', 'Apartamento', 85.00, 450000.00, 'AVAILABLE', 3, 3),
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'B-101', 'Apartamento', 65.00, 340000.00, 'AVAILABLE', 1, 2),
    (gen_random_uuid(), 'd0000001-0000-0000-0000-000000000001', 'B-102', 'Apartamento', 65.00, 340000.00, 'AVAILABLE', 1, 2);

-- === PÓS-VENDA (Chamados) ===
INSERT INTO service_ticket (id, client_name, category, description, priority, status, assigned_to, due_date, opened_at) VALUES
    (gen_random_uuid(), 'Carlos Mendes (A-101)', 'Hidráulica', 'Vazamento no registro do banheiro social', 'HIGH', 'IN_PROGRESS', 'Hidráulica Total', '2026-05-15', now() - interval '3 days'),
    (gen_random_uuid(), 'Ana Paula (A-102)', 'Elétrica', 'Tomada da cozinha sem funcionar', 'MEDIUM', 'OPEN', null, '2026-05-20', now() - interval '1 day'),
    (gen_random_uuid(), 'Roberto Silva (A-201)', 'Pintura', 'Mancha de umidade na parede do quarto', 'LOW', 'OPEN', null, '2026-05-25', now());
