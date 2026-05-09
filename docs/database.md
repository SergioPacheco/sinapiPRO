# 🗄️ Modelo de Dados — PostgreSQL 17

## Diagrama ER — Core

```mermaid
erDiagram
    BUDGET {
        uuid id PK
        varchar code UK
        varchar title
        varchar customer_name
        numeric total_amount
        varchar status
        date start_date
        date end_date
        jsonb metadata
        timestamptz created_at
        timestamptz updated_at
    }

    SUPPLIER {
        uuid id PK
        varchar code UK
        varchar name
        varchar trade_name
        varchar tax_id UK
        varchar email
        varchar phone
        integer rating
        boolean active
        timestamptz created_at
        timestamptz updated_at
    }

    INVOICE {
        uuid id PK
        varchar number UK
        uuid budget_id FK
        uuid supplier_id FK
        numeric amount
        date issue_date
        date due_date
        varchar status
        text notes
        timestamptz created_at
        timestamptz updated_at
    }

    BUDGET ||--o{ INVOICE : "has"
    SUPPLIER ||--o{ INVOICE : "issues"
```

## Diagrama ER — SINAPI (Composições e Insumos)

```mermaid
erDiagram
    MATERIAL {
        uuid id PK
        varchar sinapi_code UK
        varchar description
        varchar unit
        varchar origin
        tsvector search_vector
        timestamptz created_at
        timestamptz updated_at
    }

    MATERIAL_PRICE {
        uuid id PK
        uuid material_id FK
        char state
        date reference_month
        numeric price
        timestamptz created_at
    }

    COMPOSITION {
        uuid id PK
        varchar sinapi_code UK
        varchar description
        varchar unit
        varchar group_name
        tsvector search_vector
        timestamptz created_at
        timestamptz updated_at
    }

    COMPOSITION_ITEM {
        uuid id PK
        uuid composition_id FK
        uuid material_id FK
        numeric coefficient
        timestamptz created_at
    }

    MATERIAL ||--o{ MATERIAL_PRICE : "prices by state and month"
    COMPOSITION ||--o{ COMPOSITION_ITEM : "contains"
    MATERIAL ||--o{ COMPOSITION_ITEM : "used in"
```

## Diagrama ER — Orçamento Detalhado

```mermaid
erDiagram
    BUDGET ||--o{ BUDGET_STAGE : "has stages"
    BUDGET ||--|| BDI_CONFIG : "has BDI"

    BUDGET_STAGE {
        uuid id PK
        uuid budget_id FK
        uuid parent_id FK
        varchar name
        integer sort_order
    }

    BUDGET_STAGE ||--o{ BUDGET_ITEM : "contains"

    BUDGET_ITEM {
        uuid id PK
        uuid stage_id FK
        uuid composition_id FK
        numeric quantity
        numeric unit_cost
        numeric bdi_pct
    }

    BDI_CONFIG {
        uuid id PK
        uuid budget_id FK "unique"
        numeric administration
        numeric profit
        numeric taxes
        numeric social_charges
        numeric financial_expenses
        numeric risks
    }

    COMPOSITION ||--o{ BUDGET_ITEM : "referenced by"
```

## Diagrama ER — Medições e Contratos

```mermaid
erDiagram
    BUDGET ||--o{ MEASUREMENT : "has measurements"
    BUDGET ||--o{ CONTRACT : "has contracts"

    MEASUREMENT {
        uuid id PK
        uuid budget_id FK
        integer number
        date period_start
        date period_end
        varchar status
        numeric retention_pct
        text notes
    }

    MEASUREMENT ||--o{ MEASUREMENT_ITEM : "contains"

    MEASUREMENT_ITEM {
        uuid id PK
        uuid measurement_id FK
        uuid cost_code_id FK
        varchar description
        numeric quantity
        numeric unit_price
    }

    CONTRACT {
        uuid id PK
        uuid budget_id FK
        uuid supplier_id FK
        varchar number
        varchar description
        numeric original_value
        numeric retention_pct
        varchar status
        date start_date
        date end_date
    }

    CONTRACT ||--o{ CHANGE_ORDER : "has aditivos"

    CHANGE_ORDER {
        uuid id PK
        uuid contract_id FK
        integer number
        varchar description
        numeric amount
        varchar status
        text justification
        timestamptz approved_at
    }

    SUPPLIER ||--o{ CONTRACT : "contracted"
```

## Diagrama ER — Job Costing e Suprimentos

