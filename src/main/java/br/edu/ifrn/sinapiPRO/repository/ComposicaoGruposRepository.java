package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.model.ComposicaoGrupo;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoGrupoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.composicaogrupo.ComposicaoGruposRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;

@Repository
public interface ComposicaoGruposRepository extends JpaRepository<ComposicaoGrupo, Long>, ComposicaoGruposRepositoryQueries,
		FilterableRepository<ComposicaoGrupo, ComposicaoGrupoFilter> {

	public Optional<ComposicaoGrupo> findByNomeIgnoreCase(String nome);
	
	public Optional<ComposicaoGrupo> findByNomeAndComposicaoClasse(String nome, ComposicaoClasse composicaoClasse);
	 
	public List<ComposicaoGrupo> findAllByComposicaoClasseCodigo(Long codigoComposicaoClasse);
	
	 
}
