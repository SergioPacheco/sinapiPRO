package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.TipoUsuario;
import com.sinapipro.repository.TipoUsuariosRepository;
import com.sinapipro.repository.filter.TipoUsuarioFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroTipoUsuarioService extends AbstractNamedEntityCrudService<TipoUsuario, TipoUsuarioFilter, TipoUsuariosRepository> {

	public CadastroTipoUsuarioService(TipoUsuariosRepository repository) {
		super(
				repository,
				TipoUsuario::getCodigo,
				TipoUsuario::getNome,
				"Tipo de usuário já cadastrado",
				"Impossível apagar tipo de usuário. Já está em uso.",
				"Tipo de usuário não encontrado.");
	}
}
