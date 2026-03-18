# Risks Register

| Date | Risk | Impact | Likelihood | Mitigation | Status | Owner |
|------|------|--------|-----------|-----------|--------|-------|
| 2026-03-18 | `Item.getValorTotal()` NPE — `valorUnitario.multiply(quantidade)` sem null check | High | High | Adicionar null check. Prioridade na Fase 1 | Open | Dev |
| 2026-03-18 | Zero cobertura de testes nos services — qualquer refatoração pode quebrar sem aviso | High | High | Adicionar testes de caracterização antes de modificar services | Open | Dev |
| 2026-03-18 | Hibernate Criteria API deprecated em todos os RepositoryImpl | Med | Low | Migrar para JPA Criteria ou JPQL quando tocar cada repository | Accepted | Dev |
| 2026-03-18 | N+1 queries em `buscarComItens` — Item.composicao e Item.insumo são EAGER | Med | Med | Refatorar query para fetch join ou usar DTOs | Open | Dev |
| 2026-03-18 | Open Session in View dependency — lazy relations acessadas no controller | Med | Med | Documentar dependência. Fetch explícito nas queries críticas | Accepted | Dev |
| 2026-03-18 | Sem autorização per-orçamento — qualquer usuário acessa qualquer orçamento | Med | Med | Implementar ownership check no service layer | Open | Dev |
| 2026-03-18 | `SinapiController.java` 731+ linhas — god class de importação | Med | Low | Extrair para service dedicado quando tocar importação | Accepted | Dev |
| 2026-03-18 | `Orcamento.java` 526 linhas — entity com cálculos inline | Med | Low | Extrair cálculos para service ou helper quando refatorar | Accepted | Dev |
| 2026-03-18 | `Lib.java` 100+ métodos estáticos — utility class legacy acoplada | Low | Low | Não usar em código novo. Migrar gradualmente | Accepted | Dev |
| 2026-03-18 | `System.out.println` com dados de usuário em OrcamentoService | Low | Med | Substituir por SLF4J logger quando tocar o service | Open | Dev |
| 2026-03-18 | Push para GitHub bloqueado — sem credenciais configuradas | Low | High | Recuperar senha GitHub ou configurar SSH key | Open | Dev |
| 2026-03-18 | Spring Boot 2.0.5 end-of-life — sem patches de segurança | High | Low | Planejar upgrade como spike separado (Fase futura) | Accepted | Dev |
