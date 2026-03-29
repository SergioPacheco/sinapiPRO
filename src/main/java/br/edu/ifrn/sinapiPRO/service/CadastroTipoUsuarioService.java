package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.TipoUsuario;
import br.edu.ifrn.sinapiPRO.repository.TipoUsuariosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUsuarioFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class CadastroTipoUsuarioService {

	@Autowired
	private TipoUsuariosRepository repository;

	@Transactional
	public TipoUsuario salvar(TipoUsuario tipoUsuario) {
		Optional<TipoUsuario> existente = repository.findByNomeIgnoreCase(tipoUsuario.getNome());
		if (existente.isPresent() && !existente.get().getCodigo().equals(tipoUsuario.getCodigo())) {
			throw new JaCadastradoException("Tipo de usuário já cadastrado");
		}
		return repository.saveAndFlush(tipoUsuario);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar tipo de usuário. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<TipoUsuario> filtrar(TipoUsuarioFilter filtro, Pageable pageable) {
		return repository.filtrar(filtro, pageable);
	}

	public List<TipoUsuario> findAll() {
		return repository.findAll();
	}

	public TipoUsuario getOne(Long codigo) {
		return repository.getOne(codigo);
	}
}
