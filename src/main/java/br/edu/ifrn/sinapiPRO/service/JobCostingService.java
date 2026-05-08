package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Despesa;
import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.repository.DespesasRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;

/**
 * Job Costing com Earned Value Management (EVM).
 *
 * MÉTRICAS EVM (PMBOK / NBR ISO 21500):
 *
 * Valores base:
 *   PV  = Planned Value (Valor Planejado) — orçamento para o trabalho planejado
 *   EV  = Earned Value (Valor Agregado) — orçamento para o trabalho realizado
 *   AC  = Actual Cost (Custo Real) — custo real incorrido
 *   BAC = Budget at Completion — orçamento total da obra
 *
 * Variâncias:
 *   CV  = EV - AC          (Cost Variance: positivo = abaixo do orçamento)
 *   SV  = EV - PV          (Schedule Variance: positivo = adiantado)
 *
 * Índices de desempenho:
 *   CPI = EV / AC          (Cost Performance Index: > 1 = eficiente)
 *   SPI = EV / PV          (Schedule Performance Index: > 1 = adiantado)
 *
 * Previsões:
 *   EAC = BAC / CPI        (Estimate at Completion: previsão de custo final)
 *   ETC = EAC - AC         (Estimate to Complete: quanto ainda vai custar)
 *   VAC = BAC - EAC        (Variance at Completion: desvio previsto no final)
 *
 * Referência: PMBOK 7th Edition, NBR ISO 21500, McKinsey Construction Report.
 */
@Service
public class JobCostingService {

    private final ObrasRepository obraRepository;
    private final OrcamentosRepository orcamentoRepository;
    private final DespesasRepository despesaRepository;

    public JobCostingService(
            ObrasRepository obraRepository,
            OrcamentosRepository orcamentoRepository,
            DespesasRepository despesaRepository) {
        this.obraRepository = obraRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.despesaRepository = despesaRepository;
    }

