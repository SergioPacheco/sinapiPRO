package br.edu.ifrn.sinapiPRO.service;
import java.util.*;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Funcao;
import br.edu.ifrn.sinapiPRO.repository.FuncoesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncaoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroFuncaoService {
	@Autowired
	private FuncoesRepository repository;
	@Transactional
	public Funcao salvar(Funcao e) {
		Optional<Funcao> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("Funcao já cadastrado(a)");
		return repository.saveAndFlush(e);
	}
	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException ex) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<Funcao> filtrar(FuncaoFilter f, Pageable p) {
		return repository.filtrar(f, p);
	}

	public List<Funcao> findAll() {
		return repository.findAll();
	}

	public Funcao getOne(Long c) {
		return repository.getOne(c);
	}
}
