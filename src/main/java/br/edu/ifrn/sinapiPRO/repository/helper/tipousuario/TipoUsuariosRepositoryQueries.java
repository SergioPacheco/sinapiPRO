package br.edu.ifrn.sinapiPRO.repository.helper.tipousuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.TipoUsuario;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUsuarioFilter;

public interface TipoUsuariosRepositoryQueries {

	Page<TipoUsuario> filtrar(TipoUsuarioFilter filtro, Pageable pageable);
}
