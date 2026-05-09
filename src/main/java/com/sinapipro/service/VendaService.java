package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Venda;
import com.sinapipro.repository.VendasRepository;
import com.sinapipro.service.support.AbstractObraScopedCrudService;

@Service
public class VendaService extends AbstractObraScopedCrudService<Venda, VendasRepository> {

	private final VendasRepository repository;
	private final ValidacaoNegocioService validacao;

	public VendaService(VendasRepository repository, ValidacaoNegocioService validacao) {
		super(repository, "Impossível apagar a venda.", "Venda não encontrada.");
		this.repository = repository;
		this.validacao = validacao;
	}

	@Override
	@Transactional
	public Venda salvar(Venda venda) {
		if (venda.getUnidade() != null && venda.isNovo()) {
			validacao.validarUnidadeDisponivel(venda.getUnidade());
		}
		if (!venda.getParcelas().isEmpty()) {
			validacao.validarParcelasSemDuplicatas(venda.getParcelas());
		}
		venda.getParcelas().forEach(parcela -> parcela.setVenda(venda));
		return repository.saveAndFlush(venda);
	}

	@Transactional(readOnly = true)
	public Venda buscarComParcelas(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
