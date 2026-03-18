# Project State

## Last updated
2026-03-18

## Active spec
none

## Current status
Projeto brownfield funcional com CRUD completo de orçamentos, composições, insumos, clientes e obras. Importação SINAPI via XLS operacional. Relatórios JasperReports (PDF) funcionando. Deploy no Heroku. Refatoração do OrcamentosController concluída (2 commits: bug fixes + refatoração estrutural). Specrail full instalado.

## What's implemented
- CRUD: Orçamento, Item, Composição, Insumo, Etapa, Cliente, Obra, Usuário, Estado, Cidade, BasePreco, BaseInsumo
- Importação SINAPI (insumos + composições via XLS)
- 5 relatórios JasperReports (PDF)
- Spring Security (form login, roles, BCrypt)
- Taxas: BDI, Leis Sociais, Taxa Adm (percentuais no orçamento)
- Thymeleaf views completas para todas as entidades

## What's NOT implemented (from requirements docs)
- Tipos de orçamento (Estimativa / Venda / Execução)
- Curva ABC
- Tributos por insumo/serviço/região
- BDI por serviço (hoje só no orçamento)
- Custos diretos/indiretos/administrativos
- Fornecedores
- Subníveis de etapas (4 níveis)
- Relatório de Serviços do Orçamento
- Exportação multi-formato (XLS, CSV, RTF, EMAIL)
- Histórico de preços (popup)
- Conversão de valores por índice
- Audit trail
- Soft delete
- REST API export JSON

## Blockers
- Push para GitHub bloqueado (token sem scope `repo` ou senha não recuperada)
- Zero cobertura de testes nos services — risco para qualquer refatoração

## Recent changes
- `4ffd503` fix(OrcamentosController): assign usuarioService, fix null check order, handle null orcamento
- `141d1e4` refactor(OrcamentosController): move business logic to service, modernize annotations

## Next steps
1. Escolher primeira feature para planejar com @planner
2. Gerar specs (requirements + design + tasks) para a feature escolhida
3. Executar tasks com feedback loops (compile + test) após cada uma
