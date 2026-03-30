package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.DiarioObra;
import br.edu.ifrn.sinapiPRO.model.DiarioServico;
import br.edu.ifrn.sinapiPRO.repository.DiarioObraRepository;

/**
 * Relatório de avanço físico do Diário de Obra.
 *
 * REGRAS (práticas de gerenciamento de obras — PMBOK/NBR):
 *
 * 1. AVANÇO POR SERVIÇO:
 *    - Agrupa todos os registros de DiarioServico por descrição
 *    - Soma quantidades executadas por período
 *    - Calcula % executado acumulado
 *
 * 2. CURVA DE AVANÇO:
 *    - % executado por dia/semana/mês
 *    - Comparação com cronograma previsto (se disponível)
 *
 * 3. PRODUTIVIDADE:
 *    - Horas trabalhadas por dia (DiarioMaoObra)
 *    - Quantidade executada por hora (produtividade)
 *
 * Referência: NBR 12721, PMBOK Guide — Earned Value Management.
 */
@Service
public class AvancoFisicoService {

    @Autowired
    private DiarioObraRepository diarioRepository;

    /**
     * Gera relatório de avanço físico por serviço para uma obra.
     *
     * @param codigoObra código da obra
     * @param inicio     data inicial do período (null = desde o início)
     * @param fim        data final do período (null = até hoje)
     * @return lista de serviços com % executado acumulado
     */
    @Transactional(readOnly = true)
    public List<AvancoServico> calcularAvancoPorServico(Long codigoObra,
            LocalDate inicio, LocalDate fim) {

        List<DiarioObra> diarios = diarioRepository
                .findByObraCodigoOrderByDataDesc(codigoObra)
                .stream()
                .filter(d -> inicio == null || !d.getData().isBefore(inicio))
                .filter(d -> fim == null || !d.getData().isAfter(fim))
                .collect(Collectors.toList());

        // Agrupa serviços por descrição
        Map<String, AvancoServico> servicosMap = new LinkedHashMap<>();

        for (DiarioObra diario : diarios) {
            for (DiarioServico servico : diario.getServicos()) {
                String descricao = servico.getDescricao();
                if (descricao == null || descricao.isBlank()) continue;

                AvancoServico avanco = servicosMap.computeIfAbsent(descricao, k -> {
                    AvancoServico a = new AvancoServico();
                    a.setDescricao(k);
                    a.setUnidade(servico.getUnidade());
                    a.setQuantidadeTotal(BigDecimal.ZERO);
                    a.setPercentualAcumulado(BigDecimal.ZERO);
                    a.setRegistrosPorData(new ArrayList<>());
                    return a;
                });

                avanco.setQuantidadeTotal(avanco.getQuantidadeTotal()
                        .add(servico.getQuantidade() != null ? servico.getQuantidade() : BigDecimal.ZERO));

                // Maior % executado registrado = % acumulado
                if (servico.getPercentualExecutado() != null
                        && servico.getPercentualExecutado().compareTo(avanco.getPercentualAcumulado()) > 0) {
                    avanco.setPercentualAcumulado(servico.getPercentualExecutado());
                }

                // Registro por data
                RegistroDiario reg = new RegistroDiario();
                reg.setData(diario.getData());
                reg.setQuantidade(servico.getQuantidade() != null ? servico.getQuantidade() : BigDecimal.ZERO);
                reg.setPercentual(servico.getPercentualExecutado() != null ? servico.getPercentualExecutado() : BigDecimal.ZERO);
                avanco.getRegistrosPorData().add(reg);
            }
        }

        return new ArrayList<>(servicosMap.values());
    }

    /**
     * Calcula o avanço físico geral da obra (média ponderada dos serviços).
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularAvancoGeral(Long codigoObra) {
        List<AvancoServico> servicos = calcularAvancoPorServico(codigoObra, null, null);
        if (servicos.isEmpty()) return BigDecimal.ZERO;

        BigDecimal somaPercentuais = servicos.stream()
                .map(AvancoServico::getPercentualAcumulado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return somaPercentuais.divide(
                BigDecimal.valueOf(servicos.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Gera curva de avanço diário: % executado acumulado por data.
     */
    @Transactional(readOnly = true)
    public List<PontoCurva> gerarCurvaAvanco(Long codigoObra) {
        List<DiarioObra> diarios = diarioRepository
                .findByObraCodigoOrderByDataDesc(codigoObra);

        // Agrupa por data: média do % executado dos serviços do dia
        Map<LocalDate, List<BigDecimal>> percentuaisPorData = new LinkedHashMap<>();

        for (DiarioObra diario : diarios) {
            for (DiarioServico servico : diario.getServicos()) {
                if (servico.getPercentualExecutado() != null) {
                    percentuaisPorData
                            .computeIfAbsent(diario.getData(), k -> new ArrayList<>())
                            .add(servico.getPercentualExecutado());
                }
            }
        }

        List<PontoCurva> curva = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;

        for (Map.Entry<LocalDate, List<BigDecimal>> entry :
                percentuaisPorData.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toList())) {

            BigDecimal mediaPercentual = entry.getValue().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);

            // Usa o maior valor entre acumulado e média do dia
            if (mediaPercentual.compareTo(acumulado) > 0) {
                acumulado = mediaPercentual;
            }

            PontoCurva ponto = new PontoCurva();
            ponto.setData(entry.getKey());
            ponto.setPercentual(acumulado);
            curva.add(ponto);
        }

        return curva;
    }

    // ---- DTOs ----

    public static class AvancoServico {
        private String descricao;
        private String unidade;
        private BigDecimal quantidadeTotal;
        private BigDecimal percentualAcumulado;
        private List<RegistroDiario> registrosPorData;

public String getDescricao() {
	return descricao;
}

public void setDescricao(String descricao) {
	this.descricao = descricao;
}

public String getUnidade() {
	return unidade;
}

public void setUnidade(String unidade) {
	this.unidade = unidade;
}

public BigDecimal getQuantidadeTotal() {
	return quantidadeTotal;
}

public void setQuantidadeTotal(BigDecimal quantidadeTotal) {
	this.quantidadeTotal = quantidadeTotal;
}

public BigDecimal getPercentualAcumulado() {
	return percentualAcumulado;
}

public void setPercentualAcumulado(BigDecimal percentualAcumulado) {
	this.percentualAcumulado = percentualAcumulado;
}

public List<RegistroDiario> getRegistrosPorData() {
	return registrosPorData;
}

public void setRegistrosPorData(List<RegistroDiario> registrosPorData) {
	this.registrosPorData = registrosPorData;
}

    }

    public static class RegistroDiario {
        private LocalDate data;
        private BigDecimal quantidade;
        private BigDecimal percentual;

public LocalDate getData() {
	return data;
}

public void setData(LocalDate data) {
	this.data = data;
}

public BigDecimal getQuantidade() {
	return quantidade;
}

public void setQuantidade(BigDecimal quantidade) {
	this.quantidade = quantidade;
}

public BigDecimal getPercentual() {
	return percentual;
}

public void setPercentual(BigDecimal percentual) {
	this.percentual = percentual;
}

    }

    public static class PontoCurva {
        private LocalDate data;
        private BigDecimal percentual;

public LocalDate getData() {
	return data;
}

public void setData(LocalDate data) {
	this.data = data;
}

public BigDecimal getPercentual() {
	return percentual;
}

public void setPercentual(BigDecimal percentual) {
	this.percentual = percentual;
}

    }
}
