insert into budget (id, code, title, customer_name, total_amount, status, start_date, end_date, metadata, created_at, updated_at)
values
    ('11111111-1111-1111-1111-111111111111', 'BUD-2026-001', 'Condominio Atlass', 'Grupo Atlass', 850000.00, 'APPROVED', date '2026-01-10', date '2026-12-18', '{"segment":"residential","city":"Natal"}', now(), now()),
    ('22222222-2222-2222-2222-222222222222', 'BUD-2026-002', 'Hospital Horizonte', 'Rede Horizonte', 2450000.00, 'IN_EXECUTION', date '2026-02-03', date '2027-06-28', '{"segment":"healthcare","city":"Recife"}', now(), now());

insert into supplier (id, code, name, trade_name, tax_id, email, phone, rating, active, created_at, updated_at)
values
    ('33333333-3333-3333-3333-333333333333', 'SUP-2026-001', 'Aco Forte Industrial', 'Aco Forte', '12.345.678/0001-99', 'contato@acoforte.dev', '+55 84 4000-1000', 5, true, now(), now()),
    ('44444444-4444-4444-4444-444444444444', 'SUP-2026-002', 'Concreto Norte Engenharia', 'Concreto Norte', '98.765.432/0001-44', 'fornecedores@concretonorte.dev', '+55 81 4000-2000', 4, true, now(), now());

insert into invoice (id, number, budget_id, supplier_id, amount, issue_date, due_date, status, notes, created_at, updated_at)
values
    ('55555555-5555-5555-5555-555555555555', 'INV-2026-001', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 120000.00, date '2026-03-04', date '2026-03-29', 'PAID', 'Lote estrutural', now(), now()),
    ('66666666-6666-6666-6666-666666666666', 'INV-2026-002', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', 320000.00, date '2026-04-09', date '2026-04-30', 'OVERDUE', 'Concretagem bloco B', now(), now());
