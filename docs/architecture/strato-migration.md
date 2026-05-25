# Strato → SinapiPRO — Gap Analysis Completa

> Gerado: 2026-05-23 | Fonte: java-strato (JSF/Hibernate)
> Destino: sinapiPRO/api (Spring Boot 4)

---

## 1. FLUXOS DE PROCESSO

### 1.1 Contas a Pagar (Financeiro)

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐    ┌──────────────┐
│ NF Entrada  │───▶│ Despesa      │───▶│ Autorização     │───▶│ Pagamento    │
│ (invoice)   │    │ Diversa      │    │ Pagamento       │    │ Efetivado    │
└─────────────┘    └──────────────┘    └─────────────────┘    └──────────────┘
                         │                                           │
                         ▼                                           ▼
                   ┌──────────────┐                          ┌──────────────┐
                   │ Parcelamento │                          │ Emissão      │
                   │ Automático   │                          │ Cheque       │
                   └──────────────┘                          └──────────────┘
                                                                    │
                                                                    ▼
                                                             ┌──────────────┐
                                                             │ Mov. Bancário│
                                                             │ (conciliação)│
                                                             └──────────────┘

Estados da Despesa: ABERTA → AUTORIZADA → PAGA_PARCIAL → PAGA → CANCELADA
Retenções: ISS, INSS, IR, PIS/COFINS calculados na entrada da NF
Adiantamento: Fornecedor recebe antecipado → abate nas próximas NFs
```

**Telas Strato:** FrDespesaDiversa (18KB), FrAutorizacaoPagamento (7KB), FrEfetuaPagamento (11KB), FrEmissaoCheques (10KB), FrMovimentoBancario (7KB), FrAdiantamentosFornecedor (11KB)

### 1.2 Contas a Receber (Financeiro)

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐    ┌──────────────┐
│ Medição     │───▶│ Faturamento  │───▶│ Boleto/Ficha    │───▶│ Recebimento  │
│ Aprovada    │    │ (gera título)│    │ Compensação     │    │ (baixa)      │
└─────────────┘    └──────────────┘    └─────────────────┘    └──────────────┘
                         │                                           │
                         ▼                                           ▼
                   ┌──────────────┐                          ┌──────────────┐
                   │ Parcelas     │                          │ Baixa Parcial│
                   │ (juros/multa)│                          │ + Juros/Multa│
                   └──────────────┘                          └──────────────┘

Estados: ABERTA → EMITIDA → RECEBIDA_PARCIAL → RECEBIDA → CANCELADA → RENEGOCIADA
Integração bancária: Exportar remessa CNAB → Importar retorno → Baixa automática
```

**Telas Strato:** FrRecebimentoReceitas (7KB), FrEmissaoFichaCompensacao (11KB), FrQuitacaoParcelas (5KB), FrCancelaBoletoBancario (8KB)

### 1.3 Suprimentos (Ciclo Completo)

```
┌──────────┐   ┌───────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│Requisição│──▶│Autorização│──▶│ Cotação   │──▶│ Análise  │──▶│ Pedido   │
│(por obra)│   │(por alçada)│  │(3+ fornec)│  │Comparativa│  │ Compra   │
└──────────┘   └───────────┘   └──────────┘   └──────────┘   └──────────┘
                                     │                              │
                                     ▼                              ▼
                              ┌──────────┐                   ┌──────────┐
                              │Email p/  │                   │Recebimento│
                              │Fornecedor│                   │(NF+estoque)│
                              └──────────┘                   └──────────┘
                                                                   │
                                                                   ▼
                                                             ┌──────────┐
                                                             │Apropriação│
                                                             │(custo obra)│
                                                             └──────────┘

Requisição: RASCUNHO → AGUARDANDO_AUTH → AUTORIZADA → EM_COTAÇÃO → ATENDIDA
Pedido: GERADO → ENVIADO → PARCIAL → RECEBIDO → CANCELADO
Controles: Limite compra por obra, cronograma de compras, pedidos em atraso
```

**Telas Strato:** 21 subdiretórios em suprimentos/ (gerarrequisicao, autorizarRequisicao, cotacao, cotacaoEmail, analiseCotacao, respostaCotacao, pedidos, distribuirpedido, pedidosEmAtraso, pedidosBaixa, limitecompra, cronograma, etc.)

### 1.4 Vendas Imobiliárias (Comercial)

