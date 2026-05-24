-- V2: Dados de demonstração — permite navegar por todo o sistema sem cadastrar nada

-- Tenant demo
INSERT INTO tenant (id, name, slug) VALUES ('00000000-0000-0000-0000-000000000001', 'Construtora Demo Ltda', 'demo');

-- Settings
INSERT INTO app_settings VALUES ('DEFAULT_STATE', 'SC'), ('DEFAULT_REFERENCE_MONTH', '2026-05-01'), ('DEFAULT_DESONERATED', 'false');

-- Unidades de medida
INSERT INTO unit_of_measure (symbol, description) VALUES ('m²','Metro quadrado'),('m³','Metro cúbico'),('m','Metro linear'),('kg','Quilograma'),('un','Unidade'),('vb','Verba'),('h','Hora'),('l','Litro'),('t','Tonelada');

-- Índices econômicos
INSERT INTO monetary_index (id, code, name, source) VALUES
  ('b0000000-0000-0000-0000-000000000001','INCC','Índice Nacional de Custo da Construção','FGV'),
  ('b0000000-0000-0000-0000-000000000002','IGPM','Índice Geral de Preços do Mercado','FGV'),
  ('b0000000-0000-0000-0000-000000000003','CUB','Custo Unitário Básico','SINDUSCON');

INSERT INTO monetary_index_value (index_id, reference_month, value, accumulated) VALUES
  ('b0000000-0000-0000-0000-000000000001','2026-01-01',0.51,0.51),
  ('b0000000-0000-0000-0000-000000000001','2026-02-01',0.34,0.85),
  ('b0000000-0000-0000-0000-000000000001','2026-03-01',0.39,1.24),
  ('b0000000-0000-0000-0000-000000000001','2026-04-01',0.42,1.67);

-- Tipos de hora
INSERT INTO hour_type (code, name, multiplier) VALUES ('NORMAL','Hora Normal',1.00),('HE50','Hora Extra 50%',1.50),('HE100','Hora Extra 100%',2.00),('NOTURNA','Hora Noturna',1.20);

-- Cidades
INSERT INTO city (name, state, ibge_code) VALUES ('Florianópolis','SC','4205407'),('São Paulo','SP','3550308'),('Curitiba','PR','4106902'),('Joinville','SC','4209102');

-- ============================================================
-- DADOS POR TENANT (empresa demo)
-- ============================================================

-- Clientes
INSERT INTO client (id, tenant_id, name, document, email, phone, city, state, person_type, address, neighborhood, postal_code, whatsapp) VALUES
  ('c0000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','Incorporadora Atlântica S.A.','33.542.689/0001-45','contato@atlanticainc.com.br','(48) 3333-1111','Florianópolis','SC','PJ','Av. Beira Mar Norte, 2500','Centro','88015-200','(48) 99100-2500'),
  ('c0000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','João Carlos Mendes','529.982.247-25','joao.mendes@gmail.com','(48) 99876-5432','Florianópolis','SC','PF','Rua Lauro Linhares, 1234','Trindade','88036-002','(48) 99876-5432'),
  ('c0000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000001','Prefeitura Municipal de Joinville','83.169.623/0001-10','licitacao@joinville.sc.gov.br','(47) 3431-3000','Joinville','SC','PJ','Av. Hermann August Lepper, 10','Centro','89221-005',NULL),
  ('c0000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000001','Mariana Oliveira Santos','847.563.219-04','mariana.santos@outlook.com','(48) 99654-3210','São José','SC','PF','Rua Koesa, 456','Kobrasol','88102-310','(48) 99654-3210'),
  ('c0000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000001','Construtora Horizonte Ltda','71.284.093/0001-56','comercial@horizonteconstrutora.com.br','(47) 3422-8800','Blumenau','SC','PJ','Rua XV de Novembro, 800','Centro','89010-000','(47) 99900-8800');

