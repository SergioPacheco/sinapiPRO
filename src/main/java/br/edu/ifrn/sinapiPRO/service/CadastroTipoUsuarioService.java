package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.TipoUsuario;
import br.edu.ifrn.sinapiPRO.repository.TipoUsuariosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUsuarioFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

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
