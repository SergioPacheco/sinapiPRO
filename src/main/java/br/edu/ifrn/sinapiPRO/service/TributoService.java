package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Tributo;
import br.edu.ifrn.sinapiPRO.repository.TributosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TributoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class TributoService {

	@Autowired
	private TributosRepository repository;

	@Transactional
	public Tributo salvar(Tributo tributo) {
		Optional<Tributo> existente = repository.findByDescricaoIgnoreCase(tributo.getDescricao());
		if (existente.isPresent() && !existente.get().getCodigo().equals(tributo.getCodigo())) {
			throw new JaCadastradoException("Tributo já cadastrado");
		}
		return repository.saveAndFlush(tributo);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar tributo. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<Tributo> filtrar(TributoFilter filtro, Pageable pageable) {
		return repository.filtrar(filtro, pageable);
	}

	public List<Tributo> findAll() {
		return repository.findAll();
	}

	public Tributo getOne(Long codigo) {
		return repository.getOne(codigo);
	}
}
