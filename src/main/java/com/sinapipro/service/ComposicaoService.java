package com.sinapipro.service;

import java.time.LocalDateTime;

import javax.persistence.PersistenceException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Composicao;
import com.sinapipro.model.ComposicaoSituacao;
import com.sinapipro.repository.ComposicaoRepository;
import com.sinapipro.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ComposicaoService {

	private final ComposicaoRepository composicaoRepository;

	public ComposicaoService(ComposicaoRepository composicaoRepository) {
		this.composicaoRepository = composicaoRepository;
	}
	
	@Transactional
	public Composicao salvar(Composicao composicao) {
		
		if (composicao.isNova()) {
			composicao.setDataCriacao(LocalDateTime.now());
		} else {
			Composicao composicaoExistente = composicaoRepository.getOne(composicao.getCodigo());
			composicao.setDataCriacao(composicaoExistente.getDataCriacao());
		}
		
		return composicaoRepository.saveAndFlush(composicao);
	}
	
	// @PreAuthorize("#composicao.usuario == principal.usuario or hasRole('EXCLUIR_COMPOSICAO_SINAPI')")
	@Transactional
	public void excluir(Composicao composicao) {
		try {
			composicaoRepository.delete(composicao);
			composicaoRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Composição. Já foi usada em algum orçamento.");
		}
	}
	
	// @PreAuthorize("#composicao.usuario == principal.usuario or hasRole('DESATIVAR_COMPOSICAO_SINAPI')")
	@Transactional
	public void cancelar(Composicao composicao) {
		Composicao composicaoExistente = composicaoRepository.getOne(composicao.getCodigo());
		
		composicaoExistente.setStatus(ComposicaoSituacao.CANCELADA);
		composicaoRepository.save(composicaoExistente);
	}
	
	
}
