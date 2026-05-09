package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Competencia;
import com.sinapipro.repository.CompetenciasRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class CompetenciaService extends AbstractSimpleCrudService<Competencia, CompetenciasRepository> {

	private final CompetenciasRepository repository;

	public CompetenciaService(CompetenciasRepository repository) {
		super(repository, "Impossível apagar a competência.", "Competência não encontrada.");
		this.repository = repository;
	}

	@Transactional
	public void encerrar(Long codigo) {
		Competencia competencia = buscarPorCodigo(codigo);
		competencia.setEncerrada(true);
		repository.saveAndFlush(competencia);
	}

	@Transactional(readOnly = true)
	public List<Competencia> findAbertas() {
		return repository.findByEncerradaFalseOrderByAnoDescMesDesc();
	}
}
