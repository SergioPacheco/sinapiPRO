# 🗄️ Modelo de Dados — PostgreSQL 17

> ~85 tabelas | UUID PKs | JSONB metadata | tsvector full-text | Flyway V1–V4

## Convenções

- **Primary Key**: `id uuid DEFAULT gen_random_uuid()`
- **Auditoria**: `created_at timestamptz`, `updated_at timestamptz`
- **Multi-tenant**: `tenant_id uuid` nas tabelas de dados por empresa
- **Soft delete**: via módulo `shared.TrashItem` (lixeira com restore)
- **Monetário**: `numeric(18,2)` para valores, `numeric(6,4)` para percentuais

---

## Tabelas Globais (sem tenant)

```mermaid
erDiagram
    TENANT {
        uuid id PK
        varchar name
        varchar slug UK
        boolean active
    }
    CITY {
        uuid id PK
        varchar name
        varchar state
        varchar ibge_code
    }
    MONETARY_INDEX {
        uuid id PK
        varchar code UK
        varchar name
    }
    MONETARY_INDEX_VALUE {
        uuid id PK
        uuid index_id FK
        date reference_month
        numeric value
    }
    HOUR_TYPE {
        uuid id PK
        varchar code UK
        varchar name
        numeric multiplier
    }
    UNIT_OF_MEASURE {
        uuid id PK
        varchar symbol UK
        varchar description
    }
    APP_SETTINGS {
        varchar key PK
        varchar value
    }

    MONETARY_INDEX ||--o{ MONETARY_INDEX_VALUE : "values"
```

---

## Core: Projeto + Orçamento + Medição

```mermaid
erDiagram
    PROJECT {
        uuid id PK
        uuid tenant_id
        varchar code UK
        varchar name
        varchar customer_name
        varchar status
        date start_date
        date end_date
        numeric total_area
    }
    BUDGET {
        uuid id PK
        uuid project_id FK
        varchar code UK
        varchar title
        varchar status
        numeric total_amount
        jsonb metadata
    }
    BUDGET_STAGE {
        uuid id PK
        uuid budget_id FK
        uuid parent_id FK
        varchar code
        varchar title
        integer position
    }
    BUDGET_ITEM {
        uuid id PK
        uuid stage_id FK
        varchar code
        varchar description
        varchar unit
        numeric quantity
        numeric unit_price
        numeric bdi_percent
    }
    BDI_CONFIG {
        uuid id PK
        uuid budget_id FK
        varchar item_type
        numeric administration
        numeric profit
        numeric taxes
    }
    BUDGET_ITEM_MEMO {
        uuid id PK
        uuid budget_item_id FK
        jsonb lines
        numeric result
    }
    BUDGET_ITEM_TAG {
        uuid id PK
        uuid budget_item_id FK
        varchar tag
    }
    BUDGET_PROPOSAL {
        uuid id PK
        uuid budget_id FK
        varchar status
        numeric total_amount
        numeric discount_percent
    }
    SOCIAL_CHARGES_CONFIG {
        uuid id PK
        uuid budget_id FK
        varchar group_name
        numeric inss
        numeric fgts
    }
    MEASUREMENT {
        uuid id PK
        uuid budget_id FK
        varchar code
        varchar status
        integer period_number
        numeric total_amount
    }
    MEASUREMENT_ITEM {
        uuid id PK
        uuid measurement_id FK
        uuid budget_item_id FK
        numeric contracted_qty
        numeric previous_qty
        numeric current_qty
        numeric unit_price
    }
    MEASUREMENT_ITEM_MEMO {
        uuid id PK
        uuid measurement_item_id FK
        jsonb lines
    }
    MEASUREMENT_APPROVER {
        uuid id PK
        uuid measurement_id FK
        varchar approver_name
        varchar role
    }
    MEASUREMENT_HISTORY {
        uuid id PK
        uuid measurement_id FK
        varchar from_status
        varchar to_status
        varchar reason
    }

    PROJECT ||--o{ BUDGET : "has"
    BUDGET ||--o{ BUDGET_STAGE : "stages"
    BUDGET_STAGE ||--o{ BUDGET_ITEM : "items"
    BUDGET ||--o{ BDI_CONFIG : "bdi"
    BUDGET ||--o{ SOCIAL_CHARGES_CONFIG : "charges"
    BUDGET ||--o{ BUDGET_PROPOSAL : "proposals"
    BUDGET_ITEM ||--o{ BUDGET_ITEM_MEMO : "memo"
    BUDGET_ITEM ||--o{ BUDGET_ITEM_TAG : "tags"
    BUDGET ||--o{ MEASUREMENT : "measurements"
    MEASUREMENT ||--o{ MEASUREMENT_ITEM : "items"
    MEASUREMENT ||--o{ MEASUREMENT_APPROVER : "approvers"
    MEASUREMENT ||--o{ MEASUREMENT_HISTORY : "history"
```

