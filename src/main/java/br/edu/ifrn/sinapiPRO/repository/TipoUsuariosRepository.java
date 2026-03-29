package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoUsuario;
import br.edu.ifrn.sinapiPRO.repository.helper.tipousuario.TipoUsuariosRepositoryQueries;

@Repository
public interface TipoUsuariosRepository extends JpaRepository<TipoUsuario, Long>, TipoUsuariosRepositoryQueries {

	Optional<TipoUsuario> findByNomeIgnoreCase(String nome);
}
