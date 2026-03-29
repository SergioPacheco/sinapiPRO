# Project State

## Last updated
2026-03-29

## Active spec
none — all sprints complete

## Current status
Projeto feature-complete. Todos os 63 requisitos do backlog implementados. 238 source files compilando sem erros. Sprints 1-8 concluídos.

## What's implemented
- CRUD: Orçamento, Item, Composição, Insumo, Etapa, Cliente, Obra, Usuário, Estado, Cidade, BasePreco, BaseInsumo
- CRUD: TipoCusto, Tributo, Fornecedor, FornecedorInsumo
- CRUD: TipoUnidade, EspecieInsumo, TipoUsuario (Sprint 8)
- Importação SINAPI (insumos + composições via XLS)
- Relatórios JasperReports (PDF): ListaInsumos, ListaComposicoes, ImprimirComposicao, ImprimirOrcamento, OrcamentosEmitidos
- Relatórios Thymeleaf: OrcamentoAnalitico, GlobalMaterialMO, ServicosOrcamento (Sprint 5)
- Exportação: PDF, XLS (Apache POI), JSON (REST), CSV, RTF (Sprint 5)
- Spring Security: form login, roles, BCrypt, PrimeiroAcessoFilter, HistoricoSenha (Sprint 7)
- Audit trail: AuditService + AuditLog (Sprint 7)
- Taxas: BDI, Leis Sociais, Taxa Adm, BDI detalhado (insumo/servico/terceiro/ferramenta) (Sprint 3)
- Composição avançada: percentual_taxacao, tributacao, perdas, bonificacao (Sprint 3)
- Cálculo de tributos no custo final: calculaValorTributos() (Sprint 3)
- Fornecedores: CRUD + FornecedorInsumo com preço + OrigemInsumo + TipoEquipamento (Sprint 4)
- Fluxo: Estimativa → Venda → Execução (copiarOrcamento) (Sprint 6)
- Comparativo Venda vs Execução (Sprint 6)
- Thymeleaf views completas para todas as entidades

## What's NOT implemented (out of scope)
- Migração para microservices
- Reescrita do frontend (Thymeleaf → SPA)
- Módulos do Strato fora do escopo: comercial, financeiro, suprimentos, mão de obra, faturamento, frota, atendimento
- Diário de obra
- Contratos e medições de subempreiteiros
- Cronograma financeiro / planejamento físico-financeiro

## Blockers
- Push para GitHub bloqueado (token sem scope `repo`)
- Nexus corporativo indisponível (503) — compilar com `-s /tmp/settings-local.xml -o`
- Cobertura de testes baixa nos services

## Recent changes (2026-03-29)
- Sprint 8: CRUD TipoUnidade, EspecieInsumo, TipoUsuario (27 arquivos)
- Sprint 5: Relatórios Analítico, Global Material+MO, Serviços + export CSV/RTF (7 arquivos)
- Sprint 6: Comparativo Venda vs Execução (2 arquivos)

## Next steps
1. Adicionar testes unitários para os novos services
2. Integrar novas rotas no menu de navegação (LayoutPadrao)
3. Resolver blocker do Nexus/GitHub
4. Considerar Sprint 9: Cronograma financeiro + Planejamento (funcionalidades do Strato ainda não portadas)
