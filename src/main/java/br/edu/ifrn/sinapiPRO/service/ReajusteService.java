package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.dto.ReajustePreviewDTO;
import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.BasePrecoItem;
import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.repository.BasePrecoItemRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;

@Service
public class ReajusteService {

	private final OrcamentoService orcamentoService;
	private final OrcamentosRepository orcamentosRepository;
	private final BasePrecosRepository basePrecosRepository;
	private final BasePrecoItemRepository basePrecoItemRepository;

	public ReajusteService(
			OrcamentoService orcamentoService,
			OrcamentosRepository orcamentosRepository,
			BasePrecosRepository basePrecosRepository,
			BasePrecoItemRepository basePrecoItemRepository) {
		this.orcamentoService = orcamentoService;
		this.orcamentosRepository = orcamentosRepository;
		this.basePrecosRepository = basePrecosRepository;
		this.basePrecoItemRepository = basePrecoItemRepository;
	}

	@Transactional(readOnly = true)
	public List<ReajustePreviewDTO> previewReajuste(Long codigoOrcamento, BigDecimal percentual, Especie especie) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		BigDecimal fator = BigDecimal.ONE.add(percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

		List<ReajustePreviewDTO> resultado = new ArrayList<>();
		for (Item item : orcamento.getItens()) {
			if (especie != null && !especie.equals(item.getEspecie())) continue;
			if (item.getValorUnitario() == null) continue;

			ReajustePreviewDTO dto = new ReajustePreviewDTO();
			dto.setCodigoItem(item.getCodigo());
			dto.setDescricao(item.getDescricao());
			dto.setValorAtual(item.getValorUnitario());
			BigDecimal novo = item.getValorUnitario().multiply(fator).setScale(4, RoundingMode.HALF_UP);
			dto.setValorNovo(novo);
			dto.setDiferenca(novo.subtract(item.getValorUnitario()));
			dto.setPercentualVariacao(percentual);
			resultado.add(dto);
		}
		return resultado;
	}

	@Transactional
	public int reajustarPercentual(Long codigoOrcamento, BigDecimal percentual, Especie especie) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		BigDecimal fator = BigDecimal.ONE.add(percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
		int count = 0;
		for (Item item : orcamento.getItens()) {
			if (especie != null && !especie.equals(item.getEspecie())) continue;
			if (item.getValorUnitario() == null) continue;
			item.setValorUnitario(item.getValorUnitario().multiply(fator).setScale(4, RoundingMode.HALF_UP));
			count++;
		}
		orcamentosRepository.saveAndFlush(orcamento);
		return count;
	}

	@Transactional
	public int reajustarValor(Long codigoOrcamento, BigDecimal valor, List<Long> codigosItens) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		int count = 0;
		for (Item item : orcamento.getItens()) {
			if (!codigosItens.contains(item.getCodigo())) continue;
			if (item.getValorUnitario() == null) continue;
			item.setValorUnitario(item.getValorUnitario().add(valor));
			count++;
		}
		orcamentosRepository.saveAndFlush(orcamento);
		return count;
	}

	@Transactional
	public int aplicarPrecoSinapi(Long codigoOrcamento, Long codigoBasePreco, boolean onerado, Especie especie) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		BasePreco basePreco = basePrecosRepository.findById(codigoBasePreco)
				.orElseThrow(() -> new RuntimeException("Base de preço não encontrada"));
		int count = 0;
		for (Item item : orcamento.getItens()) {
			if (especie != null && !especie.equals(item.getEspecie())) continue;
			if (item.getInsumo() == null) continue;

			Optional<BasePrecoItem> bpi = basePrecoItemRepository
					.findByBasePrecoAndCodigoInsumo(basePreco, item.getInsumo().getCodigoInsumo());
			if (bpi.isPresent()) {
				BigDecimal preco = onerado ? bpi.get().getPrecoOnerado() : bpi.get().getPreco();
				if (preco != null) {
					item.setValorUnitario(preco);
					count++;
				}
			}
		}
		orcamentosRepository.saveAndFlush(orcamento);
		return count;
	}
}
