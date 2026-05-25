# 🔄 Fluxos da API — Diagramas de Sequência

## Autenticação (JWT)

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant JwtTokenService
    participant UserDetailsService
    participant SecurityFilter

    Client->>AuthController: POST /api/v1/auth/token<br/>{username, password, grantType: "PASSWORD"}
    AuthController->>UserDetailsService: loadUserByUsername()
    UserDetailsService-->>AuthController: UserDetails
    AuthController->>AuthController: validate password (BCrypt)
    AuthController->>JwtTokenService: generateToken(user, scopes, roles)
    JwtTokenService-->>AuthController: JWT (access + refresh)
    AuthController-->>Client: 200 {accessToken, refreshToken, expiresIn}

    Note over Client,SecurityFilter: Subsequent requests

    Client->>SecurityFilter: GET /api/v1/budgets<br/>Authorization: Bearer {jwt}
    SecurityFilter->>JwtTokenService: decode + validate
    JwtTokenService-->>SecurityFilter: JwtAuthenticationToken (scopes + roles)
    SecurityFilter->>SecurityFilter: check SCOPE_sinapipro.read
    SecurityFilter-->>Client: 200 (authorized)
```

## Criação de Orçamento (Budget)

```mermaid
sequenceDiagram
    participant Client
    participant BudgetController
    participant BudgetService
    participant BudgetRepository
    participant EventPublisher
    participant Metrics

    Client->>BudgetController: POST /api/v1/budgets<br/>{code, title, customerName, totalAmount, ...}
    BudgetController->>BudgetController: @Valid (Bean Validation)
    BudgetController->>BudgetService: create(request)
    BudgetService->>BudgetRepository: existsByCode(code)
    alt Code already exists
        BudgetRepository-->>BudgetService: true
        BudgetService-->>Client: 409 Conflict (ProblemDetail)
    else Code available
        BudgetRepository-->>BudgetService: false
        BudgetService->>BudgetRepository: save(budget)
        BudgetRepository-->>BudgetService: Budget (with UUID)
        BudgetService->>Metrics: record("budget", CREATED)
        BudgetService->>EventPublisher: publish("budget", CREATED, id)
        BudgetService-->>BudgetController: Budget
        BudgetController-->>Client: 201 Created + Location header
    end
```

## Fluxo de Medição (Workflow Completo)

```mermaid
sequenceDiagram
    participant Client
    participant MeasurementController
    participant MeasurementService
    participant CostTransactionRepo
    participant InvoiceRepo
    participant SSE

    Note over Client,SSE: 1. Criar medição (DRAFT)
    Client->>MeasurementController: POST /api/v1/measurements<br/>{budgetId, number, items[]}
    MeasurementController->>MeasurementService: create(budgetId, items)
    MeasurementService-->>Client: 201 Measurement (status: DRAFT)

    Note over Client,SSE: 2. Submeter para aprovação
    Client->>MeasurementController: POST /api/v1/measurements/{id}/submit
    MeasurementController->>MeasurementService: submit(id)
    MeasurementService->>MeasurementService: validate status == DRAFT
    MeasurementService-->>Client: 200 Measurement (status: SUBMITTED)

    Note over Client,SSE: 3. Aprovar (gera cost transactions + invoice)
    Client->>MeasurementController: POST /api/v1/measurements/{id}/approve
    MeasurementController->>MeasurementService: approve(id)

    par Structured Concurrency (Virtual Threads)
        MeasurementService->>CostTransactionRepo: save(ACTUAL transactions)
    and
        MeasurementService->>InvoiceRepo: save(auto-generated invoice)
    end

    MeasurementService-->>Client: 200 Measurement (status: APPROVED)
    MeasurementService->>SSE: DomainEvent.Created (invoice)
