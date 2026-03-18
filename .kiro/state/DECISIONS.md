# Decisions Log

## [2026-03-18] Campos de service final + constructor injection no OrcamentosController
- **Context:** `usuarioService` não era atribuído no construtor, causando NPE. Campos não-final permitiam esse tipo de bug silencioso.
- **Decision:** Tornar todos os campos de service `final` e usar constructor injection explícita.
- **Alternatives considered:** Field injection com `@Autowired` (padrão em alguns controllers do projeto).
- **Consequences:** Compilador detecta campos não atribuídos. Novos controllers devem seguir este padrão.

## [2026-03-18] Mover lógica de orçamento atual para OrcamentoService
- **Context:** `OrcamentosController.atual()` e `acessaOrcamento()` faziam lookup de usuário e lógica de negócio diretamente no controller.
- **Decision:** Criar `OrcamentoService.getCodigoOrcamentoAtual()` e `selecionarOrcamento()`. Controller apenas delega.
- **Alternatives considered:** Manter no controller (padrão existente em outros controllers).
- **Consequences:** Controller mais limpo, lógica testável no service. Outros controllers com lógica similar devem ser migrados quando tocados.

## [2026-03-18] Usar @GetMapping/@PostMapping em vez de @RequestMapping
- **Context:** `OrcamentosController` usava `@RequestMapping` genérico sem especificar método HTTP.
- **Decision:** Migrar para `@GetMapping`/`@PostMapping` explícitos.
- **Alternatives considered:** Manter `@RequestMapping` para consistência com outros controllers.
- **Consequences:** Mais legível e seguro. Outros controllers devem ser migrados quando tocados (brownfield rule: refatorar só o que toca).

## [2026-03-18] Specrail full como framework de delivery
- **Context:** Projeto precisa de estrutura para planejar e executar features de forma disciplinada.
- **Decision:** Instalar specrail-for-kiro modo full (steering + agents + hooks + state + specs).
- **Alternatives considered:** Apenas steering files manuais, sem agents/hooks.
- **Consequences:** Fluxo de delivery: codebase-mapper → planner → execute → verifier. State files mantidos em `.kiro/state/`. Specs por feature em `.kiro/specs/`.

## [2026-03-18] Manter Java 8 e Spring Boot 2.0.5
- **Context:** Stack atual é Java 8 + Spring Boot 2.0.5. Upgrade seria desejável mas arriscado.
- **Decision:** Não fazer upgrade de versão major neste momento. Focar em features e estabilização.
- **Alternatives considered:** Migrar para Java 17 + Spring Boot 3.x.
- **Consequences:** Limitado a APIs Java 8. Hibernate Criteria API deprecated mas funcional. Upgrade futuro deve ser planejado como spike separado com risk assessment.

---

<!-- Add new decisions above this line -->
