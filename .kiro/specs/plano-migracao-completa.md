# Plano de Migração Completa: Strato → SinapiPRO

**Data:** 2026-03-29
**Fonte:** java-strato (1.163 classes business + 758 forms + 471 relatórios)
**Destino:** sinapiPRO (238 classes Java + 88 templates HTML + 6 FTL)

---

## Dimensionamento

| Métrica | Strato | SinapiPRO Atual | Gap |
|---|---|---|---|
| Classes business | 1.163 | 238 | ~925 |
| Telas/Forms | 758 | 88 | ~670 |
| Relatórios | 471 | 6 FTL | ~465 |
| Entidades DB | ~200 tabelas (estimado) | 42 models | ~158 |
| Módulos | 23 | 8 | 15 |

---

## Fases de Migração

### FASE 1 — Orçamento Avançado (Sprint 9-11)
> Completar o core de orçamento. Dependência: nenhuma.

#### Sprint 9 — Planejamento Físico-Financeiro
**Strato:** `orcamento/planejamento/` (5 arquivos)
| Task | Strato | SinapiPRO | Arquivos |
|---|---|---|---|
| 1 | Migration V13 | tabelas: planejamento_item (orcamento, item, data_inicio, data_fim, percentual, predecessores) | 1 |
| 2 | PlanejamentoItem entity + repo | _PlanejamentoOrcamento, CalculoPlanejamento | ~6 |
| 3 | PlanejamentoService (distribuição de datas, cálculo) | _DistribuicaoDatasPlanejamento, _ParametroPlanejamento | ~2 |
| 4 | PlanejamentoController + telas | FrPlanejamentoOrcamento | ~4 |
**Estimativa:** 13 arquivos, 2 dias

#### Sprint 10 — Cronograma Financeiro + Curva S
**Strato:** `orcamento/impressaoorcamento/` (cronograma + curva S)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Relatório Cronograma Financeiro | cronograma-financeiro.ftl + endpoint |
| 2 | Relatório Curva S | curva-s.ftl + endpoint (gráfico acumulado) |
| 3 | Relatório Planejamento Físico | planejamento-fisico.ftl + endpoint |
| 4 | Tela de cronograma interativo | CronogramaFinanceiro.html (Thymeleaf) |
**Estimativa:** 8 arquivos, 2 dias

#### Sprint 11 — Reajuste de Preços + Aplicação em Lote
**Strato:** `orcamento/baseprecoinsumo/` + `orcamento/orcamento/_AplicarPreco*`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | ReajusteService (percentual + valor) | _ReajustePercentual, _ReajusteValorOrcamento |
| 2 | Aplicar preço SINAPI em lote | _AplicarPrecoInsumoOrcamento |
| 3 | Aplicar preço por classe | _AplicarPrecoClasse |
| 4 | Digitação rápida de itens | _DigitacaoRapida |
| 5 | Baseline do orçamento | _GravarBaseLine |
**Estimativa:** 12 arquivos, 2 dias

---

### FASE 2 — Cadastros Completos (Sprint 12-13)
> Migrar todos os cadastros auxiliares do Strato. Dependência: Fase 1.

#### Sprint 12 — Cadastros de Infraestrutura
**Strato:** `cadastros/cadastros/` (140 arquivos — selecionar relevantes)
| Task | Strato | SinapiPRO (CRUD 9 arquivos cada) |
|---|---|---|
| 1 | UnidadeMedida | _CadastroUnidadeMedida |
| 2 | DivisaoInsumo | _CadastroDivisaoInsumo |
| 3 | SubDivisaoInsumo | _CadastroSubDivisaoInsumo |
| 4 | Indice (INCC, IPCA, CUB) | _CadastroIndice |
| 5 | FormaPagamento | _CadastroFormaPagamento |
| 6 | TipoObra | _CadastroTipoObra |
**Estimativa:** 54 arquivos (6 × 9), 3 dias

