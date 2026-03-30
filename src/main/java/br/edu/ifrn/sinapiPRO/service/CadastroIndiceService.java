package br.edu.ifrn.sinapiPRO.service;
import java.util.*;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.repository.IndicesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.IndiceFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroIndiceService {
	@Autowired
	private IndicesRepository repository;
	@Transactional
	public Indice salvar(Indice i) {
		Optional<Indice> e = repository.findByNomeIgnoreCase(i.getNome());
		if (e.isPresent() && !e.get().getCodigo().equals(i.getCodigo())) throw new JaCadastradoException("Índice já cadastrado");
		return repository.saveAndFlush(i);
	}
	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<Indice> filtrar(IndiceFilter f, Pageable p) {
		return repository.filtrar(f, p);
	}

	public List<Indice> findAll() {
		return repository.findAll();
	}

	public Indice getOne(Long c) {
		return repository.getOne(c);
	}
}
