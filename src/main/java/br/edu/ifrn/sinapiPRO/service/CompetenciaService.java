package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Competencia;
import br.edu.ifrn.sinapiPRO.repository.CompetenciasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

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
