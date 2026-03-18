# Codebase Map

## Last updated
2026-03-18

## Stack
- **Language:** Java 8 (source/target 1.8)
- **Framework:** Spring Boot 2.0.5 (MVC + Thymeleaf 3.x)
- **Persistence:** Spring Data JPA / Hibernate (MySQL)
- **Security:** Spring Security (form login, roles, BCrypt)
- **Validation:** Bean Validation (JSR 303)
- **Reports:** JasperReports 6.3
- **Build:** Maven 3.x (wrapper: `mvnw`)
- **Deploy:** Heroku (Procfile)
- **Tests:** Spring Boot Test + Spring Security Test (low coverage)
- **Other:** Apache POI 3.17 (XLS import), EhCache, Thymeleaf Layout Dialect

## Structure

```
src/main/java/br/edu/ifrn/sinapiPRO/
├── config/                          ← Spring config
│   ├── SecurityConfig.java          ← Spring Security (form login, roles)
│   ├── WebConfig.java               ← MVC config, formatters
│   └── format/                      ← Number/Temporal formatters
├── controller/                      ← Spring MVC controllers (@Controller + Thymeleaf)
│   ├── AtualController.java         ← Orçamento atual (itens, etapas, impressão)
│   ├── OrcamentosController.java    ← CRUD orçamento
│   ├── ComposicaoController.java    ← CRUD composição + itens
│   ├── InsumosController.java       ← CRUD insumos
│   ├── EtapasController.java        ← CRUD etapas
│   ├── ClientesController.java      ← CRUD clientes
│   ├── ObrasController.java         ← CRUD obras
│   ├── UsuariosController.java      ← CRUD usuários
│   ├── EstadosController.java       ← CRUD estados
│   ├── CidadesController.java       ← CRUD cidades
│   ├── BasePrecosController.java    ← CRUD base preços + trigger importação
│   ├── BaseInsumosController.java   ← CRUD base insumos
│   ├── ComposicaoClassesController.java
│   ├── ComposicaoGruposController.java
│   ├── SinapiController.java        ← Importação SINAPI via XLS (731+ linhas)
│   ├── RelatoriosController.java    ← Geração de relatórios PDF
│   ├── DashboardController.java     ← Tela inicial
│   ├── SegurancaController.java     ← Login / 403
│   ├── OrcamentoValidator.java      ← Validador customizado
│   ├── converter/                   ← Type converters para form binding
│   ├── handler/                     ← ControllerAdvice exception handler
│   └── page/                        ← PageWrapper (paginação)
├── dto/                             ← Data Transfer Objects
│   ├── OrcamentoMes.java
│   ├── ComposicaoDTO.java
│   ├── ComposicaoItemDTO.java
│   ├── InsumoDTO.java
│   ├── BasePrecoItemDTO.java
│   ├── ListaInsumosDTO.java
│   ├── ListaInsumos.java            ← Filtro para relatório
│   └── ListaComposicoes.java        ← Filtro para relatório
├── model/                           ← JPA entities
│   ├── Orcamento.java               ← 526 linhas, cálculos de totais inline
│   ├── Item.java                    ← Item do orçamento (etapa/composição/insumo)
│   ├── Composicao.java              ← Composição com itens
│   ├── ComposicaoItem.java          ← Item da composição
│   ├── Insumo.java                  ← Insumo SINAPI
│   ├── Etapa.java                   ← Etapa do orçamento
│   ├── Cliente.java                 ← Cliente com CPF/CNPJ
│   ├── Obra.java                    ← Obra com endereço
│   ├── Usuario.java                 ← Usuário do sistema
│   ├── Grupo.java / Permissao.java  ← Roles e permissões
│   ├── Estado.java / Cidade.java    ← Localização
│   ├── Endereco.java                ← @Embeddable
│   ├── BaseInsumo.java              ← Base de insumos SINAPI
│   ├── BasePreco.java               ← Base de preços SINAPI
│   ├── BasePrecoItem.java           ← Preço por insumo/período
│   ├── ComposicaoClasse.java        ← Classe de composição
│   ├── ComposicaoGrupo.java         ← Grupo de composição
│   ├── Tipo.java                    ← Enum: COMPOSICAO, INSUMO, ETAPA
│   ├── Especie.java                 ← Enum: MAO_DE_OBRA, MATERIAL, EQUIPAMENTO
│   ├── Desoneracao.java             ← Enum: DESONERADO, NAODESONERADO
│   ├── OrcamentoSituacao.java       ← Enum: ABERTO, BLOQUEADO
│   ├── ComposicaoSituacao.java
│   ├── TipoPessoa.java              ← Enum: FISICA, JURIDICA (com formatação CPF/CNPJ)
│   └── validation/                  ← Custom validation annotations
├── repository/                      ← Spring Data JPA repositories
│   ├── OrcamentosRepository.java    ← extends JpaRepository + custom queries
│   ├── ItemRepository.java          ← com @Query para somas
│   ├── ComposicaoRepository.java
│   ├── InsumosRepository.java
│   ├── EtapasRepository.java
│   ├── ClientesRepository.java
│   ├── ObrasRepository.java
│   ├── UsuariosRepository.java
│   ├── filter/                      ← Query filter objects (OrcamentoFilter, etc.)
│   ├── helper/                      ← Custom query implementations (Criteria API)
│   │   ├── orcamento/OrcamentosRepositoryImpl.java
│   │   ├── item/ItemRepositoryImpl.java
│   │   ├── composicao/ComposicaoRepositoryImpl.java
│   │   ├── insumo/InsumosRepositoryImpl.java
│   │   └── ... (um impl por entidade)
│   └── paginacao/PaginacaoUtil.java
├── security/
│   ├── AppUserDetailsService.java   ← UserDetailsService implementation
│   └── UsuarioSistema.java          ← Principal customizado
├── service/                         ← Business logic
│   ├── OrcamentoService.java        ← Salvar, excluir, filtrar, buscarComItens
│   ├── ItemService.java             ← CRUD itens + somas por orçamento
│   ├── ComposicaoService.java
│   ├── InsumoService.java
│   ├── EtapaService.java
│   ├── ClienteService.java
│   ├── ObraService.java
│   ├── UsuarioService.java
│   ├── RelatorioService.java        ← 5 relatórios JasperReports
│   ├── EstadoService.java
│   ├── BasePrecoService.java / BaseInsumoService.java
│   ├── event/                       ← Application events
│   └── exception/                   ← Custom exceptions
├── session/                         ← Session-scoped beans
│   └── composicao/TabelaComposicaoItemSession.java
├── thymeleaf/                       ← Custom Thymeleaf processors
│   ├── SinapiPRODialect.java
│   └── processor/                   ← Menu, Pagination, Order, Message processors
├── utils/
│   ├── Lib.java                     ← 100+ métodos estáticos utilitários (legacy)
│   ├── Extenso.java                 ← Número por extenso
│   └── report/                      ← ReportUtil, ImprimirReport, Param, etc.
└── validation/
    └── validator/AtributoConfirmacaoValidator.java
```