    /**
     * Calcula o Job Costing completo de uma obra.
     *
     * @param codigoObra código da obra
     * @param percentualFisicoRealizado % físico realizado (0-100), informado pelo usuário
     * @return relatório EVM completo
     */
    @Transactional(readOnly = true)
    public RelatorioJobCosting calcular(Long codigoObra, BigDecimal percentualFisicoRealizado) {
        Obra obra = obraRepository.findById(codigoObra)
                .orElseThrow(() -> new RuntimeException("Obra não encontrada"));

        // BAC = soma dos orçamentos de execução vinculados à obra
        BigDecimal bac = orcamentoRepository.findAll().stream()
                .filter(o -> o.getObra() != null && o.getObra().getCodigo().equals(codigoObra))
                .map(o -> o.calculaValorTotalComTaxas())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (bac.signum() == 0) {
            throw new RuntimeException(
                    "Nenhum orçamento de execução encontrado para esta obra. "
                    + "Vincule um orçamento do tipo EXECUÇÃO à obra antes de calcular o Job Costing.");
        }

        // AC = soma das despesas pagas vinculadas à obra
        BigDecimal ac = despesaRepository.findAll().stream()
                .filter(d -> d.getObra() != null && d.getObra().getCodigo().equals(codigoObra))
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // % físico realizado (informado pelo usuário ou calculado do diário de obra)
        BigDecimal percRealizado = percentualFisicoRealizado != null
                ? percentualFisicoRealizado.min(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // EV = BAC × % físico realizado
        BigDecimal ev = bac.multiply(percRealizado)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // PV = BAC × % planejado (assumimos progresso linear se não informado)
        // Para cálculo real, o usuário deve informar o % planejado
        BigDecimal pv = ev; // simplificação: PV = EV quando não há cronograma detalhado

        // Variâncias
        BigDecimal cv = ev.subtract(ac);
        BigDecimal sv = ev.subtract(pv);

        // Índices
        BigDecimal cpi = ac.signum() > 0
                ? ev.divide(ac, 4, RoundingMode.HALF_UP)
                : BigDecimal.ONE;
        BigDecimal spi = pv.signum() > 0
                ? ev.divide(pv, 4, RoundingMode.HALF_UP)
                : BigDecimal.ONE;

        // Previsões
        BigDecimal eac = cpi.signum() > 0
                ? bac.divide(cpi, 2, RoundingMode.HALF_UP)
                : bac;
        BigDecimal etc = eac.subtract(ac);
        BigDecimal vac = bac.subtract(eac);

        // Análise por categoria de despesa
        List<CategoriaJobCosting> categorias = calcularPorCategoria(codigoObra, bac);

        RelatorioJobCosting relatorio = new RelatorioJobCosting();
        relatorio.setObra(obra.getNome());
        relatorio.setBac(bac);
        relatorio.setPv(pv);
        relatorio.setEv(ev);
        relatorio.setAc(ac);
        relatorio.setCv(cv);
        relatorio.setSv(sv);
        relatorio.setCpi(cpi);
        relatorio.setSpi(spi);
        relatorio.setEac(eac);
        relatorio.setEtc(etc);
        relatorio.setVac(vac);
        relatorio.setPercentualFisicoRealizado(percRealizado);
        relatorio.setPercentualCustoGasto(bac.signum() > 0
                ? ac.multiply(BigDecimal.valueOf(100)).divide(bac, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        relatorio.setStatusCusto(classificarStatus(cpi));
        relatorio.setStatusPrazo(classificarStatus(spi));
        relatorio.setCategorias(categorias);
        return relatorio;
    }

    private List<CategoriaJobCosting> calcularPorCategoria(Long codigoObra, BigDecimal bac) {
        // Agrupa despesas por plano de contas
        java.util.Map<String, BigDecimal> porCategoria = new java.util.LinkedHashMap<>();

        despesaRepository.findAll().stream()
                .filter(d -> d.getObra() != null && d.getObra().getCodigo().equals(codigoObra))
                .forEach(d -> {
                    String categoria = d.getPlanoContas() != null
                            ? d.getPlanoContas().getDescricao()
                            : "Sem Categoria";
                    porCategoria.merge(categoria, d.getValor(), BigDecimal::add);
                });

        return porCategoria.entrySet().stream().map(entry -> {
            CategoriaJobCosting cat = new CategoriaJobCosting();
            cat.setCategoria(entry.getKey());
            cat.setValorRealizado(entry.getValue());
            cat.setPercentualDoTotal(bac.signum() > 0
                    ? entry.getValue().multiply(BigDecimal.valueOf(100))
                            .divide(bac, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO);
            return cat;
        }).collect(Collectors.toList());
    }

    private String classificarStatus(BigDecimal indice) {
        if (indice.compareTo(new BigDecimal("1.05")) >= 0) return "EXCELENTE";
        if (indice.compareTo(BigDecimal.ONE) >= 0) return "OK";
        if (indice.compareTo(new BigDecimal("0.90")) >= 0) return "ATENCAO";
        return "CRITICO";
    }

    // ---- DTOs ----

    public static class RelatorioJobCosting {
        private String obra;
        private BigDecimal bac;   // Budget at Completion
        private BigDecimal pv;    // Planned Value
        private BigDecimal ev;    // Earned Value
        private BigDecimal ac;    // Actual Cost
        private BigDecimal cv;    // Cost Variance
        private BigDecimal sv;    // Schedule Variance
        private BigDecimal cpi;   // Cost Performance Index
        private BigDecimal spi;   // Schedule Performance Index
        private BigDecimal eac;   // Estimate at Completion
        private BigDecimal etc;   // Estimate to Complete
        private BigDecimal vac;   // Variance at Completion
        private BigDecimal percentualFisicoRealizado;
        private BigDecimal percentualCustoGasto;
        private String statusCusto;
        private String statusPrazo;
        private List<CategoriaJobCosting> categorias = new ArrayList<>();

public String getObra() {
	return obra;
}

public void setObra(String obra) {
	this.obra = obra;
}

public BigDecimal getBac() {
	return bac;
}

public void setBac(BigDecimal bac) {
	this.bac = bac;
}

public BigDecimal getPv() {
	return pv;
}

public void setPv(BigDecimal pv) {
	this.pv = pv;
}

public BigDecimal getEv() {
	return ev;
}

public void setEv(BigDecimal ev) {
	this.ev = ev;
}

public BigDecimal getAc() {
	return ac;
}

public void setAc(BigDecimal ac) {
	this.ac = ac;
}

public BigDecimal getCv() {
	return cv;
}

public void setCv(BigDecimal cv) {
	this.cv = cv;
}

public BigDecimal getSv() {
	return sv;
}

public void setSv(BigDecimal sv) {
	this.sv = sv;
}

public BigDecimal getCpi() {
	return cpi;
}

public void setCpi(BigDecimal cpi) {
	this.cpi = cpi;
}

public BigDecimal getSpi() {
	return spi;
}

public void setSpi(BigDecimal spi) {
	this.spi = spi;
}

public BigDecimal getEac() {
	return eac;
}

public void setEac(BigDecimal eac) {
	this.eac = eac;
}

public BigDecimal getEtc() {
	return etc;
}

public void setEtc(BigDecimal etc) {
	this.etc = etc;
}

public BigDecimal getVac() {
	return vac;
}

public void setVac(BigDecimal vac) {
	this.vac = vac;
}

public BigDecimal getPercentualFisicoRealizado() {
	return percentualFisicoRealizado;
}

public void setPercentualFisicoRealizado(BigDecimal percentualFisicoRealizado) {
	this.percentualFisicoRealizado = percentualFisicoRealizado;
}

public BigDecimal getPercentualCustoGasto() {
	return percentualCustoGasto;
}

public void setPercentualCustoGasto(BigDecimal percentualCustoGasto) {
	this.percentualCustoGasto = percentualCustoGasto;
}

public String getStatusCusto() {
	return statusCusto;
}

public void setStatusCusto(String statusCusto) {
	this.statusCusto = statusCusto;
}

public String getStatusPrazo() {
	return statusPrazo;
}

public void setStatusPrazo(String statusPrazo) {
	this.statusPrazo = statusPrazo;
}

public List<CategoriaJobCosting> getCategorias() {
	return categorias;
}

public void setCategorias(List<CategoriaJobCosting> categorias) {
	this.categorias = categorias;
}

    }

    public static class CategoriaJobCosting {
        private String categoria;
        private BigDecimal valorRealizado;
        private BigDecimal percentualDoTotal;

public String getCategoria() {
	return categoria;
}

public void setCategoria(String categoria) {
	this.categoria = categoria;
}

public BigDecimal getValorRealizado() {
	return valorRealizado;
}

public void setValorRealizado(BigDecimal valorRealizado) {
	this.valorRealizado = valorRealizado;
}

public BigDecimal getPercentualDoTotal() {
	return percentualDoTotal;
}

public void setPercentualDoTotal(BigDecimal percentualDoTotal) {
	this.percentualDoTotal = percentualDoTotal;
}

    }
}
