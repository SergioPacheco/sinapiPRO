package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.dto.BaselineComparativoDTO;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.OrcamentoBaseline;
import br.edu.ifrn.sinapiPRO.model.OrcamentoBaselineItem;
import br.edu.ifrn.sinapiPRO.repository.OrcamentoBaselineRepository;

@Service
public class BaselineService {

	private final OrcamentoBaselineRepository baselineRepository;
	private final OrcamentoService orcamentoService;

	public BaselineService(OrcamentoBaselineRepository baselineRepository, OrcamentoService orcamentoService) {
		this.baselineRepository = baselineRepository;
		this.orcamentoService = orcamentoService;
	}

	@Transactional
	public OrcamentoBaseline gravarBaseline(Long codigoOrcamento, String descricao) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);

		OrcamentoBaseline baseline = new OrcamentoBaseline();
		baseline.setOrcamento(orcamento);
		baseline.setDescricao(descricao);
		baseline.setDataGravacao(LocalDateTime.now());
		baseline.setValorTotal(orcamento.calculaValorTotalComTaxas());

		for (Item item : orcamento.getItens()) {
			OrcamentoBaselineItem bi = new OrcamentoBaselineItem();
			bi.setBaseline(baseline);
			bi.setItem(item);
			bi.setValorUnitario(item.getValorUnitario());
			bi.setQuantidade(item.getQuantidade());
			bi.setValorTotal(item.getValorTotal());
			baseline.getItens().add(bi);
		}

		return baselineRepository.saveAndFlush(baseline);
	}

	@Transactional(readOnly = true)
	public List<OrcamentoBaseline> listarBaselines(Long codigoOrcamento) {
		return baselineRepository.findByOrcamentoCodigoOrderByDataGravacaoDesc(codigoOrcamento);
	}

	@Transactional(readOnly = true)
	public List<BaselineComparativoDTO> compararBaseline(Long codigoBaseline) {
		OrcamentoBaseline baseline = baselineRepository.findById(codigoBaseline)
				.orElseThrow(() -> new RuntimeException("Baseline não encontrado"));
		Orcamento orcamento = orcamentoService.buscarComItens(baseline.getOrcamento().getCodigo());

		List<BaselineComparativoDTO> resultado = new ArrayList<>();
		for (OrcamentoBaselineItem bi : baseline.getItens()) {
			Item itemAtual = orcamento.getItens().stream()
					.filter(i -> i.getCodigo().equals(bi.getItem().getCodigo()))
					.findFirst().orElse(null);
			if (itemAtual == null) continue;

			BaselineComparativoDTO dto = new BaselineComparativoDTO();
			dto.setDescricao(itemAtual.getDescricao());
			BigDecimal vBase = bi.getValorTotal() != null ? bi.getValorTotal() : BigDecimal.ZERO;
			BigDecimal vAtual = itemAtual.getValorTotal() != null ? itemAtual.getValorTotal() : BigDecimal.ZERO;
			dto.setValorBaseline(vBase);
			dto.setValorAtual(vAtual);
			dto.setDiferenca(vAtual.subtract(vBase));
			dto.setPercentualVariacao(vBase.signum() != 0
					? vAtual.subtract(vBase).multiply(BigDecimal.valueOf(100)).divide(vBase, 2, RoundingMode.HALF_UP)
					: BigDecimal.ZERO);
			resultado.add(dto);
		}
		return resultado;
	}
}