---

## Contratos + Job Costing

```mermaid
erDiagram
    CONTRACT {
        uuid id PK
        uuid project_id FK
        uuid supplier_id FK
        varchar code
        varchar title
        varchar type
        varchar status
        numeric total_amount
        numeric retention_percent
    }
    CHANGE_ORDER {
        uuid id PK
        uuid contract_id FK
        varchar code
        numeric amount
        varchar status
    }
    COST_CODE {
        uuid id PK
        uuid project_id FK
        varchar code
        varchar description
        numeric budget_amount
    }
    COST_TRANSACTION {
        uuid id PK
        uuid cost_code_id FK
        varchar type
        numeric amount
        date reference_date
        varchar source_type
    }

    CONTRACT ||--o{ CHANGE_ORDER : "addendums"
    COST_CODE ||--o{ COST_TRANSACTION : "transactions"
```

---

## Suprimentos + Estoque

```mermaid
erDiagram
    PURCHASE_REQUEST {
        uuid id PK
        uuid project_id FK
        varchar code
        varchar status
        varchar priority
        date needed_by
    }
    QUOTATION {
        uuid id PK
        uuid purchase_request_id FK
        varchar status
        date deadline
    }
    QUOTATION_RESPONSE {
        uuid id PK
        uuid quotation_id FK
        uuid supplier_id FK
        numeric total_amount
        boolean selected
    }
    PURCHASE_ORDER {
        uuid id PK
        uuid project_id FK
        uuid supplier_id FK
        varchar code
        varchar status
        numeric total_amount
    }
    PURCHASE_ORDER_ITEM {
        uuid id PK
        uuid purchase_order_id FK
        varchar description
        numeric quantity
        numeric unit_price
    }
    RECEIVING {
        uuid id PK
        uuid purchase_order_id FK
        date received_date
        varchar invoice_number
    }
    PROCUREMENT_SCHEDULE {
        uuid id PK
        uuid project_id FK
        date needed_date
        integer lead_time_days
        varchar status
    }

    PURCHASE_REQUEST ||--o{ QUOTATION : "quotes"
    QUOTATION ||--o{ QUOTATION_RESPONSE : "responses"
    PURCHASE_ORDER ||--o{ PURCHASE_ORDER_ITEM : "items"
    PURCHASE_ORDER ||--o{ RECEIVING : "receivings"
```

---

## Financeiro

```mermaid
erDiagram
    PAYABLE {
        uuid id PK
        uuid project_id FK
        uuid supplier_id FK
        varchar description
        numeric amount
        varchar status
    }
    PAYABLE_INSTALLMENT {
        uuid id PK
        uuid payable_id FK
        integer number
        numeric amount
        date due_date
        varchar status
    }
    RECEIVABLE {
        uuid id PK
        uuid project_id FK
        varchar description
        numeric amount
        varchar status
    }
    RECEIVABLE_INSTALLMENT {
        uuid id PK
        uuid receivable_id FK
        integer number
        numeric amount
        date due_date
        varchar status
    }
    BANK_ACCOUNT {
        uuid id PK
        varchar bank_name
        varchar agency
        varchar account_number
        numeric balance
    }
    BANK_TRANSACTION {
        uuid id PK
        uuid bank_account_id FK
        varchar type
        numeric amount
        date transaction_date
    }
    CHART_OF_ACCOUNTS {
        uuid id PK
        varchar code UK
        varchar name
        varchar type
    }
    TAX_RETENTION {
        uuid id PK
        uuid payable_id FK
        varchar tax_type
        numeric rate
        numeric amount
    }
    SUPPLIER_ADVANCE {
        uuid id PK
        uuid supplier_id FK
        numeric amount
        numeric balance
    }
    CHECK_ISSUANCE {
        uuid id PK
        uuid bank_account_id FK
        varchar check_number
        numeric amount
        varchar status
    }

    PAYABLE ||--o{ PAYABLE_INSTALLMENT : "installments"
    PAYABLE ||--o{ TAX_RETENTION : "retentions"
    RECEIVABLE ||--o{ RECEIVABLE_INSTALLMENT : "installments"
    BANK_ACCOUNT ||--o{ BANK_TRANSACTION : "transactions"
    BANK_ACCOUNT ||--o{ CHECK_ISSUANCE : "checks"
```

