# ADR-0001 — Monolito Modular sobre Microserviços

## Status
Aceito

## Contexto
SinapiPRO é um ERP de construção civil com ~30 módulos de domínio (orçamento, medição, suprimentos, financeiro, etc.). O time é pequeno (1-3 devs). O sistema está em fase de construção, sem carga de produção ainda.

Microserviços trariam isolamento de deploy e escala independente, mas com custo operacional significativo: service mesh, distributed tracing obrigatório, eventual consistency entre módulos, e complexidade de deploy multiplicada por 30.

## Decisão
Adotar **monolito modular** com vertical slicing (`api/ → application/ → domain/` por módulo). Cada módulo é um bounded context lógico dentro do mesmo processo JVM.

## Alternativas consideradas
1. **Microserviços desde o início** — overhead operacional desproporcional para o tamanho do time e estágio do produto.
2. **Monolito sem estrutura** — risco de big ball of mud conforme cresce.
3. **Modular monolith (escolhido)** — simplicidade de deploy + boundaries claras + possibilidade de extrair módulos no futuro.

## Consequências
### Positivas
- Deploy único, simples de operar
- Transações ACID entre módulos (sem saga)
- Latência zero entre módulos (in-process)
- Refactoring seguro com IDE

### Negativas
- Escala é uniforme (todos os módulos escalam juntos)
- Um bug em um módulo pode derrubar todos
- Disciplina necessária para manter boundaries (mitigado com ArchUnit)

### Riscos
- Se o time crescer para 10+ devs, conflitos de merge podem justificar split

## Como medir sucesso
- Build time < 60s
- Deploy time < 5min
- Zero acoplamento circular entre módulos (ArchUnit)
- Possibilidade de extrair 1 módulo em < 1 sprint

## Plano de rollback
Extrair módulos para serviços independentes via Strangler Fig quando houver necessidade real de escala independente.
