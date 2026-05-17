
---

# 5. `.kiro/specs/gestao-obras-backend/tasks.md`

```markdown
# Tasks - Backend Sistema de Gestão de Obras

## Fase 0 - Setup técnico

- [ ] Criar projeto Spring Boot
- [ ] Configurar Maven multi-module
- [ ] Configurar PostgreSQL
- [ ] Configurar Flyway
- [ ] Configurar Testcontainers
- [ ] Configurar Spring Security
- [ ] Configurar OpenAPI/Swagger
- [ ] Configurar MapStruct
- [ ] Configurar Bean Validation
- [ ] Configurar profile local/dev/test
- [ ] Criar padrão de resposta de erro
- [ ] Criar tratamento global de exceptions
- [ ] Criar suporte a auditoria
- [ ] Criar suporte a soft delete
- [ ] Criar suporte a anexos

---

## Fase 1 - Identity, empresa, licença e permissões

- [ ] Criar entidades Empresa, Usuario, EmpresaUsuario
- [ ] Criar entidades Setor, Papel, Permissao
- [ ] Criar entidades Licenca, ModuloLicenciado, Plugin
- [ ] Criar migrations
- [ ] Criar repositórios
- [ ] Criar services de empresa
- [ ] Criar services de usuário
- [ ] Criar services de permissão
- [ ] Criar API de usuários da empresa
- [ ] Criar API de setores
- [ ] Criar API de permissões
- [ ] Criar middleware de empresa ativa
- [ ] Criar annotation `@RequiresPermission`
- [ ] Criar testes de permissão por módulo
- [ ] Criar testes de isolamento multiempresa

---

## Fase 2 - Obras

- [ ] Criar entidade Obra
- [ ] Criar entidade Cliente
- [ ] Criar entidade ObraMembro
- [ ] Criar entidade Fiscal
- [ ] Criar entidade Empreiteiro
- [ ] Criar migrations
- [ ] Criar CRUD de obras
- [ ] Criar API de membros da obra
- [ ] Criar API de fiscais
- [ ] Criar API de empreiteiros
- [ ] Criar API de anexos da obra
- [ ] Implementar validação de obra por empresa
- [ ] Implementar status da obra
- [ ] Criar testes de criação de obra
- [ ] Criar testes de vínculo com membros
- [ ] Criar testes de permissão por papel na obra

---

## Fase 3 - Base própria de insumos

- [ ] Criar entidade BasePropria
- [ ] Criar entidade InsumoProprio
- [ ] Criar entidade HistoricoPrecoInsumo
- [ ] Criar migrations
- [ ] Criar CRUD de insumos próprios
- [ ] Criar busca por código/descrição
- [ ] Criar importação XLSX de insumos
- [ ] Criar validação de colunas obrigatórias
- [ ] Criar relatório de erros de importação
- [ ] Criar atualização de preços por planilha
- [ ] Criar auditoria de alteração de preço
- [ ] Criar testes de importação válida
- [ ] Criar testes de importação com erro
- [ ] Criar testes de atualização de preço

---

## Fase 4 - Base própria de composições

- [ ] Criar entidade ComposicaoPropria
- [ ] Criar entidade ComposicaoPropriaItem
- [ ] Criar cálculo de preço da composição
- [ ] Criar CRUD de composições próprias
- [ ] Criar funcionalidade copiar composição
- [ ] Criar importação XLSX de composições
- [ ] Criar validação de insumos inexistentes
- [ ] Criar relatório de erros de importação
- [ ] Criar testes de composição com insumos
- [ ] Criar testes de cálculo de composição
- [ ] Criar testes de cópia de composição
- [ ] Criar testes de importação de composição

---

## Fase 5 - Orçamentos: estrutura base

- [ ] Criar entidade Orcamento
- [ ] Criar entidade OrcamentoBasePreco
- [ ] Criar entidade OrcamentoEtapa
- [ ] Criar entidade OrcamentoItem
- [ ] Criar entidade BDI
- [ ] Criar entidade EncargoSocial
- [ ] Criar migrations
- [ ] Criar CRUD de orçamento
- [ ] Criar API para etapas
- [ ] Criar API para adicionar composição própria
- [ ] Criar API para adicionar insumo próprio
- [ ] Criar API para busca de itens
- [ ] Implementar máscara de item
- [ ] Implementar ordenação de etapas e itens
- [ ] Criar testes de criação de orçamento
- [ ] Criar testes de criação de etapa
- [ ] Criar testes de adição de composição própria
- [ ] Criar testes de adição de insumo próprio

---

## Fase 6 - Orçamentos: cálculo

- [ ] Criar `OrcamentoCalculator`
- [ ] Criar `BdiCalculator`
- [ ] Criar `ArredondamentoCalculator`
- [ ] Implementar arredondamento geral
- [ ] Implementar arredondamento por base
- [ ] Implementar cálculo de total do item
- [ ] Implementar cálculo de total da etapa
- [ ] Implementar cálculo de total do orçamento
- [ ] Implementar recálculo transacional
- [ ] Criar testes unitários de cálculo
- [ ] Criar testes de arredondamento por base
- [ ] Criar testes de alteração de BDI
- [ ] Criar testes de alteração de quantidade

---

## Fase 7 - Orçamentos: lixeira, tags, duplicação e data base

- [ ] Criar entidade Tag
- [ ] Criar entidade OrcamentoItemTag
- [ ] Criar soft delete de orçamento
- [ ] Criar lixeira de itens
- [ ] Criar restauração de itens
- [ ] Criar exclusão definitiva
- [ ] Criar duplicação de item
- [ ] Criar duplicação de etapa
- [ ] Criar alteração de data base
- [ ] Criar detecção de divergência de data
- [ ] Criar testes de tag
- [ ] Criar testes de lixeira
- [ ] Criar testes de duplicação
- [ ] Criar testes de data base divergente

---

## Fase 8 - Orçamentos: ajuste linear

- [ ] Criar entidade OrcamentoAjusteLinear
- [ ] Criar entidade OrcamentoAjusteLinearItem
- [ ] Criar `AjusteLinearCalculator`
- [ ] Implementar modo preços e coeficientes
- [ ] Implementar modo apenas coeficientes
- [ ] Implementar preview do ajuste
- [ ] Identificar composições críticas
- [ ] Implementar confirmação transacional
- [ ] Implementar snapshot antes do ajuste
- [ ] Implementar auditoria detalhada
- [ ] Bloquear restauração automática sem snapshot
- [ ] Criar testes de preview
- [ ] Criar testes de confirmação
- [ ] Criar testes de mão de obra não alterada em preço
- [ ] Criar testes de coeficiente alterado
- [ ] Criar testes de itens críticos

---

## Fase 9 - Curva ABC e relatórios de orçamento

- [ ] Criar `CurvaAbcCalculator`
- [ ] Implementar curva ABC de insumos
- [ ] Implementar curva ABC de serviços
- [ ] Implementar relatório sintético
- [ ] Implementar relatório analítico
- [ ] Implementar exportação Excel
- [ ] Implementar exportação PDF
- [ ] Implementar personalização de relatório
- [ ] Criar testes de curva ABC
- [ ] Criar testes de relatório sintético
- [ ] Criar testes de relatório analítico

---

## Fase 10 - Sincronização base própria x orçamento

- [ ] Criar endpoint de itens próprios desatualizados
- [ ] Criar preview de atualização
- [ ] Criar atualização individual
- [ ] Criar atualização em massa
- [ ] Recalcular orçamento após sincronização
- [ ] Gerar auditoria da sincronização
- [ ] Criar testes de detecção de desatualização
- [ ] Criar testes de atualização individual
- [ ] Criar testes de atualização em massa

---

## Fase 11 - Planejamento: criação e calendário

- [ ] Criar entidade Planejamento
- [ ] Criar entidade PlanejamentoCalendario
- [ ] Criar entidade PlanejamentoFeriado
- [ ] Criar migrations
- [ ] Criar API de planejamento
- [ ] Criar API de calendário
- [ ] Criar API de feriados
- [ ] Implementar jornada diária
- [ ] Implementar jornada extra
- [ ] Implementar sábado/domingo trabalhável
- [ ] Implementar feriado trabalhável
- [ ] Criar testes de criação de planejamento
- [ ] Criar testes de calendário
- [ ] Criar testes de feriado

---

## Fase 12 - Planejamento: atividades e predecessores

- [ ] Criar entidade PlanejamentoAtividade
- [ ] Criar entidade PlanejamentoPredecessor
- [ ] Criar API de atividades
- [ ] Criar API de predecessores
- [ ] Implementar cálculo de datas
- [ ] Implementar validação de ciclo
- [ ] Implementar definição de duração
- [ ] Implementar sugestão de duração
- [ ] Implementar inicialização pelo orçamento
- [ ] Implementar atualização após mudança no orçamento
- [ ] Criar testes de predecessores
- [ ] Criar testes de ciclo inválido
- [ ] Criar testes de sugestão de duração
- [ ] Criar testes de inicialização via orçamento

---

## Fase 13 - Planejamento: acompanhamento

- [ ] Criar entidade PlanejamentoAcompanhamento
- [ ] Criar API de acompanhamento
- [ ] Criar listener de evento `TarefaDiarioAtualizada`
- [ ] Atualizar status da atividade
- [ ] Atualizar progresso por unidade
- [ ] Atualizar progresso por percentual
- [ ] Criar testes de atualização via diário
- [ ] Criar testes de status
- [ ] Criar testes de progresso

---

## Fase 14 - Diário de obras

- [ ] Criar entidade DiarioObra
- [ ] Criar entidade DiarioAtividade
- [ ] Criar entidade DiarioMaterialMovimento
- [ ] Criar entidade DiarioFoto
- [ ] Criar entidade DiarioOcorrencia
- [ ] Criar migrations
- [ ] Criar API de criação de diário
- [ ] Validar unicidade obra/data
- [ ] Implementar copiar último relatório
- [ ] Criar API de atividades do diário
- [ ] Criar API de materiais
- [ ] Criar API de fotos
- [ ] Criar API de ocorrências
- [ ] Publicar evento ao atualizar tarefa vinculada
- [ ] Criar relatório de diário
- [ ] Validar período de relatório
- [ ] Criar testes de criação de diário
- [ ] Criar testes de duplicidade obra/data
- [ ] Criar testes de cópia do último relatório
- [ ] Criar testes de integração com planejamento

---

## Fase 15 - Medição: estrutura

- [ ] Criar entidade Medicao
- [ ] Criar entidade MedicaoItem
- [ ] Criar entidade MedicaoMemoriaCalculo
- [ ] Criar entidade MedicaoAditivo
- [ ] Criar entidade MedicaoAprovacao
- [ ] Criar migrations
- [ ] Criar API de medição
- [ ] Criar API de itens medidos
- [ ] Criar API de serviço não orçado
- [ ] Criar API de aditivo
- [ ] Criar API de empreiteiro na medição
- [ ] Implementar importação de planilha
- [ ] Criar testes de criação de medição
- [ ] Criar testes de serviço não orçado
- [ ] Criar testes de aditivo
- [ ] Criar testes de importação

---

## Fase 16 - Medição: memória de cálculo e aprovação

- [ ] Implementar edição de memória de cálculo
- [ ] Recalcular quantidade medida pela memória
- [ ] Versionar memória de cálculo
- [ ] Implementar status da medição
- [ ] Bloquear edição quando aprovada/fechada
- [ ] Implementar solicitar aprovação
- [ ] Implementar aprovação por fiscal
- [ ] Implementar rejeição com justificativa
- [ ] Gerar auditoria de aprovação/rejeição
- [ ] Criar relatório de medição
- [ ] Criar testes de memória de cálculo
- [ ] Criar testes de bloqueio por status
- [ ] Criar testes de aprovação
- [ ] Criar testes de rejeição

---

## Fase 17 - Compras: pedido

- [ ] Criar entidade Fornecedor
- [ ] Criar entidade PedidoCompra
- [ ] Criar entidade PedidoCompraItem
- [ ] Criar entidade PedidoCompraAprovacao
- [ ] Criar migrations
- [ ] Criar CRUD de fornecedor
- [ ] Criar API de pedido
- [ ] Criar API de itens do pedido
- [ ] Implementar sugestão de itens do orçamento
- [ ] Preencher unidade, quantidade orçada e saldo automaticamente
- [ ] Permitir item não orçado sem quantidade orçada/saldo
- [ ] Implementar salvar e aprovar pelo requerente
- [ ] Criar testes de criação de pedido
- [ ] Criar testes de item orçado
- [ ] Criar testes de item não orçado
- [ ] Criar testes de envio ao responsável técnico

---

## Fase 18 - Compras: aprovação técnica e cotação

- [ ] Implementar aprovação técnica
- [ ] Implementar rejeição técnica
- [ ] Enviar pedido para setor de compras
- [ ] Selecionar fornecedores
- [ ] Cadastrar novo fornecedor durante pedido
- [ ] Adicionar frete por fornecedor
- [ ] Criar entidade Cotacao
- [ ] Criar entidade CotacaoFornecedor
- [ ] Criar entidade CotacaoItem
- [ ] Implementar envio de cotação
- [ ] Gerar token/link externo para fornecedor
- [ ] Enviar e-mail ao fornecedor
- [ ] Criar testes de aprovação técnica
- [ ] Criar testes de seleção de fornecedor
- [ ] Criar testes de envio de cotação
- [ ] Criar testes de token externo

---

## Fase 19 - Compras: resposta, financeiro e ordem de compra

- [ ] Implementar tela/API pública de resposta de cotação por token
- [ ] Validar expiração de token
- [ ] Registrar valores e descontos do fornecedor
- [ ] Atualizar mapa comparativo
- [ ] Implementar seleção de menor preço por item
- [ ] Implementar seleção de fornecedor mais barato
- [ ] Enviar pedido ao financeiro
- [ ] Implementar aprovação financeira
- [ ] Criar entidade OrdemCompra
- [ ] Criar entidade OrdemCompraItem
- [ ] Gerar prévia da ordem de compra
- [ ] Enviar ordem de compra ao fornecedor
- [ ] Criar testes de resposta de cotação
- [ ] Criar testes de seleção de vencedor
- [ ] Criar testes de aprovação financeira
- [ ] Criar testes de envio de ordem de compra

---

## Fase 20 - BIM / Quantificação

- [ ] Criar entidade ProjetoBim
- [ ] Criar entidade BimElemento
- [ ] Criar entidade BimQuantificacao
- [ ] Criar entidade BimCriterioQuantificacao
- [ ] Criar entidade BimMemoriaCalculo
- [ ] Criar API de projeto BIM
- [ ] Criar API de quantificação
- [ ] Criar API de critérios
- [ ] Implementar reaproveitamento de critérios
- [ ] Implementar elementos não orçados
- [ ] Implementar importação/exportação IFC como registro
- [ ] Criar endpoints para plugins
- [ ] Criar autenticação técnica para plugins
- [ ] Criar testes de quantificação
- [ ] Criar testes de elemento não orçado
- [ ] Criar testes de critérios

---

## Fase 21 - Plugins OF Elétrico e OF Estrutural

- [ ] Criar controle de acesso por plugin
- [ ] Criar entidade AvisoRoteamentoEletrico
- [ ] Criar API de recebimento de avisos elétricos
- [ ] Criar tipos de aviso elétrico
- [ ] Criar entidade ProjetoEstrutural
- [ ] Criar entidade ComandoEstruturalExecutado
- [ ] Criar API de registro de comandos estruturais
- [ ] Criar suporte a vistas e pranchas
- [ ] Criar suporte a repetições estruturais
- [ ] Criar testes de permissão de plugin
- [ ] Criar testes de aviso elétrico
- [ ] Criar testes de comando estrutural

---

## Fase 22 - Relatórios e BI

- [ ] Criar entidade ModeloRelatorio
- [ ] Criar entidade RelatorioSolicitacao
- [ ] Criar motor de geração PDF
- [ ] Criar motor de geração Excel
- [ ] Criar geração assíncrona
- [ ] Criar download seguro
- [ ] Criar indicadores de orçamento
- [ ] Criar indicadores de medição
- [ ] Criar indicadores de compras
- [ ] Criar indicadores de planejamento
- [ ] Criar indicadores de diário
- [ ] Criar testes de geração de relatório
- [ ] Criar testes de geração assíncrona
- [ ] Criar testes de indicadores

---

## Fase 23 - Qualidade, segurança e performance

- [ ] Revisar índices de banco
- [ ] Criar paginação em todas as listagens
- [ ] Criar filtros por obra, status, período e responsável
- [ ] Criar testes de autorização
- [ ] Criar testes multiempresa
- [ ] Criar testes de concorrência em aprovação
- [ ] Criar testes de concorrência em cálculo de orçamento
- [ ] Criar logs estruturados
- [ ] Criar métricas de cálculo
- [ ] Criar métricas de importação
- [ ] Criar tracing
- [ ] Criar documentação OpenAPI completa
- [ ] Criar coleção Postman/Bruno
- [ ] Criar pipeline CI
- [ ] Criar análise SonarQube
- [ ] Criar build Docker