```
┌──────────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│Empreendimento│──▶│ Unidades │──▶│ Tabela   │──▶│ Proposta │
│(development) │   │(apartam.)│   │ Preços   │   │ Comercial│
└──────────────┘   └──────────┘   └──────────┘   └──────────┘
                                                       │
                        ┌──────────────────────────────┘
                        ▼
                  ┌──────────┐   ┌──────────┐   ┌──────────┐
                  │ Contrato │──▶│ Parcelas │──▶│ Reajuste │
                  │ de Venda │   │(Price/SAC)│  │(INCC/IGPM)│
                  └──────────┘   └──────────┘   └──────────┘
                        │              │
                        ▼              ▼
                  ┌──────────┐   ┌──────────┐
                  │ Cessão/  │   │ Boleto/  │
                  │ Distrato │   │ Cobrança │
                  └──────────┘   └──────────┘
                        │
                        ▼
                  ┌──────────┐   ┌──────────┐
                  │ Comissão │──▶│ Repasse  │
                  │ Corretor │   │ Bancário │
                  └──────────┘   └──────────┘

Contrato: PROPOSTA → APROVADO → ASSINADO → VIGENTE → QUITADO | DISTRATADO | CEDIDO
Parcelas: FUTURA → VENCIDA → PAGA → RENEGOCIADA
Reajuste: Mensal por índice (INCC, IGPM, CUB) com data-base
```

**Telas Strato:** 14 subdiretórios em comercial/ (imovel, unidade, tabelaprecos, vendasIncorporacao, comissao, Reparcelamento, taxascontratos, banco, etc.)

### 1.5 Mão de Obra

```
┌──────────────┐   ┌──────────┐   ┌──────────┐   ┌──────────────┐
│ Cadastro     │──▶│Apontamento│──▶│ Banco de │──▶│ Encerramento │
│ Funcionário  │   │ de Horas │   │  Horas   │   │ Competência  │
└──────────────┘   └──────────┘   └──────────┘   └──────────────┘
       │                 │                              │
       ▼                 ▼                              ▼
┌──────────────┐   ┌──────────┐                  ┌──────────┐
│ EPI/Exames   │   │Hora Extra│                  │ Folha    │
│ Treinamentos │   │(cópia)   │                  │ Resumo   │
└──────────────┘   └──────────┘                  └──────────┘

Competência: ABERTA → FECHADA (não permite mais lançamentos)
Apontamento: por obra + etapa + tipo hora (normal, extra 50%, extra 100%, noturna)
Banco horas: crédito/débito com saldo acumulado
```

**Telas Strato:** 8 subdiretórios em maodeobra/ (cadastros, movimentacao, movimentacaohoras, movimentacaobancohoras, copiahoraextra, prestacaocontas, encerrarcompetencia, relatorios)

### 1.6 Orçamento (Fluxos que faltam)

```
┌──────────────┐   ┌──────────┐   ┌──────────┐   ┌──────────────┐
│ Composição   │──▶│ Digitação│──▶│ Efetivação│──▶│ Cronograma   │
│ (itens)      │   │ Rápida   │   │ (lock)   │   │ Financeiro   │
└──────────────┘   └──────────┘   └──────────┘   └──────────────┘
       │                                                │
       ▼                                                ▼
┌──────────────┐                                 ┌──────────────┐
│ Reajuste por │                                 │ Análise de   │
│ Classe/Base  │                                 │ Compras      │
└──────────────┘                                 └──────────────┘

Efetivação: RASCUNHO → EFETIVADO (não permite mais alteração de composição)
Cronograma Financeiro: distribui valor do orçamento por mês (curva S financeira)
Análise de Compras: compara orçado × comprado × saldo a comprar
```

**Telas Strato:** FrEfetivarOrcamento, FrCronogramaFinanceiro, FrDigitacaoRapida, FrReajustePercentual, FrAnaliseCompras, FrAplicarPrecoBaseValorFinalOrcamento

---

## 2. ENRIQUECIMENTO DE TABELAS EXISTENTES (ALTER TABLE)

### 2.1 project (+ 21 colunas)

