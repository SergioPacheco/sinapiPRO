package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoUsuario;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUsuarioFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.tipousuario.TipoUsuariosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface TipoUsuariosRepository extends JpaRepository<TipoUsuario, Long>, TipoUsuariosRepositoryQueries,
		NamedEntityRepository<TipoUsuario>, FilterableRepository<TipoUsuario, TipoUsuarioFilter> {

	Optional<TipoUsuario> findByNomeIgnoreCase(String nome);
}
