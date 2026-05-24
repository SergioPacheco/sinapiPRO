package com.sinapipro.api.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Reports", description = "Geração de relatórios PDF — Financeiro, Suprimentos, Medição, Comercial, Orçamento, MO/Estoque, Gerencial")
@RestController
@RequestMapping("/api/v1/reports")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class ReportController {

    private final FinanceReportService finance;
    private final ProcurementReportService procurement;
    private final MeasurementReportService measurement;
    private final CommercialReportService commercial;
    private final BudgetReportService budget;
    private final LaborStockReportService laborStock;
    private final ManagerialReportService managerial;
    private final ServiceOrderReportService serviceOrder;
    private final AdditionalFinanceReportService addFinance;
    private final AdditionalCommercialReportService addCommercial;

    public ReportController(FinanceReportService finance, ProcurementReportService procurement,
                             MeasurementReportService measurement, CommercialReportService commercial,
                             BudgetReportService budget, LaborStockReportService laborStock,
                             ManagerialReportService managerial, ServiceOrderReportService serviceOrder,
                             AdditionalFinanceReportService addFinance, AdditionalCommercialReportService addCommercial) {
        this.finance = finance; this.procurement = procurement; this.measurement = measurement;
        this.commercial = commercial; this.budget = budget; this.laborStock = laborStock; this.managerial = managerial;
        this.serviceOrder = serviceOrder; this.addFinance = addFinance; this.addCommercial = addCommercial;
    }

    // === FINANCEIRO (Sprint 17) ===
    @Operation(summary = "Boleto bancário") @GetMapping("/finance/boleto/{installmentId}")
    public ResponseEntity<byte[]> boleto(@PathVariable UUID installmentId) { return pdf(finance.boleto(installmentId), "boleto"); }

    @Operation(summary = "Recibo de pagamento") @GetMapping("/finance/recibo/{installmentId}")
    public ResponseEntity<byte[]> recibo(@PathVariable UUID installmentId) { return pdf(finance.recibo(installmentId), "recibo"); }

    @Operation(summary = "Extrato conta corrente") @GetMapping("/finance/extrato-conta/{bankAccountId}")
    public ResponseEntity<byte[]> extratoConta(@PathVariable UUID bankAccountId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(finance.extratoConta(bankAccountId, from, to), "extrato-conta"); }

    @Operation(summary = "Extrato movimentação bancária") @GetMapping("/finance/extrato-mov/{bankAccountId}")
    public ResponseEntity<byte[]> extratoMov(@PathVariable UUID bankAccountId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(finance.extratoMovBancaria(bankAccountId, from, to), "extrato-mov"); }

    @Operation(summary = "Aging contas a pagar") @GetMapping("/finance/aging-pagar")
    public ResponseEntity<byte[]> agingPagar() { return pdf(finance.agingPagar(), "aging-pagar"); }

    @Operation(summary = "Aging contas a receber") @GetMapping("/finance/aging-receber")
    public ResponseEntity<byte[]> agingReceber() { return pdf(finance.agingReceber(), "aging-receber"); }

    @Operation(summary = "DRE por obra") @GetMapping("/finance/dre/{budgetId}")
    public ResponseEntity<byte[]> dre(@PathVariable UUID budgetId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(finance.dre(budgetId, from, to), "dre"); }

    @Operation(summary = "Fluxo de caixa projetado") @GetMapping("/finance/fluxo-caixa")
    public ResponseEntity<byte[]> fluxoCaixa(@RequestParam(defaultValue = "12") int months) { return pdf(finance.fluxoCaixa(months), "fluxo-caixa"); }

    @Operation(summary = "Balancete financeiro") @GetMapping("/finance/balancete/{budgetId}")
    public ResponseEntity<byte[]> balancete(@PathVariable UUID budgetId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(finance.balancete(budgetId, from, to), "balancete"); }

    @Operation(summary = "Mapa de custos") @GetMapping("/finance/mapa-custos/{budgetId}")
    public ResponseEntity<byte[]> mapaCustos(@PathVariable UUID budgetId) { return pdf(finance.mapaCustos(budgetId), "mapa-custos"); }

    // === SUPRIMENTOS (Sprint 18) ===
    @Operation(summary = "Requisição de compra") @GetMapping("/procurement/requisicao/{id}")
    public ResponseEntity<byte[]> requisicao(@PathVariable UUID id) { return pdf(procurement.requisicao(id), "requisicao"); }

    @Operation(summary = "Mapa comparativo cotações") @GetMapping("/procurement/mapa-comparativo/{id}")
    public ResponseEntity<byte[]> mapaComparativo(@PathVariable UUID id) { return pdf(procurement.mapaComparativo(id), "mapa-comparativo"); }

    @Operation(summary = "Pedido de compra") @GetMapping("/procurement/pedido/{id}")
    public ResponseEntity<byte[]> pedidoCompra(@PathVariable UUID id) { return pdf(procurement.pedidoCompra(id), "pedido-compra"); }

    @Operation(summary = "Pedidos em atraso") @GetMapping("/procurement/pedidos-atraso/{projectId}")
    public ResponseEntity<byte[]> pedidosAtraso(@PathVariable UUID projectId) { return pdf(procurement.pedidosAtraso(projectId), "pedidos-atraso"); }

    @Operation(summary = "Curva ABC insumos") @GetMapping("/procurement/abc-insumos/{budgetId}")
    public ResponseEntity<byte[]> abcInsumos(@PathVariable UUID budgetId) { return pdf(procurement.curvaAbcInsumos(budgetId), "abc-insumos"); }

    @Operation(summary = "Curva ABC fornecedores") @GetMapping("/procurement/abc-fornecedores/{projectId}")
    public ResponseEntity<byte[]> abcFornecedores(@PathVariable UUID projectId) { return pdf(procurement.curvaAbcFornecedores(projectId), "abc-fornecedores"); }

    @Operation(summary = "Cronograma de compras") @GetMapping("/procurement/cronograma/{projectId}")
    public ResponseEntity<byte[]> cronogramaCompras(@PathVariable UUID projectId) { return pdf(procurement.cronogramaCompras(projectId), "cronograma-compras"); }

    @Operation(summary = "Nota de recebimento") @GetMapping("/procurement/nota-recebimento/{orderId}")
    public ResponseEntity<byte[]> notaRecebimento(@PathVariable UUID orderId) { return pdf(procurement.notaRecebimento(orderId), "nota-recebimento"); }

    // === MEDIÇÃO/OBRA (Sprint 19) ===
    @Operation(summary = "Boletim medição acumulada") @GetMapping("/measurement/boletim-acumulado/{id}")
    public ResponseEntity<byte[]> boletimAcumulado(@PathVariable UUID id) { return pdf(measurement.boletimAcumulado(id), "boletim-acumulado"); }

    @Operation(summary = "Medição por empreiteiro") @GetMapping("/measurement/empreiteiro/{id}")
    public ResponseEntity<byte[]> medicaoEmpreiteiro(@PathVariable UUID id) { return pdf(measurement.medicaoEmpreiteiro(id), "medicao-empreiteiro"); }

    @Operation(summary = "Resumo medições") @GetMapping("/measurement/resumo/{projectId}")
    public ResponseEntity<byte[]> resumoMedicoes(@PathVariable UUID projectId) { return pdf(measurement.resumoMedicoes(projectId), "resumo-medicoes"); }

    @Operation(summary = "Memória de cálculo") @GetMapping("/measurement/memoria-calculo/{id}")
    public ResponseEntity<byte[]> memoriaCalculo(@PathVariable UUID id) { return pdf(measurement.memoriaCalculo(id), "memoria-calculo"); }

    @Operation(summary = "RDO completo com fotos") @GetMapping("/measurement/rdo/{id}")
    public ResponseEntity<byte[]> rdoCompleto(@PathVariable UUID id) { return pdf(measurement.rdoCompleto(id), "rdo"); }

    @Operation(summary = "Cronograma físico-financeiro") @GetMapping("/measurement/cronograma/{projectId}")
    public ResponseEntity<byte[]> cronograma(@PathVariable UUID projectId) { return pdf(measurement.cronogramaFisicoFinanceiro(projectId), "cronograma"); }

    @Operation(summary = "Curva S") @GetMapping("/measurement/curva-s/{projectId}")
    public ResponseEntity<byte[]> curvaS(@PathVariable UUID projectId) { return pdf(measurement.curvaS(projectId), "curva-s"); }

    @Operation(summary = "Diário consolidado") @GetMapping("/measurement/diario-consolidado/{projectId}")
    public ResponseEntity<byte[]> diarioConsolidado(@PathVariable UUID projectId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(measurement.diarioConsolidado(projectId, from, to), "diario-consolidado"); }

    // === COMERCIAL (Sprint 20) ===
    @Operation(summary = "Ficha do imóvel") @GetMapping("/commercial/ficha-imovel/{unitId}")
    public ResponseEntity<byte[]> fichaImovel(@PathVariable UUID unitId) { return pdf(commercial.fichaImovel(unitId), "ficha-imovel"); }

    @Operation(summary = "Contrato de venda") @GetMapping("/commercial/contrato/{id}")
    public ResponseEntity<byte[]> contratoVenda(@PathVariable UUID id) { return pdf(commercial.contratoVenda(id), "contrato-venda"); }

    @Operation(summary = "Posição de vendas") @GetMapping("/commercial/posicao-vendas/{developmentId}")
    public ResponseEntity<byte[]> posicaoVendas(@PathVariable UUID developmentId) { return pdf(commercial.posicaoVendas(developmentId), "posicao-vendas"); }

    @Operation(summary = "Extrato do cliente") @GetMapping("/commercial/extrato-cliente/{contractId}")
    public ResponseEntity<byte[]> extratoCliente(@PathVariable UUID contractId) { return pdf(commercial.extratoCliente(contractId), "extrato-cliente"); }

    @Operation(summary = "Comissões") @GetMapping("/commercial/comissoes/{developmentId}")
    public ResponseEntity<byte[]> comissoes(@PathVariable UUID developmentId) { return pdf(commercial.comissoes(developmentId), "comissoes"); }

    @Operation(summary = "Inadimplência") @GetMapping("/commercial/inadimplencia/{developmentId}")
    public ResponseEntity<byte[]> inadimplencia(@PathVariable UUID developmentId) { return pdf(commercial.inadimplencia(developmentId), "inadimplencia"); }

    @Operation(summary = "Proposta comercial") @GetMapping("/commercial/proposta/{id}")
    public ResponseEntity<byte[]> propostaComercial(@PathVariable UUID id) { return pdf(commercial.propostaComercial(id), "proposta"); }

    @Operation(summary = "Distrato") @GetMapping("/commercial/distrato/{id}")
    public ResponseEntity<byte[]> distrato(@PathVariable UUID id) { return pdf(commercial.distrato(id), "distrato"); }

    // === ORÇAMENTO (Sprint 21) ===
    @Operation(summary = "Orçamento analítico") @GetMapping("/budget/analitico/{id}")
    public ResponseEntity<byte[]> analitico(@PathVariable UUID id) { return pdf(budget.analitico(id), "orcamento-analitico"); }

    @Operation(summary = "Orçamento sintético") @GetMapping("/budget/sintetico/{id}")
    public ResponseEntity<byte[]> sintetico(@PathVariable UUID id) { return pdf(budget.sintetico(id), "orcamento-sintetico"); }

    @Operation(summary = "CPU") @GetMapping("/budget/cpu/{id}")
    public ResponseEntity<byte[]> cpu(@PathVariable UUID id) { return pdf(budget.cpu(id), "cpu"); }

    @Operation(summary = "Cronograma financeiro") @GetMapping("/budget/cronograma-financeiro/{id}")
    public ResponseEntity<byte[]> cronogramaFinanceiro(@PathVariable UUID id) { return pdf(budget.cronogramaFinanceiro(id), "cronograma-financeiro"); }

    @Operation(summary = "Análise de compras") @GetMapping("/budget/analise-compras/{id}")
    public ResponseEntity<byte[]> analiseCompras(@PathVariable UUID id) { return pdf(budget.analiseCompras(id), "analise-compras"); }

    @Operation(summary = "Comparativo orçamentos") @GetMapping("/budget/comparativo")
    public ResponseEntity<byte[]> comparativo(@RequestParam UUID id1, @RequestParam UUID id2) { return pdf(budget.comparativo(id1, id2), "comparativo"); }

    @Operation(summary = "Listagem insumos") @GetMapping("/budget/insumos/{id}")
    public ResponseEntity<byte[]> listagemInsumos(@PathVariable UUID id) { return pdf(budget.listagemInsumos(id), "listagem-insumos"); }

    @Operation(summary = "BDI detalhado") @GetMapping("/budget/bdi/{id}")
    public ResponseEntity<byte[]> bdiDetalhado(@PathVariable UUID id) { return pdf(budget.bdiDetalhado(id), "bdi-detalhado"); }

    // === MO/ESTOQUE (Sprint 22) ===
    @Operation(summary = "Folha resumo") @GetMapping("/labor/folha-resumo/{projectId}")
    public ResponseEntity<byte[]> folhaResumo(@PathVariable UUID projectId, @RequestParam LocalDate yearMonth) { return pdf(laborStock.folhaResumo(projectId, yearMonth), "folha-resumo"); }

    @Operation(summary = "Banco de horas") @GetMapping("/labor/banco-horas/{employeeId}")
    public ResponseEntity<byte[]> bancoHoras(@PathVariable UUID employeeId, @RequestParam UUID projectId) { return pdf(laborStock.bancoHoras(employeeId, projectId), "banco-horas"); }

    @Operation(summary = "Produtividade") @GetMapping("/labor/produtividade/{projectId}")
    public ResponseEntity<byte[]> produtividade(@PathVariable UUID projectId) { return pdf(laborStock.produtividade(projectId), "produtividade"); }

    @Operation(summary = "Posição estoque") @GetMapping("/stock/posicao/{projectId}")
    public ResponseEntity<byte[]> posicaoEstoque(@PathVariable UUID projectId) { return pdf(laborStock.posicaoEstoque(projectId), "posicao-estoque"); }

    @Operation(summary = "Movimentação estoque") @GetMapping("/stock/movimentacao/{projectId}")
    public ResponseEntity<byte[]> movEstoque(@PathVariable UUID projectId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(laborStock.movimentacaoEstoque(projectId, from, to), "mov-estoque"); }

    @Operation(summary = "Controle EPI") @GetMapping("/labor/epi/{employeeId}")
    public ResponseEntity<byte[]> controleEpi(@PathVariable UUID employeeId) { return pdf(laborStock.controleEpi(employeeId), "controle-epi"); }

    @Operation(summary = "Ficha equipamento") @GetMapping("/equipment/ficha/{equipmentId}")
    public ResponseEntity<byte[]> fichaEquipamento(@PathVariable UUID equipmentId) { return pdf(laborStock.fichaEquipamento(equipmentId), "ficha-equipamento"); }

    @Operation(summary = "Etiquetas patrimônio") @GetMapping("/equipment/etiquetas/{projectId}")
    public ResponseEntity<byte[]> etiquetas(@PathVariable UUID projectId) { return pdf(laborStock.etiquetasPatrimonio(projectId), "etiquetas"); }

    // === GERENCIAL (Sprint 23) ===
    @Operation(summary = "Dashboard executivo PDF") @GetMapping("/managerial/dashboard")
    public ResponseEntity<byte[]> dashboard() { return pdf(managerial.dashboardExecutivo(), "dashboard-executivo"); }

    @Operation(summary = "Gerencial resumo por obra") @GetMapping("/managerial/resumo/{projectId}")
    public ResponseEntity<byte[]> gerencialResumo(@PathVariable UUID projectId) { return pdf(managerial.gerencialResumo(projectId), "gerencial-resumo"); }

    @Operation(summary = "EVM") @GetMapping("/managerial/evm/{projectId}")
    public ResponseEntity<byte[]> evm(@PathVariable UUID projectId) { return pdf(managerial.evm(projectId), "evm"); }

    @Operation(summary = "Posição financeira consolidada") @GetMapping("/managerial/posicao-financeira")
    public ResponseEntity<byte[]> posicaoFinanceira() { return pdf(managerial.posicaoFinanceiraConsolidada(), "posicao-financeira"); }

    @Operation(summary = "Contratos vigentes") @GetMapping("/managerial/contratos/{projectId}")
    public ResponseEntity<byte[]> contratos(@PathVariable UUID projectId) { return pdf(managerial.contratos(projectId), "contratos"); }

    @Operation(summary = "Segurança") @GetMapping("/managerial/seguranca/{projectId}")
    public ResponseEntity<byte[]> seguranca(@PathVariable UUID projectId) { return pdf(managerial.seguranca(projectId), "seguranca"); }

    @Operation(summary = "Avaliação fornecedores") @GetMapping("/managerial/avaliacao-fornecedores")
    public ResponseEntity<byte[]> avaliacaoFornecedores() { return pdf(managerial.avaliacaoFornecedores(), "avaliacao-fornecedores"); }

    // === ORDEM DE SERVIÇO / ATENDIMENTO ===
    @Operation(summary = "Ficha de atendimento") @GetMapping("/serviceorder/ficha/{ticketId}")
    public ResponseEntity<byte[]> fichaAtendimento(@PathVariable UUID ticketId) { return pdf(serviceOrder.fichaAtendimento(ticketId), "ficha-atendimento"); }
    @Operation(summary = "Histórico atendimentos por cliente") @GetMapping("/serviceorder/historico/{clientId}")
    public ResponseEntity<byte[]> historicoAtendimentos(@PathVariable UUID clientId) { return pdf(serviceOrder.historicoAtendimentos(clientId), "historico-atendimentos"); }
    @Operation(summary = "Atendimentos por período") @GetMapping("/serviceorder/periodo")
    public ResponseEntity<byte[]> atendimentosPeriodo(@RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(serviceOrder.atendimentosPorPeriodo(from, to), "atendimentos-periodo"); }
    @Operation(summary = "Atendimentos por categoria") @GetMapping("/serviceorder/categorias")
    public ResponseEntity<byte[]> atendimentosCategoria() { return pdf(serviceOrder.atendimentosPorCategoria(), "atendimentos-categoria"); }
    @Operation(summary = "SLA Report") @GetMapping("/serviceorder/sla")
    public ResponseEntity<byte[]> slaReport() { return pdf(serviceOrder.slaReport(), "sla-report"); }
    @Operation(summary = "Backlog atendimentos") @GetMapping("/serviceorder/backlog")
    public ResponseEntity<byte[]> backlog() { return pdf(serviceOrder.backlogAtendimentos(), "backlog"); }
    @Operation(summary = "Solicitações pendentes") @GetMapping("/serviceorder/solicitacoes-pendentes")
    public ResponseEntity<byte[]> solicitacoesPendentes() { return pdf(serviceOrder.solicitacoesPendentes(), "solicitacoes-pendentes"); }
    @Operation(summary = "Histórico aprovações") @GetMapping("/serviceorder/aprovacoes/{projectId}")
    public ResponseEntity<byte[]> historicoAprovacoes(@PathVariable UUID projectId) { return pdf(serviceOrder.historicoAprovacoes(projectId), "historico-aprovacoes"); }

    // === FINANCEIRO ADICIONAL ===
    @Operation(summary = "Informe de rendimentos") @GetMapping("/finance/informe-rendimentos/{supplierId}")
    public ResponseEntity<byte[]> informeRendimentos(@PathVariable UUID supplierId, @RequestParam int ano) { return pdf(addFinance.informeRendimentos(supplierId, ano), "informe-rendimentos"); }
    @Operation(summary = "Distribuição despesas por obra") @GetMapping("/finance/distribuicao-despesas/{projectId}")
    public ResponseEntity<byte[]> distribuicaoDespesas(@PathVariable UUID projectId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.distribuicaoDespesas(projectId, from, to), "distribuicao-despesas"); }
    @Operation(summary = "Acompanhamento financeiro") @GetMapping("/finance/acompanhamento/{projectId}")
    public ResponseEntity<byte[]> acompanhamentoFinanceiro(@PathVariable UUID projectId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.acompanhamentoFinanceiro(projectId, from, to), "acompanhamento-financeiro"); }
    @Operation(summary = "Evolução saldo bancário") @GetMapping("/finance/evolucao-saldo/{bankAccountId}")
    public ResponseEntity<byte[]> evolucaoSaldo(@PathVariable UUID bankAccountId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.evolucaoSaldo(bankAccountId, from, to), "evolucao-saldo"); }
    @Operation(summary = "Plano de contas") @GetMapping("/finance/plano-contas")
    public ResponseEntity<byte[]> planoContas() { return pdf(addFinance.planoContas(), "plano-contas"); }
    @Operation(summary = "Razão contábil") @GetMapping("/finance/razao/{contaCode}")
    public ResponseEntity<byte[]> razaoContabil(@PathVariable String contaCode, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.razaoContabil(contaCode, from, to), "razao-contabil"); }
    @Operation(summary = "Fechamento mensal") @GetMapping("/finance/fechamento/{projectId}")
    public ResponseEntity<byte[]> fechamentoMensal(@PathVariable UUID projectId, @RequestParam LocalDate yearMonth) { return pdf(addFinance.fechamentoMensal(projectId, yearMonth), "fechamento-mensal"); }
    @Operation(summary = "Notas fiscais emitidas") @GetMapping("/finance/nf-emitidas")
    public ResponseEntity<byte[]> nfEmitidas(@RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.notasFiscaisEmitidas(from, to), "nf-emitidas"); }
    @Operation(summary = "Notas fiscais recebidas") @GetMapping("/finance/nf-recebidas")
    public ResponseEntity<byte[]> nfRecebidas(@RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.notasFiscaisRecebidas(from, to), "nf-recebidas"); }
    @Operation(summary = "Cheques emitidos") @GetMapping("/finance/cheques/{bankAccountId}")
    public ResponseEntity<byte[]> chequesEmitidos(@PathVariable UUID bankAccountId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.chequeEmitidos(bankAccountId, from, to), "cheques-emitidos"); }
    @Operation(summary = "Resumo despesas por natureza") @GetMapping("/finance/resumo-despesas/{projectId}")
    public ResponseEntity<byte[]> resumoDespesas(@PathVariable UUID projectId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addFinance.resumoDespesasPorNatureza(projectId, from, to), "resumo-despesas"); }

    // === COMERCIAL ADICIONAL ===
    @Operation(summary = "Vendas por corretor") @GetMapping("/commercial/vendas-corretor/{developmentId}")
    public ResponseEntity<byte[]> vendasCorretor(@PathVariable UUID developmentId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addCommercial.vendasPorCorretor(developmentId, from, to), "vendas-corretor"); }
    @Operation(summary = "Vendas por período") @GetMapping("/commercial/vendas-periodo/{developmentId}")
    public ResponseEntity<byte[]> vendasPeriodo(@PathVariable UUID developmentId, @RequestParam LocalDate from, @RequestParam LocalDate to) { return pdf(addCommercial.vendasPorPeriodo(developmentId, from, to), "vendas-periodo"); }
    @Operation(summary = "Vendas resumo por status") @GetMapping("/commercial/vendas-status/{developmentId}")
    public ResponseEntity<byte[]> vendasStatus(@PathVariable UUID developmentId) { return pdf(addCommercial.vendasResumoStatus(developmentId), "vendas-status"); }
    @Operation(summary = "Tabela de preços") @GetMapping("/commercial/tabela-precos/{developmentId}")
    public ResponseEntity<byte[]> tabelaPrecos(@PathVariable UUID developmentId) { return pdf(addCommercial.tabelaPrecos(developmentId), "tabela-precos"); }
    @Operation(summary = "Contrato com aditivos") @GetMapping("/commercial/contrato-aditivos/{contractId}")
    public ResponseEntity<byte[]> contratoAditivos(@PathVariable UUID contractId) { return pdf(addCommercial.contratoComAditivos(contractId), "contrato-aditivos"); }
    @Operation(summary = "Medições do contrato") @GetMapping("/commercial/medicoes-contrato/{contractId}")
    public ResponseEntity<byte[]> medicoesContrato(@PathVariable UUID contractId) { return pdf(addCommercial.medicoesDoContrato(contractId), "medicoes-contrato"); }
    @Operation(summary = "Saldo contratual") @GetMapping("/commercial/saldo-contratual/{contractId}")
    public ResponseEntity<byte[]> saldoContratual(@PathVariable UUID contractId) { return pdf(addCommercial.saldoContratual(contractId), "saldo-contratual"); }
    @Operation(summary = "Ranking fornecedores") @GetMapping("/commercial/ranking-fornecedores")
    public ResponseEntity<byte[]> rankingFornecedores() { return pdf(addCommercial.rankingFornecedores(), "ranking-fornecedores"); }
    @Operation(summary = "Plano compras mensal") @GetMapping("/procurement/plano-mensal/{projectId}")
    public ResponseEntity<byte[]> planoComprasMensal(@PathVariable UUID projectId, @RequestParam LocalDate yearMonth) { return pdf(addCommercial.planoComprasMensal(projectId, yearMonth), "plano-compras-mensal"); }
    @Operation(summary = "Custo unitário") @GetMapping("/budget/custo-unitario/{budgetId}")
    public ResponseEntity<byte[]> custoUnitario(@PathVariable UUID budgetId) { return pdf(addCommercial.custoUnitario(budgetId), "custo-unitario"); }
    @Operation(summary = "Manutenção preventiva") @GetMapping("/equipment/manutencao/{equipmentId}")
    public ResponseEntity<byte[]> manutencaoPreventiva(@PathVariable UUID equipmentId) { return pdf(addCommercial.manutencaoPreventiva(equipmentId), "manutencao-preventiva"); }
    @Operation(summary = "Ficha cliente") @GetMapping("/registry/ficha-cliente/{clientId}")
    public ResponseEntity<byte[]> fichaCliente(@PathVariable UUID clientId) { return pdf(addCommercial.fichaCliente(clientId), "ficha-cliente"); }
    @Operation(summary = "Ficha fornecedor") @GetMapping("/registry/ficha-fornecedor/{supplierId}")
    public ResponseEntity<byte[]> fichaFornecedor(@PathVariable UUID supplierId) { return pdf(addCommercial.fichaFornecedor(supplierId), "ficha-fornecedor"); }

    // Helper
    private ResponseEntity<byte[]> pdf(byte[] content, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }
}