```sql
ALTER TABLE project ADD COLUMN neighborhood varchar(100);
ALTER TABLE project ADD COLUMN postal_code varchar(10);
ALTER TABLE project ADD COLUMN address_number varchar(20);
ALTER TABLE project ADD COLUMN phone varchar(30);
ALTER TABLE project ADD COLUMN total_built_area numeric(14,2);
ALTER TABLE project ADD COLUMN cei_cno varchar(30);
ALTER TABLE project ADD COLUMN client_id uuid REFERENCES client(id);
ALTER TABLE project ADD COLUMN development_id uuid REFERENCES development(id);
ALTER TABLE project ADD COLUMN project_type_id uuid;
ALTER TABLE project ADD COLUMN branch_id uuid;
ALTER TABLE project ADD COLUMN accounting_code varchar(30);
ALTER TABLE project ADD COLUMN accounting_code_stock varchar(30);
ALTER TABLE project ADD COLUMN accounting_code_mgmt varchar(30);
ALTER TABLE project ADD COLUMN financial_control_enabled boolean NOT NULL DEFAULT false;
ALTER TABLE project ADD COLUMN stock_control_enabled boolean NOT NULL DEFAULT false;
ALTER TABLE project ADD COLUMN budget_control_enabled boolean NOT NULL DEFAULT false;
ALTER TABLE project ADD COLUMN cost_apportionment_enabled boolean NOT NULL DEFAULT false;
ALTER TABLE project ADD COLUMN apportionment_rate numeric(5,2) DEFAULT 0;
ALTER TABLE project ADD COLUMN purchase_limit_no_auth numeric(18,2);
ALTER TABLE project ADD COLUMN labor_price_table_id uuid;
ALTER TABLE project ADD COLUMN billing_to_client boolean NOT NULL DEFAULT false;
```

### 2.2 client (+ 18 colunas)

```sql
ALTER TABLE client ADD COLUMN trade_name varchar(200);
ALTER TABLE client ADD COLUMN person_type varchar(2) NOT NULL DEFAULT 'PJ';
ALTER TABLE client ADD COLUMN cell_phone varchar(30);
ALTER TABLE client ADD COLUMN home_phone varchar(30);
ALTER TABLE client ADD COLUMN whatsapp varchar(30);
ALTER TABLE client ADD COLUMN website varchar(200);
ALTER TABLE client ADD COLUMN address_number varchar(20);
ALTER TABLE client ADD COLUMN neighborhood varchar(100);
ALTER TABLE client ADD COLUMN postal_code varchar(10);
ALTER TABLE client ADD COLUMN billing_address varchar(400);
ALTER TABLE client ADD COLUMN billing_neighborhood varchar(100);
ALTER TABLE client ADD COLUMN billing_postal_code varchar(10);
ALTER TABLE client ADD COLUMN billing_city varchar(100);
ALTER TABLE client ADD COLUMN work_address varchar(400);
ALTER TABLE client ADD COLUMN gross_income numeric(18,2);
ALTER TABLE client ADD COLUMN spouse_income numeric(18,2);
ALTER TABLE client ADD COLUMN preferred_due_day integer;
ALTER TABLE client ADD COLUMN billing_by_email boolean NOT NULL DEFAULT false;
```

### 2.3 supplier (+ 9 colunas)

```sql
ALTER TABLE supplier ADD COLUMN cell_phone varchar(30);
ALTER TABLE supplier ADD COLUMN commercial_phone varchar(30);
ALTER TABLE supplier ADD COLUMN whatsapp varchar(30);
ALTER TABLE supplier ADD COLUMN website varchar(200);
ALTER TABLE supplier ADD COLUMN address varchar(400);
ALTER TABLE supplier ADD COLUMN city varchar(100);
ALTER TABLE supplier ADD COLUMN state varchar(2);
ALTER TABLE supplier ADD COLUMN postal_code varchar(10);
ALTER TABLE supplier ADD COLUMN payment_due_day integer;
```

### 2.4 employee (+ 15 colunas)

```sql
ALTER TABLE employee ADD COLUMN trade_name varchar(200);
ALTER TABLE employee ADD COLUMN birth_date date;
ALTER TABLE employee ADD COLUMN gender varchar(1);
ALTER TABLE employee ADD COLUMN marital_status varchar(20);
ALTER TABLE employee ADD COLUMN rg varchar(20);
ALTER TABLE employee ADD COLUMN rg_issuer varchar(20);
ALTER TABLE employee ADD COLUMN pis varchar(20);
ALTER TABLE employee ADD COLUMN ctps varchar(20);
ALTER TABLE employee ADD COLUMN ctps_series varchar(10);
ALTER TABLE employee ADD COLUMN voter_id varchar(20);
ALTER TABLE employee ADD COLUMN military_id varchar(20);
ALTER TABLE employee ADD COLUMN address varchar(400);
ALTER TABLE employee ADD COLUMN city varchar(100);
ALTER TABLE employee ADD COLUMN state varchar(2);
ALTER TABLE employee ADD COLUMN postal_code varchar(10);
ALTER TABLE employee ADD COLUMN cell_phone varchar(30);
ALTER TABLE employee ADD COLUMN emergency_contact varchar(200);
ALTER TABLE employee ADD COLUMN emergency_phone varchar(30);
ALTER TABLE employee ADD COLUMN bank_code varchar(10);
ALTER TABLE employee ADD COLUMN bank_agency varchar(20);
ALTER TABLE employee ADD COLUMN bank_account varchar(30);
ALTER TABLE employee ADD COLUMN salary numeric(14,2);
ALTER TABLE employee ADD COLUMN department varchar(100);
ALTER TABLE employee ADD COLUMN cost_center_id uuid;
ALTER TABLE employee ADD COLUMN dismissal_date date;
ALTER TABLE employee ADD COLUMN dismissal_reason varchar(200);
```

