package br.edu.ifrn.sinapiPRO.service;


import java.time.LocalDateTime;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ComposicaoService {

	@Autowired
	private Composicoes composicoes;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Transactional
	public Composicao salvar(Composicao composicao) {
	
		
		if (composicao.isNova()) {
			composicao.setDataCriacao(LocalDateTime.now());
		} else {
			Composicao composicaoExistente = composicoes.getOne(composicao.getCodigo());
			composicao.setDataCriacao(composicaoExistente.getDataCriacao());
		}
		
		return composicoes.saveAndFlush(composicao);
	}
	
	@PreAuthorize("#composicao.usuario == principal.usuario or hasRole('EXCLUIR_COMPOSICAO_SINAPI')")
	@Transactional
	public void excluir(Composicao composicao) {
		try {
			composicoes.delete(composicao);
			composicoes.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Composição. Já foi usada em algum orçamento.");
		}
	}
	
}