-- Fornecedores
INSERT INTO supplier (id, tenant_id, code, name, tax_id, email, phone, category, qualification_status, payment_term_days, lead_time_days, city, state, rating, active, trade_name, contact_name, whatsapp) VALUES
  ('d0000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','FORN-001','Votorantim Cimentos S.A.','60.892.403/0001-27','vendas.sc@vcimentos.com.br','(11) 4003-1234','MATERIAL','APPROVED',30,5,'Votorantim','SP',9,true,'Votorantim Cimentos','Ricardo Ferreira','(11) 99800-1234'),
  ('d0000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','FORN-002','Gerdau Aços Longos S.A.','07.358.761/0001-69','comercial.sul@gerdau.com.br','(51) 3323-2000','MATERIAL','APPROVED',28,7,'Charqueadas','RS',8,true,'Gerdau','Marcos Vinícius','(51) 99700-2000'),
  ('d0000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000001','FORN-003','Elétrica Luminar Ltda','18.476.903/0001-82','orcamento@luminar.com.br','(48) 3222-5678','SERVICO','APPROVED',15,3,'Florianópolis','SC',7,true,'Luminar Elétrica','Ana Paula Costa','(48) 99600-5678'),
  ('d0000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000001','FORN-004','Madeireira Catarinense Ltda','42.891.567/0001-38','vendas@madeireiracatarinense.com.br','(47) 3433-9900','MATERIAL','APPROVED',21,10,'Joinville','SC',6,true,'Madeireira Catarinense','José Antônio','(47) 99500-9900'),
  ('d0000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000001','FORN-005','Hidráulica Sul Comércio','56.234.178/0001-91','contato@hidraulicasul.com.br','(48) 3244-7700','MATERIAL','PENDING',30,5,'Palhoça','SC',5,true,'Hidráulica Sul','Fernando Lima','(48) 99400-7700');

-- Funcionários
INSERT INTO employee (id, tenant_id, employee_code, name, document, role, specialty, type, employment_status, email, phone, hourly_rate, admission_date, mobile_phone, city, state, department, salary) VALUES
  ('e0000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','EMP-001','Carlos Eduardo da Silva','041.678.329-50','Engenheiro Civil','Estruturas','EMPLOYEE','ACTIVE','carlos.silva@demo.com.br','(48) 3333-0001',85.00,'2024-01-15','(48) 99101-0001','Florianópolis','SC','Engenharia',14500.00),
  ('e0000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','EMP-002','Maria Fernanda Costa','283.947.561-82','Mestre de Obras','Execução','EMPLOYEE','ACTIVE','maria.costa@demo.com.br','(48) 3333-0002',45.00,'2023-06-01','(48) 99102-0002','São José','SC','Produção',7800.00),
  ('e0000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000001','EMP-003','Roberto de Almeida Júnior','394.158.672-93','Técnico Segurança','SST','EMPLOYEE','ACTIVE','roberto.almeida@demo.com.br','(48) 3333-0003',55.00,'2024-03-10','(48) 99103-0003','Palhoça','SC','Segurança',9200.00),
  ('e0000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000001','EMP-004','Antônio Marcos Pereira','156.789.234-67','Pedreiro','Alvenaria','EMPLOYEE','ACTIVE',NULL,'(48) 3333-0004',28.00,'2024-02-01','(48) 99104-0004','Biguaçu','SC','Produção',4800.00),
  ('e0000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000001','EMP-005','Lucas Gabriel Souza','267.891.345-78','Eletricista','Instalações','EMPLOYEE','ACTIVE',NULL,'(48) 3333-0005',35.00,'2024-04-15','(48) 99105-0005','Florianópolis','SC','Produção',6000.00);

-- Conta bancária
INSERT INTO bank_account (id, tenant_id, bank_code, bank_name, agency, account_number, holder_name, initial_balance) VALUES
  ('f0000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','001','Banco do Brasil','1234-5','56789-0','Construtora Demo Ltda',150000.00);

-- Projetos (Obras)
INSERT INTO project (id, tenant_id, code, name, customer_name, customer_document, address, city, state, responsible_engineer, start_date, expected_end_date, status, total_area, total_budget) VALUES
  ('10000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','OBR-2024-001','Residencial Jardim Europa','Incorporadora Atlântica S.A.','12.345.678/0001-90','Rua das Palmeiras, 500','Florianópolis','SC','Carlos Eduardo Silva','2024-06-01','2025-12-31','IN_PROGRESS',4500.00,8500000.00),
  ('10000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','OBR-2024-002','Escola Municipal Joinville','Prefeitura Municipal de Joinville','83.169.623/0001-10','Av. Brasil, 1200','Joinville','SC','Carlos Eduardo Silva','2025-01-15','2026-06-30','PLANNING',2200.00,4200000.00);