```

## Fluxo de Suprimentos (Cotação → Pedido → Recebimento)

```mermaid
sequenceDiagram
    participant Client
    participant ProcurementController
    participant ProcurementService
    participant QuotationRepo
    participant OrderRepo
    participant CostTransactionRepo

    Note over Client,CostTransactionRepo: 1. Criar cotação
    Client->>ProcurementController: POST /api/v1/procurement/quotations<br/>{purchaseRequestId, deadline}
    ProcurementController->>ProcurementService: createQuotation(prId, deadline)
    ProcurementService-->>Client: 201 Quotation (status: OPEN)

    Note over Client,CostTransactionRepo: 2. Fornecedores respondem
    Client->>ProcurementController: POST /api/v1/procurement/quotations/{id}/responses<br/>{supplierId, unitPrice, deliveryDays}
    ProcurementService-->>Client: 201 QuotationResponse

    Note over Client,CostTransactionRepo: 3. Análise comparativa
    Client->>ProcurementController: GET /api/v1/procurement/quotations/{id}/analysis
    ProcurementService->>ProcurementService: sort by unitPrice, pick best
    ProcurementService-->>Client: 200 {quotes[], bestPrice}

    Note over Client,CostTransactionRepo: 4. Gerar pedido (menor preço)
    Client->>ProcurementController: POST /api/v1/procurement/quotations/{id}/generate-order
    ProcurementService->>QuotationRepo: close quotation
    ProcurementService->>OrderRepo: save(PurchaseOrder)
    ProcurementService->>CostTransactionRepo: save(COMMITTED transaction)
    ProcurementService-->>Client: 201 PurchaseOrder

    Note over Client,CostTransactionRepo: 5. Recebimento
    Client->>ProcurementController: POST /api/v1/procurement/orders/{id}/receive<br/>{quantityReceived, receivedAt}
    ProcurementService->>ProcurementService: check total received vs ordered
    alt Fully received
        ProcurementService->>CostTransactionRepo: save(ACTUAL transaction)
        ProcurementService-->>Client: 200 (status: RECEIVED)
    else Partially received
        ProcurementService-->>Client: 200 (status: PARTIAL)
    end
```

## Cálculo de Custo SINAPI

```mermaid
sequenceDiagram
    participant Client
    participant CompositionController
    participant CompositionCostService
    participant CompositionRepo
    participant MaterialPriceRepo

    Client->>CompositionController: GET /api/v1/sinapi/compositions/{id}/cost<br/>?state=SC&referenceMonth=2025-03
    CompositionController->>CompositionCostService: calculate(id, state, month)
    CompositionCostService->>CompositionRepo: findWithItems(id)
    CompositionRepo-->>CompositionCostService: Composition + items[]

    loop Para cada item da composição
        CompositionCostService->>MaterialPriceRepo: findPrice(materialId, state, month)
        MaterialPriceRepo-->>CompositionCostService: price
        CompositionCostService->>CompositionCostService: itemCost = coefficient × price
    end

    CompositionCostService->>CompositionCostService: totalCost = Σ(itemCosts)
    CompositionCostService-->>Client: 200 {compositionId, totalCost, items[{material, coefficient, price, subtotal}]}
```

## EVM (Earned Value Management)

```mermaid
sequenceDiagram
    participant Client
    participant AnalyticsController
    participant EarnedValueService
    participant CostTransactionRepo
    participant ScheduleRepo

    Client->>AnalyticsController: GET /api/v1/analytics/evm/{budgetId}
    AnalyticsController->>EarnedValueService: calculate(budgetId)

    EarnedValueService->>CostTransactionRepo: findByBudgetId(budgetId)
    CostTransactionRepo-->>EarnedValueService: transactions[]

    EarnedValueService->>EarnedValueService: PV = Σ(BUDGETED)
    EarnedValueService->>EarnedValueService: EV = Σ(COMMITTED where completed)
    EarnedValueService->>EarnedValueService: AC = Σ(ACTUAL)
    EarnedValueService->>EarnedValueService: CPI = EV / AC
    EarnedValueService->>EarnedValueService: SPI = EV / PV
    EarnedValueService->>EarnedValueService: EAC = AC + (BAC - EV) / CPI
    EarnedValueService->>EarnedValueService: VAC = BAC - EAC

    EarnedValueService-->>Client: 200 {pv, ev, ac, cpi, spi, eac, vac, bac}
```

## Server-Sent Events (Notificações Real-time)

```mermaid
sequenceDiagram
    participant Client
    participant EventStreamController
    participant OperationEventPublisher
    participant BudgetService

    Client->>EventStreamController: GET /api/v1/events (Accept: text/event-stream)
    EventStreamController->>OperationEventPublisher: stream()
    OperationEventPublisher-->>Client: SSE connection established

    loop Every 15s
        OperationEventPublisher-->>Client: event: heartbeat<br/>data: {type: "Heartbeat"}
    end

    Note over BudgetService,Client: Another user creates a budget
    BudgetService->>OperationEventPublisher: publish("budget", CREATED, id, msg)
    OperationEventPublisher-->>Client: event: created<br/>data: {domain: "budget", entityId: "uuid", message: "..."}
```
