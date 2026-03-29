package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.EspecieInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class CadastroEspecieInsumoService {

	@Autowired
	private EspecieInsumosRepository repository;

	@Transactional
	public EspecieInsumo salvar(EspecieInsumo especieInsumo) {
		Optional<EspecieInsumo> existente = repository.findByNomeIgnoreCase(especieInsumo.getNome());
		if (existente.isPresent() && !existente.get().getCodigo().equals(especieInsumo.getCodigo())) {
			throw new JaCadastradoException("Espécie de insumo já cadastrada");
		}
		return repository.saveAndFlush(especieInsumo);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar espécie de insumo. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<EspecieInsumo> filtrar(EspecieInsumoFilter filtro, Pageable pageable) {
		return repository.filtrar(filtro, pageable);
	}

	public List<EspecieInsumo> findAll() {
		return repository.findAll();
	}

	public EspecieInsumo getOne(Long codigo) {
		return repository.getOne(codigo);
	}
}
