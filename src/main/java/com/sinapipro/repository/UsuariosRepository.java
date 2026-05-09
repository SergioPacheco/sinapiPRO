package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.sinapipro.model.Usuario;
import com.sinapipro.repository.filter.UsuarioFilter;
import com.sinapipro.repository.helper.usuario.UsuariosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

public interface UsuariosRepository extends JpaRepository<Usuario, Long>, UsuariosRepositoryQueries,
		FilterableRepository<Usuario, UsuarioFilter> {

	public Optional<Usuario> findByEmail(String email);
	public Optional<Usuario> findByNome(String nome);
	public List<Usuario> findByCodigoIn(Long[] codigos);
}