#### Sprint 13 — Cadastros de Pessoas e Empresa
**Strato:** `cadastros/cadastros/` + `cadastros/funcionario/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Empresa (multi-empresa) | _CadastroEmpresa |
| 2 | Departamento | _CadastroDepartamento |
| 3 | Cargo | _CadastroCargo |
| 4 | Função | _CadastroFuncao |
| 5 | Funcionário | _CadastroFuncionario* (4 arquivos) |
| 6 | Endereços do cliente | _CadastroEnderecosCliente |
| 7 | Referências do cliente | _CadastroReferenciasCliente |
**Estimativa:** 63 arquivos, 3 dias

---

### FASE 3 — Operacional de Obra (Sprint 14-16)
> Módulos de acompanhamento de obra. Dependência: Fase 1.

#### Sprint 14 — Diário de Obra
**Strato:** `diariodeobra/` (21 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V14 | tabelas: diario_obra, diario_clima, diario_mao_obra, diario_equipamento, diario_ocorrencia, diario_servico |
| 2 | Entities + repos (6 entidades) | beans + _DiarioObra |
| 3 | DiarioObraService | _DiarioObra, abas/* |
| 4 | DiarioObraController + telas | FrDiarioObra (5 telas) |
| 5 | Cadastros auxiliares (Clima, Área, Acidente) | _CadastroClima, _CadastroArea, _CadastroAcidente |
**Estimativa:** 40 arquivos, 4 dias

#### Sprint 15 — Contratos e Medições
**Strato:** `geral/previsaodespesacontrato/` (22 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V15 | tabelas: contrato, contrato_item, medicao, medicao_item |
| 2 | Entities + repos | PrevisaoDespesaContrato, CamposContratoServico |
| 3 | ContratoService | _PrevisaoDespesaContrato, _Medicao |
| 4 | MedicaoService | _ContratoMedicao, _ContratoServicoMedicao |
| 5 | Controller + telas | FrPrevisaoDespesaContrato, FrMedicao |
| 6 | Relatório de medição | _OpcaoRelatorioMedicao |
**Estimativa:** 35 arquivos, 4 dias

#### Sprint 16 — Requisições de Insumos
**Strato:** `geral/requisicaoInsumos/` (5 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V16 | tabelas: requisicao, requisicao_item |
| 2 | Entities + repos + service | _RequisicaoInsumos, _MovimentaEstoque |
| 3 | Controller + telas | FrRequisicaoInsumos |
**Estimativa:** 18 arquivos, 2 dias

---

### FASE 4 — Suprimentos (Sprint 17-19)
> Cadeia de suprimentos. Dependência: Fase 2 + 3.

#### Sprint 17 — Cotações
**Strato:** `suprimentos/cotacao/` + `suprimentos/respostacotacao/` + `suprimentos/analiseCotacao/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V17 | tabelas: cotacao, cotacao_item, cotacao_fornecedor, resposta_cotacao |
| 2 | Entities + repos | _Cotacao, _CotacaoItens, _CotacaoFornecedores, _CotacaoPreco |
| 3 | CotacaoService | _Cotacao, _RespostaCotacao, _AnaliseCotacao |
| 4 | Controller + telas | FrCotacao, FrRespostaCotacao, FrAnaliseCotacao |
| 5 | Email de cotação | _CotacaoEmail |
**Estimativa:** 35 arquivos, 4 dias

#### Sprint 18 — Pedidos de Compra
**Strato:** `suprimentos/pedidos/` + `suprimentos/pedidosBaixa/` + `pedidocompra/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V18 | tabelas: pedido_compra, pedido_item, nota_fiscal |
| 2 | Entities + repos | _Pedidos, _PedidosItens, _PedidosVencimentos |
| 3 | PedidoService | _PedidoCompra, _BaixaPedido |
| 4 | Controller + telas | FrPedidoCompra, FrListaPedidoCompra |
| 5 | Relatórios | _ImprimePedido, _RelatorioPedidos |
**Estimativa:** 35 arquivos, 4 dias

#### Sprint 19 — Estoque
**Strato:** `estoque/` (9 arquivos) + `suprimentos/reprocessarestoque/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V19 | tabelas: estoque, movimento_estoque, equipamento |
| 2 | Entities + repos | _CadastroEquipamento |
| 3 | EstoqueService | movimentoestoque, posicaoestoque, inventario |
| 4 | Controller + telas + relatórios | 5 relatórios de estoque |
**Estimativa:** 30 arquivos, 3 dias

---

### FASE 5 — Financeiro (Sprint 20-23)
> Módulo financeiro completo. Dependência: Fase 4.