### 2.5 development_unit (+ 12 colunas)

```sql
ALTER TABLE development_unit ADD COLUMN private_area numeric(10,2);
ALTER TABLE development_unit ADD COLUMN common_area numeric(10,2);
ALTER TABLE development_unit ADD COLUMN garage_area numeric(10,2);
ALTER TABLE development_unit ADD COLUMN garages integer DEFAULT 0;
ALTER TABLE development_unit ADD COLUMN suites integer DEFAULT 0;
ALTER TABLE development_unit ADD COLUMN bathrooms integer DEFAULT 0;
ALTER TABLE development_unit ADD COLUMN solar_orientation varchar(20);
ALTER TABLE development_unit ADD COLUMN view_description varchar(200);
ALTER TABLE development_unit ADD COLUMN registry_number varchar(50);
ALTER TABLE development_unit ADD COLUMN iptu_code varchar(30);
ALTER TABLE development_unit ADD COLUMN client_id uuid REFERENCES client(id);
ALTER TABLE development_unit ADD COLUMN sold_at timestamptz;
```

### 2.6 bank_account (+ 7 colunas)

```sql
ALTER TABLE bank_account ADD COLUMN initial_balance numeric(18,2) DEFAULT 0;
ALTER TABLE bank_account ADD COLUMN initial_balance_date date;
ALTER TABLE bank_account ADD COLUMN cnab_layout varchar(10);
ALTER TABLE bank_account ADD COLUMN covenant_code varchar(30);
ALTER TABLE bank_account ADD COLUMN wallet_code varchar(10);
ALTER TABLE bank_account ADD COLUMN our_number_sequence bigint DEFAULT 1;
ALTER TABLE bank_account ADD COLUMN project_id uuid REFERENCES project(id);
```

---

## 3. NOVAS TABELAS

### 3.1 Financeiro

```sql
-- Índices econômicos (INCC, IGPM, CUB, IPCA)
CREATE TABLE monetary_index (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(20) NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    source varchar(50),
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE monetary_index_value (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    index_id uuid NOT NULL REFERENCES monetary_index(id),
    reference_month date NOT NULL,
    value numeric(12,6) NOT NULL,
    accumulated numeric(14,6),
    UNIQUE(index_id, reference_month)
);

-- Parcelas de contas a pagar (enriquece payable)
CREATE TABLE payable_installment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    payable_id uuid NOT NULL REFERENCES payable(id) ON DELETE CASCADE,
    installment_number integer NOT NULL,
    due_date date NOT NULL,
    amount numeric(18,2) NOT NULL,
    paid_amount numeric(18,2) DEFAULT 0,
    paid_date date,
    discount numeric(18,2) DEFAULT 0,
    interest numeric(18,2) DEFAULT 0,
    fine numeric(18,2) DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    payment_method varchar(30),
    bank_account_id uuid REFERENCES bank_account(id),
    check_number varchar(20),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Adiantamentos a fornecedores
CREATE TABLE supplier_advance (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    project_id uuid NOT NULL REFERENCES project(id),
    amount numeric(18,2) NOT NULL,
    advance_date date NOT NULL,
    balance numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'ACTIVE',
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Movimentação bancária
CREATE TABLE bank_transaction (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_account_id uuid NOT NULL REFERENCES bank_account(id),
    transaction_date date NOT NULL,
    type varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    balance_after numeric(18,2),
    description varchar(300) NOT NULL,
    reference_type varchar(30),
    reference_id uuid,
    reconciled boolean NOT NULL DEFAULT false,
    reconciled_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Cheques emitidos
CREATE TABLE check_issuance (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_account_id uuid NOT NULL REFERENCES bank_account(id),
    check_number varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    issue_date date NOT NULL,
    due_date date,
    payee_name varchar(200) NOT NULL,
    payee_document varchar(20),
    status varchar(20) NOT NULL DEFAULT 'ISSUED',
    cleared_date date,
    payable_installment_id uuid REFERENCES payable_installment(id),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Parcelas de contas a receber
CREATE TABLE receivable_installment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    receivable_id uuid NOT NULL REFERENCES receivable(id) ON DELETE CASCADE,
    installment_number integer NOT NULL,
    due_date date NOT NULL,
    amount numeric(18,2) NOT NULL,
    received_amount numeric(18,2) DEFAULT 0,
    received_date date,
    discount numeric(18,2) DEFAULT 0,
    interest numeric(18,2) DEFAULT 0,
    fine numeric(18,2) DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    boleto_number varchar(50),
    boleto_barcode varchar(60),
    our_number varchar(30),
    remittance_file varchar(100),
    return_file varchar(100),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Retenções fiscais
CREATE TABLE tax_retention (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    payable_id uuid NOT NULL REFERENCES payable(id),
    tax_type varchar(20) NOT NULL,
    base_amount numeric(18,2) NOT NULL,
    rate numeric(8,4) NOT NULL,
    amount numeric(18,2) NOT NULL,
    due_date date,
    paid boolean NOT NULL DEFAULT false,
    guide_number varchar(30),
    created_at timestamptz NOT NULL DEFAULT now()
);
```

