package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.filter.UsuarioFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.usuario.UsuariosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;

public interface UsuariosRepository extends JpaRepository<Usuario, Long>, UsuariosRepositoryQueries,
		FilterableRepository<Usuario, UsuarioFilter> {

	public Optional<Usuario> findByEmail(String email);
	public Optional<Usuario> findByNome(String nome);
	public List<Usuario> findByCodigoIn(Long[] codigos);
}
