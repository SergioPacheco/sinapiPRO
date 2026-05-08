package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Receita;
import br.edu.ifrn.sinapiPRO.repository.ReceitasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class ReceitaService extends AbstractSimpleCrudService<Receita, ReceitasRepository> {

	private final ReceitasRepository repository;

	public ReceitaService(ReceitasRepository repository) {
		super(repository, "Impossível apagar a receita.", "Receita não encontrada.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Receita salvar(Receita receita) {
		receita.getRecebimentos().forEach(recebimento -> recebimento.setReceita(receita));
		BigDecimal totalRecebido = receita.getRecebimentos().stream()
				.map(recebimento -> recebimento.getValorRecebido())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		if (totalRecebido.compareTo(receita.getValor()) >= 0) {
			receita.setSituacao("RECEBIDA");
		} else if (totalRecebido.signum() > 0) {
			receita.setSituacao("PARCIAL");
		}
		return repository.saveAndFlush(receita);
	}

	@Transactional(readOnly = true)
	public List<Receita> findAbertas() {
		return repository.findBySituacaoOrderByDataVencimentoAsc("ABERTA");
	}

	@Transactional(readOnly = true)
	public Receita buscarComRecebimentos(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