### 3.2 Comercial (Vendas Imobiliárias)

```sql
-- Tabela de preços
CREATE TABLE price_table (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    development_id uuid NOT NULL REFERENCES development(id),
    name varchar(100) NOT NULL,
    valid_from date NOT NULL,
    valid_until date,
    index_id uuid REFERENCES monetary_index(id),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE price_table_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    price_table_id uuid NOT NULL REFERENCES price_table(id) ON DELETE CASCADE,
    unit_id uuid NOT NULL REFERENCES development_unit(id),
    price numeric(18,2) NOT NULL,
    down_payment_pct numeric(5,2) DEFAULT 0,
    max_installments integer DEFAULT 1
);

-- Contrato de venda
CREATE TABLE sale_contract (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    development_id uuid NOT NULL REFERENCES development(id),
    contract_number varchar(30) NOT NULL UNIQUE,
    contract_date date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PROPOSAL',
    total_amount numeric(18,2) NOT NULL,
    down_payment numeric(18,2) DEFAULT 0,
    financed_amount numeric(18,2) DEFAULT 0,
    installment_count integer NOT NULL DEFAULT 1,
    index_id uuid REFERENCES monetary_index(id),
    interest_rate numeric(8,4) DEFAULT 0,
    amortization_type varchar(10) DEFAULT 'PRICE',
    signing_date date,
    cancellation_date date,
    cancellation_reason varchar(500),
    cancellation_fine_pct numeric(5,2),
    transfer_date date,
    transferred_to_contract_id uuid,
    broker_id uuid,
    commission_rate numeric(5,2),
    commission_amount numeric(18,2),
    notes text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- Unidades do contrato
CREATE TABLE sale_contract_unit (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id uuid NOT NULL REFERENCES sale_contract(id) ON DELETE CASCADE,
    unit_id uuid NOT NULL REFERENCES development_unit(id),
    price numeric(18,2) NOT NULL
);

-- Compradores (proponentes)
CREATE TABLE sale_contract_proponent (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id uuid NOT NULL REFERENCES sale_contract(id) ON DELETE CASCADE,
    client_id uuid NOT NULL REFERENCES client(id),
    participation_pct numeric(5,2) NOT NULL DEFAULT 100,
    role varchar(20) NOT NULL DEFAULT 'BUYER'
);

-- Parcelas da venda
CREATE TABLE sale_installment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id uuid NOT NULL REFERENCES sale_contract(id) ON DELETE CASCADE,
    installment_number integer NOT NULL,
    type varchar(30) NOT NULL DEFAULT 'MONTHLY',
    original_due_date date NOT NULL,
    current_due_date date NOT NULL,
    original_amount numeric(18,2) NOT NULL,
    adjusted_amount numeric(18,2) NOT NULL,
    paid_amount numeric(18,2) DEFAULT 0,
    paid_date date,
    interest numeric(18,2) DEFAULT 0,
    fine numeric(18,2) DEFAULT 0,
    discount numeric(18,2) DEFAULT 0,
    adjustment_index_value numeric(12,6),
    status varchar(20) NOT NULL DEFAULT 'FUTURE',
    boleto_number varchar(50),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);
```

### 3.3 Suprimentos (enriquecimento)