## Architecture

### Layers
```
Browser → Controller (@Controller + Thymeleaf) → Service (@Service + @Transactional) → Repository (JPA + Criteria API) → MySQL
```

### Data flow
- Controllers recebem requests HTTP, fazem binding com entities JPA, delegam para services
- Services contêm lógica de negócio e boundaries transacionais
- Repositories usam Spring Data JPA + custom queries via Hibernate Criteria API (deprecated)
- Models (entities JPA) são usados diretamente nas views Thymeleaf (sem DTO para reads)
- Relatórios gerados via JasperReports (.jasper compilados em resources/relatorios/)

### Session management
- `TabelaComposicaoItemSession` mantém itens de composição em sessão HTTP durante edição
- `UsuarioSistema` (principal) carrega orçamento atual e etapa selecionada do usuário

## Patterns in use

### Naming
- Controllers: `{Entity}Controller` ou `{Entities}Controller`
- Services: `{Entity}Service` (exceção: `CadastroOrcamentoService` não existe, é `OrcamentoService`)
- Repositories: `{Entities}Repository` + `{Entities}RepositoryQueries` (interface) + `{Entities}RepositoryImpl`
- Models: singular (`Orcamento`, `Composicao`, `Insumo`)
- Filters: `{Entity}Filter`
- DTOs: `{Entity}DTO`

