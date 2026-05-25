# ADR-0005 — Virtual Threads sobre Reactive (WebFlux)

## Status
Aceito

## Contexto
A API é I/O-bound (banco, email, APIs externas). Precisamos de alta concorrência sem esgotar threads do OS. Duas abordagens modernas: programação reativa (WebFlux/Reactor) ou Virtual Threads (Project Loom).

## Decisão
Usar **Virtual Threads** (Java 21+, habilitadas via `spring.threads.virtual.enabled=true`) com o modelo imperativo tradicional (Spring MVC). WebFlux usado apenas para SSE (Server-Sent Events) em notificações.

## Alternativas consideradas
1. **Spring WebFlux (Reactor)** — non-blocking, backpressure nativo. Mas: curva de aprendizado alta, debugging difícil, stack traces ilegíveis, ecossistema JDBC não é reativo nativamente.
2. **Platform Threads + pool grande** — simples, mas: 1 thread por request = limite de ~500 conexões simultâneas com 2GB de stack.
3. **Virtual Threads (escolhido)** — código imperativo (fácil de ler/debugar) + concorrência de milhares de "threads" com custo mínimo de memória.

## Consequências
### Positivas
- Código imperativo, legível, debugável
- Stack traces completas
- Compatível com JDBC/JPA (blocking I/O transparente)
- Throughput similar ao reativo para workloads I/O-bound
- Sem necessidade de tuning de thread pool

### Negativas
- Requer Java 21+ (não é problema — usamos Java 25)
- ThreadLocal funciona mas consome mais memória que ScopedValues (futuro)
- Não tem backpressure nativo (precisa implementar manualmente se necessário)
- Synchronized blocks podem pinnar carrier threads (usar ReentrantLock)

### Riscos
- Bibliotecas que usam `synchronized` internamente podem causar pinning
- ThreadLocal abuse pode consumir memória com milhares de VTs

## Como medir sucesso
- Throughput > 1000 req/s com latência p99 < 500ms em load test
- Zero thread pool exhaustion em produção
- Carrier thread pinning < 1% (monitorar via JFR)

## Plano de rollback
Voltar para platform threads com pool configurado (trivial: desabilitar `spring.threads.virtual.enabled`). Migrar para WebFlux apenas se backpressure se tornar requisito crítico.
