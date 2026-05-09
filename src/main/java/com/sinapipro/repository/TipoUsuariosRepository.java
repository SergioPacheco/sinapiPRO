package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.TipoUsuario;
import com.sinapipro.repository.filter.TipoUsuarioFilter;
import com.sinapipro.repository.helper.tipousuario.TipoUsuariosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface TipoUsuariosRepository extends JpaRepository<TipoUsuario, Long>, TipoUsuariosRepositoryQueries,
		NamedEntityRepository<TipoUsuario>, FilterableRepository<TipoUsuario, TipoUsuarioFilter> {

	Optional<TipoUsuario> findByNomeIgnoreCase(String nome);
}