### Error handling
- Custom exceptions: `ImpossivelExcluirEntidadeException`, `ResourceNotFoundException`, `SenhaObrigatoriaUsuarioException`
- `ControllerAdvice` para tratamento global
- Catch genérico de `PersistenceException` em exclusões

### DI style
- Constructor injection com `@Autowired` (campos `final` no OrcamentosController após refatoração)
- Alguns controllers ainda usam field injection implícita

### Logging
- Mínimo. Alguns `System.out.println` em services (ex: OrcamentoService.findEtapaSelecionada)

### Tests
- 3 arquivos de teste existentes:
  - `SinapiPROApplicationTestes.java` — context load
  - `EtapaTeste.java` — teste de persistência
  - `UsuarioTeste.java` — teste de persistência
  - `LoginTeste.java` — teste de segurança
- Sem testes para services ou controllers

## Key entry points

### Web routes (principais)
| Route | Controller | Função |
|-------|-----------|--------|
| `/` | DashboardController | Tela inicial |
| `/login` | SegurancaController | Login |
| `/orcamentos` | OrcamentosController | Pesquisa orçamentos |
| `/orcamentos/novo` | OrcamentosController | Novo orçamento |
| `/atual/{codigo}` | AtualController | Orçamento atual (itens) |
| `/composicoes` | ComposicaoController | Pesquisa composições |
| `/insumos` | InsumosController | Pesquisa insumos |
| `/clientes` | ClientesController | CRUD clientes |
| `/obras` | ObrasController | CRUD obras |
| `/usuarios` | UsuariosController | CRUD usuários |
| `/relatorios/*` | RelatoriosController | Geração de relatórios |
| `/composicao/importa*` | SinapiController | Importação SINAPI |

### Security
- `.anyRequest().authenticated()` — tudo requer login
- `/orcamentos/novo` → requer `ROLE_CADASTRAR_ORCAMENTO`
- `/usuarios/**` → requer `ROLE_CADASTRAR_USUARIO`
- `/login` → público
- Static resources (`/layout/**`, `/images/**`) → público

## Concerns

### Bugs conhecidos
- `Item.getValorTotal()` faz `valorUnitario.multiply(quantidade)` sem null check → NPE

### God classes
- `Orcamento.java` (526 linhas) — entity + cálculos de totais inline
- `SinapiController.java` (731+ linhas) — importação XLS monolítica
- `Lib.java` (100+ métodos estáticos) — utility class legacy

### Deprecated APIs
- Todas as custom queries usam `Hibernate Criteria API` (deprecated desde Hibernate 5.2)
- `Session.createCriteria()` em todos os `*RepositoryImpl`

### Missing test coverage
- 0% nos services (OrcamentoService, ItemService, ComposicaoService, InsumoService)
- 0% nos controllers
- Apenas testes básicos de persistência e context load

### Lazy loading / N+1
- `Orcamento.baseInsumo`, `basePreco`, `estado` são `FetchType.LAZY`
- `buscarComItens()` só faz join com `itens`, não com as lazy relations
- Depende de Open Session in View (Spring Boot default `true`)
- Item → Composicao e Item → Insumo são EAGER (default @ManyToOne) → N+1 queries

### Security
- Sem autorização per-orçamento (qualquer usuário autenticado acessa qualquer orçamento)
- `System.out.println` com dados de usuário em OrcamentoService

### Shared mutable state
- `TabelaComposicaoItemSession` é session-scoped — seguro para single-user mas pode ter race conditions com tabs múltiplas

## Dependencies between modules

```
Controller → Service → Repository → Model
     ↓           ↓
   DTO        Exception
     ↓
  Thymeleaf Views (templates/)
     ↓
  Static Resources (js/, css/)

SinapiController → Apache POI → XLS files (resources/sinapi-download/)
RelatoriosController → RelatorioService → JasperReports → .jasper files
SecurityConfig → AppUserDetailsService → UsuariosRepository
```

### Cross-cutting
- `Orcamento` é referenciado por: Item, AtualController, OrcamentosController, OrcamentoService, ItemService, RelatorioService, UsuarioService (orçamento atual)
- `Usuario` é referenciado por: Orcamento, Obra, Composicao, Insumo, UsuarioService, AppUserDetailsService
- `BaseInsumo` / `BasePreco` são referenciados por: Orcamento, Composicao, Insumo, SinapiController
