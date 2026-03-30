package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.dto.CronogramaMes;
import br.edu.ifrn.sinapiPRO.dto.PlanejamentoFisicoDTO;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.PlanejamentoItem;
import br.edu.ifrn.sinapiPRO.repository.PlanejamentoItemRepository;

@Service
public class PlanejamentoService {

	@Autowired
	private PlanejamentoItemRepository repository;

	@Autowired
	private OrcamentoService orcamentoService;

	@Transactional(readOnly = true)
	public List<PlanejamentoItem> buscarPorOrcamento(Long codigoOrcamento) {
		return repository.findByOrcamentoCodigo(codigoOrcamento);
	}

	@Transactional
	public void salvar(Long codigoOrcamento, List<PlanejamentoItem> itens) {
		repository.deleteByOrcamentoCodigo(codigoOrcamento);
		repository.flush();
		repository.saveAll(itens);
	}

	/**
	 * Calcula cronograma financeiro: distribuição linear de custos por mês.
	 * Distribuição linear de custos por período
	 */
	@Transactional(readOnly = true)
	public List<CronogramaMes> calcularCronograma(Long codigoOrcamento) {
		List<PlanejamentoItem> planejamento = repository.findByOrcamentoCodigo(codigoOrcamento);
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);

		Map<String, CronogramaMes> meses = new LinkedHashMap<>();

		for (PlanejamentoItem pi : planejamento) {
			if (pi.getDataInicio() == null || pi.getDataFim() == null) continue;

			Item item = pi.getItem();
			BigDecimal valorTotal = item.getValorTotal() != null ? item.getValorTotal() : BigDecimal.ZERO;
			if (valorTotal.signum() == 0) continue;

			long totalMeses = ChronoUnit.MONTHS.between(
					pi.getDataInicio().withDayOfMonth(1),
					pi.getDataFim().withDayOfMonth(1)) + 1;
			if (totalMeses <= 0) totalMeses = 1;

			BigDecimal valorMensal = valorTotal.divide(BigDecimal.valueOf(totalMeses), 2, RoundingMode.HALF_UP);

			LocalDate cursor = pi.getDataInicio().withDayOfMonth(1);
			LocalDate fim = pi.getDataFim().withDayOfMonth(1);
			while (!cursor.isAfter(fim)) {
				String chave = String.format("%d-%02d", cursor.getYear(), cursor.getMonthValue());
				final int year = cursor.getYear();
				final int month = cursor.getMonthValue();
				meses.computeIfAbsent(chave, k -> new CronogramaMes(year, month));
				meses.get(chave).adicionarValor(valorMensal);
				cursor = cursor.plusMonths(1);
			}
		}

		// Calcular acumulado e percentual
		BigDecimal totalGeral = orcamento.calculaValorTotalComTaxas();
		BigDecimal acumulado = BigDecimal.ZERO;
		List<CronogramaMes> resultado = new ArrayList<>(meses.values());
		for (CronogramaMes cm : resultado) {
			acumulado = acumulado.add(cm.getValorPlanejado());
			cm.setValorAcumulado(acumulado);
			cm.setPercentual(totalGeral.signum() != 0
					? acumulado.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 2, RoundingMode.HALF_UP)
					: BigDecimal.ZERO);
		}

		return resultado;
	}

	@Transactional(readOnly = true)
	public List<PlanejamentoFisicoDTO> montarPlanejamentoFisico(Long codigoOrcamento) {
		List<PlanejamentoItem> planejamento = repository.findByOrcamentoCodigo(codigoOrcamento);
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		BigDecimal totalGeral = orcamento.calculaValorTotalComTaxas();

		List<PlanejamentoFisicoDTO> resultado = new ArrayList<>();
		for (PlanejamentoItem pi : planejamento) {
			if (pi.getDataInicio() == null || pi.getDataFim() == null) continue;
			Item item = pi.getItem();

			PlanejamentoFisicoDTO dto = new PlanejamentoFisicoDTO();
			dto.setItemizacao(item.getItemizacao());
			dto.setDescricao(item.getDescricao());
			dto.setEtapa(item.getEtapa() != null ? item.getEtapa().getNome() : "Sem Etapa");
			dto.setDataInicio(pi.getDataInicio());
			dto.setDataFim(pi.getDataFim());

			long meses = ChronoUnit.MONTHS.between(
					pi.getDataInicio().withDayOfMonth(1),
					pi.getDataFim().withDayOfMonth(1)) + 1;
			dto.setDuracaoMeses(meses > 0 ? (int) meses : 1);

			BigDecimal valor = item.getValorTotal() != null ? item.getValorTotal() : BigDecimal.ZERO;
			dto.setValor(valor);
			dto.setPercentualDoTotal(totalGeral.signum() != 0
					? valor.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 2, RoundingMode.HALF_UP)
					: BigDecimal.ZERO);
			resultado.add(dto);
		}
		return resultado;
	}
}