```mermaid
erDiagram
    BUDGET ||--o{ COST_CODE : "has cost codes"

    COST_CODE {
        uuid id PK
        uuid budget_id FK
        varchar code
        varchar description
        numeric budgeted_amount
    }

    COST_CODE ||--o{ COST_TRANSACTION : "has transactions"

    COST_TRANSACTION {
        uuid id PK
        uuid cost_code_id FK
        varchar type
        numeric amount
        varchar description
        uuid reference_id
        date transaction_date
    }

    BUDGET ||--o{ PURCHASE_REQUEST : "has requests"

    PURCHASE_REQUEST {
        uuid id PK
        uuid budget_id FK
        uuid cost_code_id FK
        varchar description
        numeric quantity
        varchar unit
        varchar status
    }

    PURCHASE_REQUEST ||--o{ QUOTATION : "quoted via"

    QUOTATION {
        uuid id PK
        uuid purchase_request_id FK
        date deadline
        varchar status
    }

    QUOTATION ||--o{ QUOTATION_RESPONSE : "has responses"

    QUOTATION_RESPONSE {
        uuid id PK
        uuid quotation_id FK
        uuid supplier_id FK
        numeric unit_price
        integer delivery_days
        text notes
    }

    BUDGET ||--o{ PURCHASE_ORDER : "has orders"

    PURCHASE_ORDER {
        uuid id PK
        uuid budget_id FK
        uuid supplier_id FK
        varchar number
        varchar description
        numeric quantity
        numeric unit_price
        varchar status
    }

    PURCHASE_ORDER ||--o{ RECEIVING : "receivings"

    RECEIVING {
        uuid id PK
        uuid purchase_order_id FK
        numeric quantity_received
        date received_at
        text notes
    }

    SUPPLIER ||--o{ QUOTATION_RESPONSE : "responds"
    SUPPLIER ||--o{ PURCHASE_ORDER : "supplies"
```

## Detalhes dos Tipos (PostgreSQL)

| Coluna | Tipo Real | Notas |
|--------|-----------|-------|
| `id` | `uuid` | PK, gerado via `gen_random_uuid()` |
| `code`, `number` | `varchar(40)` | Chave de negócio, UNIQUE |
| `name`, `title`, `description` | `varchar(140-500)` | Texto descritivo |
| `total_amount`, `amount`, `original_value` | `numeric(18,2)` | Valores monetários |
| `quantity`, `unit_cost`, `unit_price` | `numeric(14,4)` | Quantidades e preços unitários |
| `coefficient` | `numeric(14,6)` | Coeficientes SINAPI (alta precisão) |
| `bdi_pct`, `retention_pct` | `numeric(6,4)` | Percentuais |
| `metadata` | `jsonb` | Campos flexíveis sem migration |
| `search_vector` | `tsvector` | Full-text search (GIN index) |
| `state` | `char(2)` | UF brasileira |
| `status` | `varchar(20-30)` | Enum como string |
| `created_at`, `updated_at` | `timestamptz` | Auditoria |

## Features PostgreSQL Utilizadas

| Feature | Uso |
|---------|-----|
| `uuid` PK | Distributed-friendly, sem exposição de sequência |
| `jsonb` | Metadata flexível em budget (campos opcionais sem migration) |
| `tsvector` + GIN | Full-text search em composições e materiais SINAPI |
| `numeric(18,2)` | Valores monetários com precisão exata |
| Índices parciais | `idx_supplier_active`, `idx_invoice_status` |
| Índices compostos | `(material_id, state, reference_month)` para lookup de preço |
| `UNIQUE` constraints | Chaves de negócio (code, sinapi_code, number) |
| `ON DELETE CASCADE` | Limpeza automática de filhos ao remover pai |
| `gen_random_uuid()` | Geração de UUID no banco (pgcrypto) |
| Views | `showcase_portfolio_summary` para dashboards |

## Migrations (Flyway)

| Migration | Descrição |
|-----------|-----------|
| V1 | Schema core: budget, supplier, invoice + índices + view |
| V2 | Dados de demonstração (seed) |
| V3 | Composições SINAPI: material, material_price, composition, composition_item |
| V4 | Dados SINAPI (insumos, preços, composições reais) |
| V5 | Orçamento detalhado: budget_stage, budget_item, bdi_config |
| V6 | Job Costing: cost_code, cost_transaction |
| V7 | Cronograma: schedule_activity, activity_dependency |
| V8 | Medições: measurement, measurement_item |
| V9 | Contratos: contract, contract_item, change_order |
| V10 | Suprimentos: purchase_request, quotation, purchase_order, receiving |
| V11 | Diário de Obra: daily_log + sub-tabelas |
| V12 | Equipamentos: equipment, equipment_usage |
| V13 | Documentos, RFI, Punch List, Segurança do Trabalho |
| V14 | Submittals, Weather Delays, Timesheet, Notificações |
