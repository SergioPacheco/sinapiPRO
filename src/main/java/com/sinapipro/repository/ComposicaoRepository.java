package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.model.Composicao;
import com.sinapipro.repository.helper.composicao.ComposicaoRepositoryQueries;

public interface ComposicaoRepository extends JpaRepository<Composicao, Long>, ComposicaoRepositoryQueries {

	public Optional<Composicao> findByBaseInsumoAndCodigoComposicao(BaseInsumo baseInsumo, String codigoComposicao);
	public List<Composicao> findByBaseInsumo(BaseInsumo baseInsumo);
	
	Long countByBaseInsumoCodigo(Long codigo);
	
}

/*
 * @ManyToOne - como recupear filhos 
 *  
List<PostComment> comments = entityManager.createQuery(
	    "select pc " +
	    "from PostComment pc " +
	    "where pc.post.id = :postId", PostComment.class)
	.setParameter( "postId", 1L )
	.getResultList();
	
*/	