#### Sprint 20 — Plano de Contas e Estrutura Financeira
**Strato:** `financeiro/agrupadores/` + `cadastros/_CadastroHistoricoBancario` + `_CadastroHistoricoContabil`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V20 | tabelas: plano_contas, conta_bancaria, historico_bancario, historico_contabil, agrupador |
| 2 | Entities + repos (5 entidades) | |
| 3 | Services + controllers + telas | |
**Estimativa:** 40 arquivos, 3 dias

#### Sprint 21 — Contas a Pagar (Despesas)
**Strato:** `financeiro/despesasDiversas/` + `financeiro/pagamentoDespesa/` + `financeiro/ajustedespesa/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V21 | tabelas: despesa, pagamento_despesa |
| 2 | DespesaService | _DespesaDiversa, _DespesaDiversaPaga, _PagamentoDespesas |
| 3 | Controller + telas | |
| 4 | Autorização de pagamento | _AutorizacaoPagamento |
**Estimativa:** 30 arquivos, 3 dias

#### Sprint 22 — Contas a Receber (Receitas)
**Strato:** `financeiro/recebimentoReceitas/` + `financeiro/diversa/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V22 | tabelas: receita, recebimento_receita |
| 2 | ReceitaService | _RecebimentoReceitas, _ReceitaDiversa |
| 3 | Controller + telas | |
**Estimativa:** 25 arquivos, 2 dias

#### Sprint 23 — Movimento Bancário + Fluxo de Caixa
**Strato:** `financeiro/movimentobancario/` + `financeiro/relatorios/fluxocaixa/` + `financeiro/relatorios/caixadiario/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V23 | tabelas: movimento_bancario, saldo_conta |
| 2 | MovimentoBancarioService | _MovimentoBancario, _InicializaSaldoCaixa |
| 3 | Relatórios financeiros | fluxocaixa, caixadiario, caixabancos, balancete, DRE |
| 4 | Controller + telas | |
**Estimativa:** 40 arquivos, 4 dias

---

### FASE 6 — Comercial (Sprint 24-27)
> Módulo comercial/imobiliário. Dependência: Fase 5.

#### Sprint 24 — Unidades e Imóveis
**Strato:** `comercial/unidade/` + `comercial/imovel/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V24 | tabelas: unidade_venda, situacao_unidade, caracteristica_unidade |
| 2 | Entities + repos | TS1_UNIV, TS1_SITU, TS1_DESU |
| 3 | UnidadeService | _ConsultaUnidade, _ListarUnidades, _GerarUnidades |
| 4 | Controller + telas | espelho de vendas, ficha do imóvel |
**Estimativa:** 40 arquivos, 4 dias