-- Composições SINAPI (exemplo)
INSERT INTO composition (id, tenant_id, sinapi_code, description, unit, group_name, origin) VALUES
  ('20000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','73948/2','Limpeza mecanizada de terreno','m²','SERVIÇOS PRELIMINARES','SINAPI'),
  ('20000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','74209/1','Tapume de madeira compensada','m²','SERVIÇOS PRELIMINARES','SINAPI'),
  ('20000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000001','74157/3','Escavação manual de vala','m³','INFRAESTRUTURA','SINAPI'),
  ('20000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000001','92263','Concreto fck=25MPa bombeado','m³','SUPERESTRUTURA','SINAPI'),
  ('20000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000001','92791','Alvenaria de bloco cerâmico 14cm','m²','ALVENARIA','SINAPI'),
  ('20000000-0000-0000-0000-000000000006','00000000-0000-0000-0000-000000000001','87879','Revestimento cerâmico piso','m²','REVESTIMENTO','SINAPI'),
  ('20000000-0000-0000-0000-000000000007','00000000-0000-0000-0000-000000000001','93358','Pintura látex PVA 2 demãos','m²','PINTURA','SINAPI'),
  ('20000000-0000-0000-0000-000000000008','00000000-0000-0000-0000-000000000001','91926','Instalação elétrica ponto luz','un','ELÉTRICA','SINAPI');

-- Orçamento completo (Obra 1)
INSERT INTO budget (id, tenant_id, code, title, customer_name, total_amount, status, active, start_date, end_date, project_id, state, reference_date) VALUES
  ('30000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','ORC-2024-001','Orçamento Residencial Jardim Europa','Incorporadora Atlântica S.A.',8500000.00,'IN_EXECUTION',true,'2024-06-01','2025-12-31','10000000-0000-0000-0000-000000000001','SC','2026-03-01');

-- Etapas do orçamento
INSERT INTO budget_stage (id, budget_id, name, sort_order) VALUES
  ('31000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','01. Serviços Preliminares',1),
  ('31000000-0000-0000-0000-000000000002','30000000-0000-0000-0000-000000000001','02. Infraestrutura',2),
  ('31000000-0000-0000-0000-000000000003','30000000-0000-0000-0000-000000000001','03. Superestrutura',3),
  ('31000000-0000-0000-0000-000000000004','30000000-0000-0000-0000-000000000001','04. Alvenaria',4),
  ('31000000-0000-0000-0000-000000000005','30000000-0000-0000-0000-000000000001','05. Revestimento',5),
  ('31000000-0000-0000-0000-000000000006','30000000-0000-0000-0000-000000000001','06. Pintura',6),
  ('31000000-0000-0000-0000-000000000007','30000000-0000-0000-0000-000000000001','07. Instalações Elétricas',7);

-- Itens do orçamento
INSERT INTO budget_item (stage_id, composition_id, quantity, unit_cost, bdi_pct) VALUES
  ('31000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000001',4500.00,25.30,0.2500),
  ('31000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000002',320.00,70.00,0.2500),
  ('31000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000003',1200.00,85.40,0.2500),
  ('31000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000004',850.00,520.00,0.2500),
  ('31000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000005',6200.00,78.50,0.2500),
  ('31000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000006',3800.00,95.00,0.2500),
  ('31000000-0000-0000-0000-000000000006','20000000-0000-0000-0000-000000000007',8500.00,18.50,0.2500),
  ('31000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000008',420.00,145.00,0.2500);

-- Medição (1ª medição da obra)
INSERT INTO measurement (id, tenant_id, budget_id, project_id, number, period_start, period_end, status, total_amount) VALUES
  ('40000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001',1,'2024-06-01','2024-06-30','APPROVED',425000.00),
  ('40000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001',2,'2024-07-01','2024-07-31','SUBMITTED',680000.00);

-- Pedido de compra
INSERT INTO purchase_order (id, tenant_id, budget_id, supplier_id, project_id, number, description, quantity, unit_price, status, expected_delivery_date) VALUES
  ('50000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','d0000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','PED-001','Cimento CP-II 50kg',2000,32.50,'APPROVED','2024-07-15'),
  ('50000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','d0000000-0000-0000-0000-000000000002','10000000-0000-0000-0000-000000000001','PED-002','Aço CA-50 10mm',15000,8.90,'PENDING','2024-07-20');

-- Contas a pagar
INSERT INTO payable (id, tenant_id, budget_id, project_id, supplier_id, description, amount, due_date, status, category) VALUES
  ('60000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','d0000000-0000-0000-0000-000000000001','NF 12345 - Cimento',65000.00,'2024-08-15','PENDING','MATERIAL'),
  ('60000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','d0000000-0000-0000-0000-000000000002','NF 67890 - Aço',133500.00,'2024-08-20','PENDING','MATERIAL'),
  ('60000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','d0000000-0000-0000-0000-000000000003','NF 11111 - Instalação elétrica',45000.00,'2024-07-30','PAID','SERVICO');