```sql
-- Itens do pedido de compra (multi-item)
CREATE TABLE purchase_order_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id uuid NOT NULL REFERENCES purchase_order(id) ON DELETE CASCADE,
    material_id uuid REFERENCES material(id),
    description varchar(300) NOT NULL,
    unit varchar(20) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit_price numeric(14,4) NOT NULL,
    total_price numeric(18,2) NOT NULL,
    received_quantity numeric(14,4) DEFAULT 0,
    budget_item_id uuid,
    cost_code_id uuid,
    notes varchar(300)
);

-- Limite de compra por obra
CREATE TABLE purchase_budget_limit (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    period_start date NOT NULL,
    period_end date NOT NULL,
    limit_amount numeric(18,2) NOT NULL,
    consumed_amount numeric(18,2) DEFAULT 0,
    requires_auth_above numeric(18,2),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Cronograma de compras
CREATE TABLE procurement_schedule (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    budget_item_id uuid,
    material_description varchar(300) NOT NULL,
    planned_date date NOT NULL,
    quantity numeric(14,4) NOT NULL,
    estimated_cost numeric(18,2),
    status varchar(20) NOT NULL DEFAULT 'PLANNED',
    purchase_order_id uuid REFERENCES purchase_order(id),
    created_at timestamptz NOT NULL DEFAULT now()
);
```

### 3.4 Mão de Obra

```sql
-- Tipos de hora
CREATE TABLE hour_type (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(10) NOT NULL UNIQUE,
    name varchar(60) NOT NULL,
    multiplier numeric(4,2) NOT NULL DEFAULT 1.0,
    active boolean NOT NULL DEFAULT true
);

-- Competência (período mensal)
CREATE TABLE competency_period (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    year_month date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    closed_at timestamptz,
    closed_by varchar(200),
    UNIQUE(project_id, year_month)
);

-- Banco de horas
CREATE TABLE hour_bank (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id uuid NOT NULL REFERENCES employee(id),
    project_id uuid NOT NULL REFERENCES project(id),
    competency_id uuid NOT NULL REFERENCES competency_period(id),
    type varchar(10) NOT NULL,
    hours numeric(8,2) NOT NULL,
    description varchar(200),
    reference_date date NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Histórico salarial
CREATE TABLE salary_history (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id uuid NOT NULL REFERENCES employee(id),
    effective_date date NOT NULL,
    salary numeric(14,2) NOT NULL,
    hourly_rate numeric(14,4) NOT NULL,
    reason varchar(200),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- Tabela de preço de mão de obra
CREATE TABLE labor_price_table (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL,
    valid_from date NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE labor_price_table_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    table_id uuid NOT NULL REFERENCES labor_price_table(id) ON DELETE CASCADE,
    role varchar(80) NOT NULL,
    hourly_rate numeric(14,4) NOT NULL,
    monthly_rate numeric(14,2)
);
```

### 3.5 Lookups

```sql
-- Cidade (FK normalizada)
CREATE TABLE city (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL,
    state varchar(2) NOT NULL,
    ibge_code varchar(10),
    UNIQUE(name, state)
);

-- Tipo de despesa
CREATE TABLE expense_type (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(20) NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    category varchar(50),
    accounting_code varchar(30),
    active boolean NOT NULL DEFAULT true
);

-- Plano de contas
CREATE TABLE chart_of_accounts (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(20) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    parent_id uuid REFERENCES chart_of_accounts(id),
    type varchar(20) NOT NULL,
    level integer NOT NULL DEFAULT 1,
    active boolean NOT NULL DEFAULT true
);
```

---

## 4. TASKS DE DESENVOLVIMENTO

### Sprint A — Fundação: Schema Enrichment (Est: 31h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| A.1 | Migration V11: ALTER TABLE project (+21 cols) | 2h | Adicionar campos de controle financeiro, contábil, flags |
| A.2 | Migration V12: ALTER TABLE client (+18 cols) | 2h | Múltiplos endereços, dados financeiros, classificação |
| A.3 | Migration V13: ALTER TABLE supplier (+9 cols) | 1h | Contatos, endereço, dia vencimento |
| A.4 | Migration V14: ALTER TABLE employee (+25 cols) | 2h | Documentos, endereço, banco, salário |
| A.5 | Migration V15: Lookup tables (city, expense_type, chart_of_accounts) | 3h | Tabelas de referência normalizadas |
| A.6 | Migration V16: monetary_index + monetary_index_value | 2h | Índices econômicos com histórico |
| A.7 | Atualizar entities JPA (Project, Client, Supplier, Employee) | 8h | Mapear novos campos nas entidades |
| A.8 | Atualizar DTOs (request/response records) | 4h | Expor novos campos na API |
| A.9 | Atualizar testes existentes | 4h | Garantir que nada quebrou |
| A.10 | Seed data: cidades BR, índices INCC/IGPM/CUB | 3h | Dados iniciais para lookup |

