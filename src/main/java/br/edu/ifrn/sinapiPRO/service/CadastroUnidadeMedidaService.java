package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.UnidadesMedidaRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.UnidadeMedidaFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class CadastroUnidadeMedidaService {

	@Autowired
	private UnidadesMedidaRepository repository;

	@Transactional
	public UnidadeMedida salvar(UnidadeMedida u) {
		Optional<UnidadeMedida> existente = repository.findByNomeIgnoreCase(u.getNome());
		if (existente.isPresent() && !existente.get().getCodigo().equals(u.getCodigo()))
			throw new JaCadastradoException("Unidade de medida já cadastrada");
		return repository.saveAndFlush(u);
	}

	@Transactional
	public void excluir(Long codigo) {
		try { repository.deleteById(codigo); repository.flush();
	}
		catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
	}
	}

	@Transactional(readOnly = true)
	public Page<UnidadeMedida> filtrar(UnidadeMedidaFilter filtro, Pageable pageable) {
		return repository.filtrar(filtro, pageable);
	}

	public List<UnidadeMedida> findAll() {
		return repository.findAll();
	}

	public UnidadeMedida getOne(Long codigo) {
		return repository.getOne(codigo);
	}
}
