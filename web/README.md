# SinapiPRO — Frontend

Angular 20 + Angular Material + ng-matero extensions

## Desenvolvimento

```bash
# Requisitos: Node 22+
nvm use 22

# Instalar dependências
npm install --legacy-peer-deps

# Rodar em dev (com mock — sem backend)
npx ng serve
# http://localhost:4200
# Login: admin@sinapipro.com / admin123

# Rodar em dev (com API real — requer backend rodando na 8080)
# Editar src/environments/environment.ts → useInMemoryApi: false
npx ng serve
```

## Build

```bash
npx ng build              # produção (usa API real)
npx ng build --watch      # dev com rebuild automático
```

## Estrutura

```
src/app/
├── core/           ← Auth, interceptors, bootstrap, settings
├── theme/          ← Layout (sidebar, header, admin-layout)
├── shared/         ← Componentes reutilizáveis, pipes, services
├── routes/         ← Módulos de negócio (lazy-loaded)
│   ├── dashboard/
│   ├── budget/     ← Orçamentos (list + form)
│   ├── sinapi/     ← Composições + Insumos
│   ├── measurement/← Medições (workflow)
│   ├── contract/   ← Contratos
│   ├── procurement/← Suprimentos (pedidos, cotações, estoque)
│   ├── schedule/   ← Cronograma
│   ├── daily-log/  ← Diário de Obra
│   ├── equipment/  ← Equipamentos
│   ├── job-costing/← Job Costing
│   ├── analytics/  ← EVM / KPIs
│   ├── supplier/   ← Fornecedores
│   ├── safety/     ← Segurança do Trabalho
│   └── settings/   ← Configurações
└── environments/
```

## Proxy (dev)

Em desenvolvimento, `/api/v1/*` é redirecionado para `http://localhost:8080` via `proxy.conf.json`.

## Modo Mock vs API Real

| Variável | Mock (dev) | API Real (prod) |
|----------|-----------|-----------------|
| `useInMemoryApi` | `true` | `false` |
| `baseUrl` | `/api/v1` | `/api/v1` |

No modo mock, auth e menu são simulados localmente. No modo real, conecta ao backend Spring Boot.