#### Sprint 25 — Vendas e Incorporação
**Strato:** `comercial/vendasincorporacao/` (20 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V25 | tabelas: venda, parcela_venda, contrato_venda, proposta |
| 2 | VendaService | _VendasIncorporacao, _ParcelasVenda, _ContratoVenda |
| 3 | PropostaService | _MontarProposta, _AprovarPropostas |
| 4 | Controller + telas | |
**Estimativa:** 45 arquivos, 5 dias

#### Sprint 26 — Tabela de Preços e Comissões
**Strato:** `comercial/tabelaprecos/` + `comercial/comissao/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V26 | tabelas: tabela_preco, parcelamento, comissao |
| 2 | TabelaPrecoService | _TabelaPreco, _Parcelamentos, _Distribuicao |
| 3 | ComissaoService | _Comissao |
| 4 | Controller + telas | |
**Estimativa:** 30 arquivos, 3 dias

#### Sprint 27 — Relatórios Comerciais
**Strato:** `comercial/relatorios/` (15+ arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Mapa de vendas | _MapaVenda |
| 2 | Resumo de vendas | _ResumoVenda |
| 3 | Resumo corretor | resumocorretor/* |
| 4 | Análise de estoque | _RelatorioAnaliseEstoque |
| 5 | Relatórios diversos | impostoRenda, taxasAdicionais, prazoMedioVendas |
**Estimativa:** 20 arquivos (FTL), 3 dias

---

### FASE 7 — Mão de Obra (Sprint 28-29)
> Gestão de mão de obra. Dependência: Fase 2.

#### Sprint 28 — Cadastros e Movimentação de Horas
**Strato:** `maodeobra/` (29 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V28 | tabelas: banco_horas, movimentacao_hora, competencia, extra_obra |
| 2 | Entities + repos | |
| 3 | MovimentacaoHoraService | _MovimentacaoHora, _DigitacaoMovimentacaoHora |
| 4 | BancoHorasService | _MovimentacaoBancoHora, _CadastroBancoHoras |
| 5 | Controller + telas | |
**Estimativa:** 35 arquivos, 3 dias

#### Sprint 29 — Relatórios de MO + Prestação de Contas
**Strato:** `maodeobra/relatorios/` + `maodeobra/prestacaocontas/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Relatório funcionários | _RelatorioFuncionario |
| 2 | Relatório horas trabalhadas | _RelatorioHorasTrabalhadas |
| 3 | Relatório funções/categorias | _RelatorioFuncoes, _RelatorioCategorias |
| 4 | Prestação de contas | _PrestacaoContas |
| 5 | Etiqueta cartão ponto | _EtiquetaCartaoPonto |
**Estimativa:** 15 arquivos (FTL + endpoints), 2 dias

---

### FASE 8 — Financeiro Avançado (Sprint 30-32)
> Relatórios e operações financeiras avançadas. Dependência: Fase 5.

#### Sprint 30 — Boletos e Cobranças
**Strato:** `financas/` + `importacoes/exportacaocobranca/` + `importacoes/importacaocobranca/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Emissão de boletos | _EmissaoFichaCompensacao |
| 2 | Exportação CNAB | _ExportacaoCobranca |
| 3 | Importação retorno | _ImportacaoCobranca, _ImportacaoRecebimentos |
| 4 | Cancelamento | _CancelaBoletoBancario |
**Estimativa:** 25 arquivos, 3 dias

#### Sprint 31 — Cheques e Operações Bancárias
**Strato:** `financeiro/emissaoCheques/` + `financeiro/chequescustodia/` + `financeiro/debitoConta/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Emissão de cheques | _EmissaoCheques |
| 2 | Cheques em custódia | _ChequeCustodia |
| 3 | Débito em conta | _DebitoConta |
**Estimativa:** 20 arquivos, 2 dias

#### Sprint 32 — Relatórios Financeiros
**Strato:** `financeiro/relatorios/` (40+ arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Balancete | _RelatorioBalancete |
| 2 | DRE | _RelatorioDRE |
| 3 | Fluxo de caixa | _FluxoCaixa |
| 4 | Posição financeira | _PosicaoFinanceira |
| 5 | Mapa de custos | _MapaCusto, _MapaDespesa, _MapaReceita |
| 6 | Ficha financeira | _RelatorioFichaFinanceira |
| 7 | Razão fornecedor | _RelatorioRazao |
| 8 | Relatórios diversos | adiantamentos, créditos, liberações, evolução saldo |
**Estimativa:** 25 arquivos (FTL), 4 dias

---

### FASE 9 — Atendimento e CRM (Sprint 33-34)
> Módulo de atendimento ao cliente. Dependência: Fase 2.

#### Sprint 33 — Atendimento / Ordem de Serviço
**Strato:** `atendimento/` (32 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V33 | tabelas: atendimento, ordem_servico, previsao_atividade |
| 2 | Entities + repos | |
| 3 | AtendimentoService | _OrdemServico, _ConverteAtende |
| 4 | PrevisaoAtividadeService | previsaoatividade/*, prioridade/* |
| 5 | Controller + telas | |
**Estimativa:** 35 arquivos, 4 dias

#### Sprint 34 — Avaliação e Notificações
**Strato:** `avaliacao/` + `atendimento/notificacao/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | AvaliacaoService | _ApresentaAvaliacao |
| 2 | NotificacaoService | notificacao/* |
| 3 | Relatórios de atendimento | _RelatorioAtendimento, _ConsultaEstatistica |
**Estimativa:** 15 arquivos, 2 dias

---

### FASE 10 — Faturamento e NF (Sprint 35)
> Notas fiscais. Dependência: Fase 5.

#### Sprint 35 — Notas Fiscais
**Strato:** `faturamento/` + `modulosgerais/`
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Migration V35 | tabelas: nota_fiscal_servico, nota_fiscal_serie |
| 2 | NotaFiscalService | _NotaFiscalServico, _NotaFiscalSerieUnica |
| 3 | Relatórios | prestacaocontas, resumoprestacaocontas |
**Estimativa:** 20 arquivos, 2 dias

---

### FASE 11 — Módulos de Apoio (Sprint 36-37)
> Módulos menores. Dependência: Fase 2.

#### Sprint 36 — GED + Frota
**Strato:** `ged/` (3 arquivos) + `frota/` (1 arquivo)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | GED (upload/download de documentos) | _Ged, _GedAnexar, _ArquivosGed |
| 2 | Frota (agendamento de manutenção) | _AgendamentoManutencao |
| 3 | Cadastros de veículos | _CadastroMarcaVeiculos, _CadastroTipoVeiculos |
**Estimativa:** 20 arquivos, 2 dias

#### Sprint 37 — Relatórios Gerais
**Strato:** `relatoriogerais/` (19 arquivos)
| Task | Strato | SinapiPRO |
|---|---|---|
| 1 | Relatório clientes | _RelatorioClientes |
| 2 | Relatório fornecedores | _RelatorioFornecedores |
| 3 | Relatório obras | _RelatorioObras |
| 4 | Relatório centro de custos | _RelatorioCentroCustos |
| 5 | Mala direta | _MalaDireta |
| 6 | Ranking cliente | _RankingCliente |
| 7 | Relatórios diversos | medicoes, pedidos, naturezaOperacao, etc. |
**Estimativa:** 20 arquivos (FTL), 3 dias

---

## Resumo do Plano

| Fase | Sprints | Descrição | Arquivos | Dias |
|---|---|---|---|---|
| 1 | 9-11 | Orçamento Avançado | ~33 | 6 |
| 2 | 12-13 | Cadastros Completos | ~117 | 6 |
| 3 | 14-16 | Operacional de Obra | ~93 | 10 |
| 4 | 17-19 | Suprimentos | ~100 | 11 |
| 5 | 20-23 | Financeiro | ~135 | 12 |
| 6 | 24-27 | Comercial | ~135 | 15 |
| 7 | 28-29 | Mão de Obra | ~50 | 5 |
| 8 | 30-32 | Financeiro Avançado | ~70 | 9 |
| 9 | 33-34 | Atendimento/CRM | ~50 | 6 |
| 10 | 35 | Faturamento/NF | ~20 | 2 |
| 11 | 36-37 | Módulos de Apoio | ~40 | 5 |
| **TOTAL** | **29 sprints** | | **~843 arquivos** | **~87 dias** |

---

## Cronograma Estimado

Considerando 1 sprint = 2-5 dias úteis:

| Mês | Fases | Sprints |
|---|---|---|
| Abril 2026 | Fase 1 + 2 | Sprint 9-13 |
| Maio 2026 | Fase 3 + 4 | Sprint 14-19 |
| Junho 2026 | Fase 5 | Sprint 20-23 |
| Julho 2026 | Fase 6 | Sprint 24-27 |
| Agosto 2026 | Fase 7 + 8 | Sprint 28-32 |
| Setembro 2026 | Fase 9 + 10 + 11 | Sprint 33-37 |

**Previsão de conclusão: Setembro 2026** (6 meses)

---

## Premissas

1. Cada CRUD segue o padrão estabelecido (9 arquivos: Model + Repository + Filter + Queries + Impl + Service + Controller + 2 templates)
2. Relatórios usam FreeMarker + Flying Saucer (padrão sgn3)
3. Stack mantida: Spring Boot 2.7 + Thymeleaf + Spring Data JPA + MySQL
4. Sem migração de frontend (Thymeleaf mantido)
5. Sem migração de dados — apenas estrutura e funcionalidades
6. Testes unitários adicionados incrementalmente por sprint

## Riscos

| Risco | Mitigação |
|---|---|
| Regras de negócio complexas no Strato (SQL concatenado) | Reescrever com Criteria API / JPQL |
| Módulo financeiro tem muitas interdependências | Implementar em ordem: plano de contas → despesas → receitas → movimento |
| Módulo comercial é muito específico (imobiliário) | Adaptar para contexto genérico de obras |
| Volume de relatórios (471 no Strato) | Migrar apenas os essenciais por módulo |
| Nexus corporativo indisponível | Manter settings-local.xml para compilação offline |
