package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.repository.helper.composicao.ComposicaoRepositoryQueries;

public interface ComposicaoRepository extends JpaRepository<Composicao, Long>, ComposicaoRepositoryQueries {

	public Optional<Composicao> findByBaseInsumoAndCodigoComposicao(BaseInsumo baseInsumo, Long codigoComposicao);
	public List<Composicao> findByBaseInsumo(BaseInsumo baseInsumo);
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
