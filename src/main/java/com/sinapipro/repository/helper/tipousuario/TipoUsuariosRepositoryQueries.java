package com.sinapipro.repository.helper.tipousuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.TipoUsuario;
import com.sinapipro.repository.filter.TipoUsuarioFilter;

public interface TipoUsuariosRepositoryQueries {

	Page<TipoUsuario> filtrar(TipoUsuarioFilter filtro, Pageable pageable);
}