### Sprint B — Financeiro: Contas a Pagar (Est: 59h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| B.1 | Migration: payable_installment, tax_retention, supplier_advance | 3h | Tabelas de parcelamento e retenção |
| B.2 | Migration: bank_transaction, check_issuance | 2h | Movimentação bancária e cheques |
| B.3 | PayableInstallmentService: parcelamento automático | 6h | Gerar N parcelas com datas e valores |
| B.4 | TaxRetentionService: cálculo ISS/INSS/IR/PIS/COFINS | 8h | Retenções na entrada da NF |
| B.5 | PaymentAuthorizationService: workflow de autorização | 5h | Alçada por valor, aprovação multi-nível |
| B.6 | PaymentExecutionService: efetuar pagamento | 6h | Baixa parcela, gera mov. bancária |
| B.7 | CheckIssuanceService: emissão e controle de cheques | 4h | Emitir, compensar, cancelar |
| B.8 | SupplierAdvanceService: adiantamentos | 4h | Criar, abater em NFs futuras |
| B.9 | BankTransactionService: movimentação e conciliação | 6h | Lançamentos, saldo, reconciliação |
| B.10 | Controllers REST (6 endpoints novos) | 5h | CRUD + workflow actions |
| B.11 | Relatório: Posição de pagamentos PDF | 4h | Aging, por fornecedor, por obra |
| B.12 | Testes unitários + integração | 6h | Cobertura 80%+ |

### Sprint C — Financeiro: Contas a Receber (Est: 30h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| C.1 | Migration: receivable_installment (enriquecimento) | 2h | Boleto, nosso número, remessa/retorno |
| C.2 | ReceivableInstallmentService: geração de parcelas | 4h | Price/SAC, juros, multa |
| C.3 | BoletoService: geração de boletos (ficha compensação) | 6h | Layout bancário, código de barras |
| C.4 | CnabExportService: remessa CNAB 240/400 | 5h | Arquivo de cobrança bancária |
| C.5 | CnabImportService: retorno CNAB (baixa automática) | 5h | Ler retorno, baixar parcelas |
| C.6 | ReceivablePaymentService: baixa manual com juros/multa | 3h | Recebimento parcial, desconto |
| C.7 | Controllers REST (4 endpoints) | 3h | Parcelas, boletos, CNAB |
| C.8 | Testes | 2h | |

### Sprint D — Suprimentos Avançado (Est: 54h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| D.1 | Migration: purchase_order_item, purchase_budget_limit, procurement_schedule | 3h | Multi-item, limites, cronograma |
| D.2 | RequisitionAuthorizationService: autorização por alçada | 6h | Limite por obra/usuário, workflow |
| D.3 | PurchaseOrderItemService: pedido multi-item | 5h | Itens com material, qtd, preço |
| D.4 | PurchaseBudgetLimitService: controle de limite | 4h | Verificar saldo antes de comprar |
| D.5 | ProcurementScheduleService: cronograma de compras | 5h | Planejamento × execução |
| D.6 | QuotationAnalysisService: análise comparativa melhorada | 5h | Mapa comparativo multi-item |
| D.7 | CostAppropriationService: apropriação por obra/etapa | 6h | Distribuir custo do pedido |
| D.8 | PurchaseOrderReceivingService: recebimento parcial multi-item | 5h | Receber item a item |
| D.9 | Controllers REST (8 endpoints) | 6h | Autorização, limites, cronograma |
| D.10 | Relatórios: Pedidos em atraso, Mapa cotação PDF | 5h | |
| D.11 | Testes | 4h | |

### Sprint E — Vendas Imobiliárias (Est: 78h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| E.1 | Migration: price_table, sale_contract, sale_installment + relacionadas | 4h | Schema completo comercial |
| E.2 | PriceTableService: tabelas de preço por empreendimento | 4h | Vigência, índice de reajuste |
| E.3 | SaleContractService: CRUD + workflow de contrato | 8h | Proposta→Assinado→Vigente→Quitado |
| E.4 | SaleInstallmentService: geração de parcelas (Price/SAC) | 8h | Cálculo financeiro, séries |
| E.5 | InstallmentAdjustmentService: reajuste por índice | 6h | INCC/IGPM mensal automático |
| E.6 | SalePaymentService: recebimento de parcelas | 5h | Baixa com juros/multa/desconto |
| E.7 | ContractCancellationService: distrato | 6h | Cálculo multa, devolução |
| E.8 | ContractTransferService: cessão/transferência | 5h | Novo comprador, manter parcelas |
| E.9 | RenegotiationService: reparcelamento | 6h | Gerar novas parcelas do saldo |
| E.10 | CommissionService: comissões de corretores | 5h | Cálculo, controle pagamento |
| E.11 | BankTransferService: repasse bancário | 4h | Documentação, acompanhamento |
| E.12 | Controllers REST (12 endpoints) | 8h | Contratos, parcelas, reajuste |
| E.13 | Relatórios: Posição vendas, Inadimplência, Comissões | 5h | |
| E.14 | Testes | 4h | |