---

## Comercial (Vendas Imobiliárias)

| Tabela | Descrição |
|--------|-----------|
| `development` | Empreendimentos imobiliários |
| `development_unit` | Unidades (apartamentos, lotes) |
| `sale_contract` | Contratos de venda |
| `sale_installment` | Parcelas do contrato |
| `service_ticket` | Tickets de pós-venda |

---

## Cronograma + Diário + Equipamentos

| Tabela | Descrição |
|--------|-----------|
| `schedule_activity` | Atividades do cronograma (CPM, WBS) |
| `activity_dependency` | Dependências FS/FF/SS/SF com lag |
| `schedule_baseline` | Snapshots do cronograma (JSONB) |
| `schedule_holiday` | Feriados por obra |
| `daily_log` | Diário de obra (cabeçalho) |
| `daily_log_task/labor/equipment/material/occurrence/photo` | Detalhes do diário |
| `equipment` | Cadastro de equipamentos |
| `equipment_usage` | Horas de uso |
| `equipment_fueling` | Abastecimentos |
| `weather_delay` | Atrasos por clima |

---

## Segurança + Qualidade + Documentos

| Tabela | Descrição |
|--------|-----------|
| `safety_inspection` | Inspeções de segurança |
| `safety_incident` | Incidentes/acidentes |
| `rfi` | Request for Information |
| `punch_list_item` | Pendências de entrega |
| `submittal` | Aprovação de materiais/métodos |
| `document` | Documentos vinculados a obras |
| `document_version` | Versionamento de documentos |

---

## SINAPI (Composições + Insumos)

```mermaid
erDiagram
    MATERIAL {
        uuid id PK
        varchar sinapi_code UK
        varchar description
        varchar unit
        varchar origin
        tsvector search_vector
    }
    MATERIAL_PRICE {
        uuid id PK
        uuid material_id FK
        varchar state
        date reference_month
        numeric price
    }
    COMPOSITION {
        uuid id PK
        varchar sinapi_code UK
        varchar description
        varchar unit
        numeric labor_cost
        numeric material_cost
        numeric equipment_cost
        numeric total_cost
        tsvector search_vector
    }
    COMPOSITION_ITEM {
        uuid id PK
        uuid composition_id FK
        uuid material_id FK
        numeric coefficient
    }

    MATERIAL ||--o{ MATERIAL_PRICE : "prices"
    COMPOSITION ||--o{ COMPOSITION_ITEM : "inputs"
    MATERIAL ||--o{ COMPOSITION_ITEM : "used_in"
```

---

## Cadastros (Registry)

| Tabela | Descrição |
|--------|-----------|
| `client` | Clientes (PF/PJ) |
| `employee` | Funcionários |
| `supplier` | Fornecedores |
| `supplier_document/bank_account/evaluation` | Extensões do fornecedor |
| `bank_account` | Contas bancárias da empresa |
| `team` / `team_member` | Equipes de obra |

---

## Plataforma

| Tabela | Descrição |
|--------|-----------|
| `tenant` | Empresas (multi-tenant) |
| `notification` | Notificações do sistema |
| `report_template` | Templates de relatório |
| `trash_item` | Lixeira (soft delete com restore) |

---

## Índices (V4)

- `tsvector` GIN indexes em `material.search_vector` e `composition.search_vector`
- Suportam busca full-text em português para composições SINAPI
