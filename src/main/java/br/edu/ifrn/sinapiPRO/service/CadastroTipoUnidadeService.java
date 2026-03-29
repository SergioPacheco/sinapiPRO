package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.TipoUnidade;
import br.edu.ifrn.sinapiPRO.repository.TipoUnidadesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUnidadeFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class CadastroTipoUnidadeService {

	@Autowired
	private TipoUnidadesRepository repository;

	@Transactional
	public TipoUnidade salvar(TipoUnidade tipoUnidade) {
		Optional<TipoUnidade> existente = repository.findByNomeIgnoreCase(tipoUnidade.getNome());
		if (existente.isPresent() && !existente.get().getCodigo().equals(tipoUnidade.getCodigo())) {
			throw new JaCadastradoException("Tipo de unidade já cadastrado");
		}
		return repository.saveAndFlush(tipoUnidade);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar tipo de unidade. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<TipoUnidade> filtrar(TipoUnidadeFilter filtro, Pageable pageable) {
		return repository.filtrar(filtro, pageable);
	}

	public List<TipoUnidade> findAll() {
		return repository.findAll();
	}

	public TipoUnidade getOne(Long codigo) {
		return repository.getOne(codigo);
	}
}