### Sprint F — Mão de Obra (Est: 47h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| F.1 | Migration: hour_type, competency_period, hour_bank, salary_history, labor_price_table | 3h | Schema MO |
| F.2 | CompetencyPeriodService: abrir/fechar competência | 4h | Controle mensal, lock |
| F.3 | TimesheetEnrichmentService: apontamento por tipo hora | 6h | Normal, extra 50/100%, noturna |
| F.4 | HourBankService: banco de horas (crédito/débito/saldo) | 5h | Acumulado por funcionário |
| F.5 | LaborCostService: custo de MO por obra/etapa | 6h | Apropriação, encargos |
| F.6 | SalaryHistoryService: histórico salarial | 3h | Reajustes, promoções |
| F.7 | LaborPriceTableService: tabela de preços MO | 3h | Por cargo/função |
| F.8 | LaborProductivityService: produtividade (HH/unidade) | 5h | Comparar com orçado |
| F.9 | Controllers REST (8 endpoints) | 5h | Competência, banco horas, tabela |
| F.10 | Relatórios: Folha resumo, Produtividade, Banco horas | 4h | |
| F.11 | Testes | 3h | |

### Sprint G — Orçamento: Fluxos Faltantes (Est: 40h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| G.1 | BudgetEffectivenessService: efetivar orçamento (lock) | 4h | RASCUNHO→EFETIVADO, impede edição |
| G.2 | BulkEntryService: digitação rápida (batch insert) | 5h | Importar lista de itens de uma vez |
| G.3 | FinancialScheduleService: cronograma financeiro | 6h | Distribuir valor por mês (curva S $) |
| G.4 | PriceAdjustmentByClassService: reajuste por classe/tipo | 4h | Aplicar % por grupo de insumo |
| G.5 | PurchaseAnalysisService: análise de compras do orçamento | 5h | Orçado × comprado × saldo |
| G.6 | BudgetComparisonEnhanced: diff detalhado entre versões | 4h | Item a item com variação % |
| G.7 | Controllers REST (6 endpoints) | 4h | Efetivar, cronograma, análise |
| G.8 | Relatórios: Cronograma financeiro PDF, Análise compras | 4h | |
| G.9 | Testes | 4h | |

### Sprint H — Integração e Relatórios Gerenciais (Est: 57h)

| # | Task | Esforço | Descrição |
|---|------|:-------:|-----------|
| H.1 | DREService: Demonstrativo de Resultado por obra | 8h | Receitas - Custos - Despesas |
| H.2 | CostMapService: mapa orçado × comprometido × realizado | 6h | Visão consolidada por etapa |
| H.3 | CashFlowProjectionService: fluxo de caixa projetado | 6h | Previsto + realizado + projeção |
| H.4 | AgingReportService: inadimplência por faixa | 4h | 30/60/90/120+ dias |
| H.5 | ABCSupplierService: curva ABC fornecedores | 3h | Volume de compras |
| H.6 | ABCMaterialService: curva ABC insumos consumidos | 3h | Consumo por obra |
| H.7 | ExecutiveDashboardService: painel gerencial | 8h | KPIs consolidados multi-obra |
| H.8 | EventIntegrationService: eventos cross-module | 6h | Pagamento→atualiza saldo, Medição→gera receita |
| H.9 | Controllers REST (8 endpoints) | 5h | Relatórios e dashboard |
| H.10 | PDF reports (DRE, Mapa custos, Fluxo caixa) | 5h | |
| H.11 | Testes | 3h | |

---

## 5. RESUMO EXECUTIVO

| Sprint | Tema | Horas | Prioridade |
|--------|------|:-----:|:----------:|
| A | Fundação (schema enrichment) | 31h | **P0** |
| B | Contas a Pagar | 59h | **P1** |
| C | Contas a Receber | 30h | **P1** |
| D | Suprimentos Avançado | 54h | **P1** |
| E | Vendas Imobiliárias | 78h | **P1** |
| F | Mão de Obra | 47h | **P2** |
| G | Orçamento: Fluxos Faltantes | 40h | **P2** |
| H | Integração e Relatórios | 57h | **P2** |
| **TOTAL** | | **396h** | |

### Ordem de execução recomendada:
1. **Sprint A** (fundação) — obrigatório antes de tudo
2. **Sprint B + C** (financeiro) — core de qualquer ERP
3. **Sprint D** (suprimentos) — depende do financeiro
4. **Sprint G** (orçamento) — independente
5. **Sprint E** (comercial) — depende do financeiro
6. **Sprint F** (mão de obra) — independente
7. **Sprint H** (integração) — depende de todos os anteriores
