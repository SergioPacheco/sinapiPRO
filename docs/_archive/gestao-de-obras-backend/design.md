# Design - Backend Sistema de Gestão de Obras

## Arquitetura proposta

Backend modular monolítico inicialmente, preparado para extração futura por bounded context.

Stack sugerida:

- Java 21 ou Java 25
- Spring Boot 3.x
- PostgreSQL
- Flyway
- Hibernate/JPA
- Bean Validation
- Spring Security + JWT/OAuth2
- Testcontainers
- OpenAPI/Swagger
- MapStruct
- JasperReports ou engine equivalente para relatórios
- MinIO/S3 para anexos
- RabbitMQ/Kafka opcional para eventos assíncronos

---

## Módulos Maven sugeridos

```text
gestao-obras/
  pom.xml
  app-api/
  app-domain/
  app-application/
  app-infrastructure/
  app-reporting/
  app-importer/
  app-security/
  app-bim-integration/


## Organização por pacotes
br.com.empresa.gestaoobras
  identity
  licensing
  obras
  orcamentos
  basepropria
  planejamento
  diario
  medicao
  compras
  bim
  eletrico
  estrutural
  relatorios
  shared

# Modelo de dados principal

## Identity / Licensing

empresa
usuario
empresa_usuario
setor
papel
permissao
usuario_permissao
licenca
licenca_modulo
plugin
usuario_plugin
auditoria

## Obras

obra
obra_membro
obra_fiscal
obra_empreiteiro
cliente
anexo

## Orçamento

orcamento
orcamento_base_preco
orcamento_etapa
orcamento_item
orcamento_item_tag
tag
base_preco
composicao_oficial
insumo_oficial
orcamento_item_memoria_calculo
orcamento_ajuste_linear
orcamento_ajuste_linear_item
bdi
encargo_social
lixeira_item_orcamento

## Base Própria

base_propria
insumo_proprio
composicao_propria
composicao_propria_item
importacao_planilha
importacao_planilha_erro
historico_preco_insumo_proprio

## Planejamento

planejamento
planejamento_calendario
planejamento_feriado
planejamento_atividade
planejamento_predecessor
planejamento_recurso
planejamento_acompanhamento

## Diário

diario_obra
diario_atividade
diario_material_movimento
diario_foto
diario_ocorrencia
diario_equipe
Medição

## medicao

medicao_item
medicao_memoria_calculo
medicao_aditivo
medicao_aprovacao
medicao_relatorio_fotografico

## Compras

fornecedor
pedido_compra
pedido_compra_item
pedido_compra_aprovacao
cotacao
cotacao_fornecedor
cotacao_item
ordem_compra
ordem_compra_item

## BIM

projeto_bim
bim_elemento
bim_quantificacao
bim_criterio_quantificacao
bim_memoria_calculo
bim_importacao_ifc
bim_elemento_nao_orcado


# Padrões de domínio

## Aggregate Roots

Empresa
Obra
Orçamento
BasePropria
Planejamento
DiarioObra
Medicao
PedidoCompra
ProjetoBim

## Eventos de domínio

OrcamentoCriado
OrcamentoRecalculado
ItemOrcamentoExcluido
ItemOrcamentoRestaurado
PrecoBasePropriaAtualizado
PlanejamentoCriado
AtividadePlanejamentoAtualizada
DiarioCriado
TarefaDiarioAtualizada
MedicaoEnviadaParaAprovacao
MedicaoAprovada
MedicaoRejeitada
PedidoCompraCriado
PedidoCompraAprovadoTecnicamente
CotacaoEnviada
CotacaoRespondida
PedidoEnviadoAoFinanceiro
OrdemCompraEmitida
ProjetoBimQuantificado


# Regras transacionais importantes


## Orçamento
Alteração de item deve recalcular totais da etapa e orçamento.
Alteração de BDI deve recalcular itens impactados.
Alteração de arredondamento deve recalcular itens impactados.
Ajuste linear deve ser transacional.
Ajuste linear confirmado deve gerar snapshot ou auditoria detalhada.

## Base Própria
Atualização de preço não deve alterar automaticamente orçamentos sem ação explícita.
Sincronização deve ser comandada pelo usuário.
Importações devem gerar relatório de sucesso/erro.


## Planejamento
Alteração de calendário deve recalcular datas previstas.
Diário de obra deve atualizar acompanhamento.
Predecessores não podem gerar ciclo.


## Diário
Diário deve ser único por obra/data, salvo configuração.
Atualização de tarefa vinculada deve publicar evento para planejamento.


## Medição
Medição aprovada não pode ser alterada.
Serviço não orçado deve exigir aprovação.
Memória de cálculo deve ser versionada.

## Compras
Pedido aprovado tecnicamente segue para compras.
Cotação enviada não deve ser alterada livremente.
Cotação respondida pelo fornecedor deve atualizar mapa comparativo.
Ordem de compra só pode ser emitida após aprovação financeira.

# APIs principais

## Obras
POST /api/obras
GET /api/obras
GET /api/obras/{id}
PUT /api/obras/{id}
DELETE /api/obras/{id}

POST /api/obras/{id}/membros
POST /api/obras/{id}/fiscais
POST /api/obras/{id}/empreiteiros
POST /api/obras/{id}/anexos

## Orçamentos
POST /api/orcamentos
GET /api/orcamentos
GET /api/orcamentos/{id}
PUT /api/orcamentos/{id}
DELETE /api/orcamentos/{id}

POST /api/orcamentos/{id}/etapas
POST /api/orcamentos/{id}/itens/composicoes
POST /api/orcamentos/{id}/itens/insumos
POST /api/orcamentos/{id}/recalcular
POST /api/orcamentos/{id}/ajuste-linear/preview
POST /api/orcamentos/{id}/ajuste-linear/confirmar
POST /api/orcamentos/{id}/data-base
GET /api/orcamentos/{id}/curva-abc
GET /api/orcamentos/{id}/relatorios

## Base Própria
POST /api/base-propria/insumos
POST /api/base-propria/insumos/importar
PUT /api/base-propria/insumos/{id}/preco
POST /api/base-propria/composicoes
POST /api/base-propria/composicoes/importar
POST /api/base-propria/composicoes/{id}/copiar
POST /api/base-propria/sincronizar-orcamento/{orcamentoId}


## Planejamento
POST /api/planejamentos
GET /api/planejamentos/{id}
POST /api/planejamentos/{id}/atividades
PUT /api/planejamentos/{id}/atividades/{atividadeId}
POST /api/planejamentos/{id}/predecessores
POST /api/planejamentos/{id}/feriados
GET /api/planejamentos/{id}/acompanhamento
POST /api/planejamentos/{id}/sincronizar-orcamento

##Diário de Obras
POST /api/diarios
GET /api/diarios
GET /api/diarios/{id}
PUT /api/diarios/{id}
POST /api/diarios/{id}/atividades
POST /api/diarios/{id}/materiais
POST /api/diarios/{id}/fotos
GET /api/diarios/relatorios

## Medição
POST /api/medicoes
GET /api/medicoes/{id}
POST /api/medicoes/{id}/itens
POST /api/medicoes/{id}/servicos-nao-orcados
POST /api/medicoes/{id}/aditivos
PUT /api/medicoes/{id}/itens/{itemId}/memoria-calculo
POST /api/medicoes/{id}/solicitar-aprovacao
POST /api/medicoes/{id}/aprovar
POST /api/medicoes/{id}/rejeitar
GET /api/medicoes/{id}/relatorios

## Compras
POST /api/compras/pedidos
GET /api/compras/pedidos/{id}
POST /api/compras/pedidos/{id}/itens
POST /api/compras/pedidos/{id}/salvar-aprovar
POST /api/compras/pedidos/{id}/aprovar-tecnico
POST /api/compras/pedidos/{id}/rejeitar-tecnico
POST /api/compras/pedidos/{id}/fornecedores
POST /api/compras/pedidos/{id}/cotacoes/enviar
POST /api/compras/cotacoes/{tokenFornecedor}/responder
POST /api/compras/pedidos/{id}/selecionar-vencedores
POST /api/compras/pedidos/{id}/enviar-financeiro
POST /api/compras/pedidos/{id}/aprovar-financeiro
POST /api/compras/pedidos/{id}/ordem-compra/enviar



# Estados

## Medição
RASCUNHO
ENVIADA_APROVACAO
APROVADA
REJEITADA
CANCELADA
FECHADA

Pedido de Compra
RASCUNHO
ENVIADO_RESPONSAVEL_TECNICO
APROVADO_TECNICAMENTE
REJEITADO_TECNICAMENTE
EM_COTACAO
COTADO
ENVIADO_FINANCEIRO
APROVADO_FINANCEIRO
ORDEM_COMPRA_ENVIADA
CANCELADO

Cotação
CRIADA
ENVIADA
RESPONDIDA
VENCEDORA
DESCARTADA
EXPIRADA

Diário
RASCUNHO
FECHADO
CANCELADO

Planejamento
RASCUNHO
ATIVO
CONCLUIDO
CANCELADO

## Segurança

- JWT com claims de empresa ativa.
- Controle por empresa_id.
- Controle por permissões de módulo.
- Controle por papel na obra.
- Controle por plugin/licença.
- Toda consulta deve filtrar por empresa.
- APIs de fornecedor por token devem ter escopo limitado e expiração.
- Estratégia de cálculo

Criar serviços puros de domínio:

- OrcamentoCalculator
- BdiCalculator
- ArredondamentoCalculator
- AjusteLinearCalculator
- CurvaAbcCalculator
- PlanejamentoCalculator
- MedicaoCalculator
- CotacaoComparator

Esses serviços devem ser testados com unit tests sem dependência de banco.

Estratégia de importação
- Upload da planilha
- Validação estrutural
- Parsing
- Validação semântica
- Geração de relatório de erros
- Persistência em lote
- Auditoria
- Evento de importação concluída

## Estratégia de relatórios
- Relatórios síncronos para pequenos volumes
- Relatórios assíncronos para grandes volumes
- Tabela relatorio_solicitacao
- Status: PENDENTE, PROCESSANDO, CONCLUIDO, ERRO
- Armazenar arquivo final em S3/MinIO

## Observabilidade

- Logs estruturados com correlationId
- Métricas por endpoint
- Métricas de tempo de cálculo
- Métricas de importação
- Auditoria funcional
- Tracing para operações críticas  