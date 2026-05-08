package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Cotacao;
import br.edu.ifrn.sinapiPRO.repository.CotacoesRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractObraScopedCrudService;

@Service
public class CotacaoService extends AbstractObraScopedCrudService<Cotacao, CotacoesRepository> {

	private final CotacoesRepository repository;

	public CotacaoService(CotacoesRepository repository) {
		super(repository, "Impossível apagar a cotação.", "Cotação não encontrada.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Cotacao salvar(Cotacao cotacao) {
		cotacao.getItens().forEach(item -> item.setCotacao(cotacao));
		cotacao.getFornecedores().forEach(fornecedor -> fornecedor.setCotacao(cotacao));
		return repository.saveAndFlush(cotacao);
	}

	@Transactional(readOnly = true)
	public Cotacao buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