-- Contas a receber
INSERT INTO receivable (id, tenant_id, budget_id, project_id, description, amount, due_date, status, category) VALUES
  ('70000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','Medição #1 - Jun/2024',425000.00,'2024-07-15','PAID','MEDICAO'),
  ('70000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','Medição #2 - Jul/2024',680000.00,'2024-08-15','PENDING','MEDICAO');

-- Movimentação bancária
INSERT INTO bank_transaction (tenant_id, bank_account_id, transaction_date, type, amount, description, reconciled) VALUES
  ('00000000-0000-0000-0000-000000000001','f0000000-0000-0000-0000-000000000001','2024-07-15','CREDIT',425000.00,'Recebimento Medição #1',true),
  ('00000000-0000-0000-0000-000000000001','f0000000-0000-0000-0000-000000000001','2024-07-30','DEBIT',45000.00,'Pgto NF 11111 - Elétrica',true),
  ('00000000-0000-0000-0000-000000000001','f0000000-0000-0000-0000-000000000001','2024-08-05','DEBIT',12500.00,'Folha de pagamento Jul/24',false),
  ('00000000-0000-0000-0000-000000000001','f0000000-0000-0000-0000-000000000001','2024-08-10','DEBIT',8700.00,'Aluguel equipamentos',false);

-- Diário de obra
INSERT INTO daily_log (id, tenant_id, project_id, log_date, weather, temperature_min, temperature_max, notes) VALUES
  ('80000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','2024-07-15','SUNNY',18.0,26.5,'Concretagem laje 2º pavimento concluída. 42m³ lançados.'),
  ('80000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','2024-07-16','CLOUDY',16.0,22.0,'Desforma parcial. Início alvenaria bloco A.');

-- Notificações
INSERT INTO notification (tenant_id, user_id, title, message, type, entity_type, entity_id) VALUES
  ('00000000-0000-0000-0000-000000000001','demo','Medição #2 submetida','A medição de Jul/2024 aguarda aprovação','MEASUREMENT','measurement','40000000-0000-0000-0000-000000000002'),
  ('00000000-0000-0000-0000-000000000001','demo','Pedido PED-002 pendente','Pedido de aço aguarda aprovação','PROCUREMENT','purchase_order','50000000-0000-0000-0000-000000000002'),
  ('00000000-0000-0000-0000-000000000001','demo','Conta vencendo','NF 12345 vence em 15/08','FINANCE','payable','60000000-0000-0000-0000-000000000001');

-- Empreendimento + Unidades (Comercial)
INSERT INTO development (id, tenant_id, name, address, city, state, total_units, status) VALUES
  ('90000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000001','Residencial Jardim Europa','Rua das Palmeiras, 500','Florianópolis','SC',24,'ACTIVE');

INSERT INTO development_unit (tenant_id, development_id, identifier, type, private_area, price, status) VALUES
  ('00000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Apto 101','APARTMENT',72.50,450000.00,'AVAILABLE'),
  ('00000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Apto 102','APARTMENT',72.50,460000.00,'AVAILABLE'),
  ('00000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Apto 201','APARTMENT',85.00,520000.00,'SOLD'),
  ('00000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Apto 202','APARTMENT',85.00,530000.00,'RESERVED'),
  ('00000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Apto 301','APARTMENT',110.00,680000.00,'AVAILABLE'),
  ('00000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Cobertura 401','PENTHOUSE',180.00,1200000.00,'AVAILABLE');

-- Ordem de Serviço (Atendimento)
INSERT INTO service_ticket (tenant_id, client_name, category, description, priority, status, assigned_to, due_date) VALUES
  ('00000000-0000-0000-0000-000000000001','João Carlos Mendes','Garantia','Infiltração no banheiro do apto 201','HIGH','OPEN',NULL,'2026-06-01'),
  ('00000000-0000-0000-0000-000000000001','Maria Silva','Manutenção','Troca de fechadura porta principal','LOW','IN_PROGRESS','Roberto Almeida','2026-06-15'),
  ('00000000-0000-0000-0000-000000000001','Pedro Santos','Vistoria','Vistoria de entrega apto 102','MEDIUM','RESOLVED','Carlos Eduardo Silva','2026-05-20');
