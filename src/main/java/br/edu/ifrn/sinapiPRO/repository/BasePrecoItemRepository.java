package br.edu.ifrn.sinapiPRO.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.BasePrecoItem;

@Repository
public interface BasePrecoItemRepository extends JpaRepository<BasePrecoItem, Long> {

	public Optional<BasePrecoItem> findByBasePrecoAndCodigoInsumo(BasePreco basePreco, String codigoInsumo);
	
}



/*
@Query("  SELECT YEAR(bp.data_referencia), MONTH(bp.data_referencia), it.preco "
		+ "FROM item_base_preco it "
		+ "INNER JOIN base_preco  bp ON bp.codigo = it.base_precoid "
		+ "INNER JOIN base_insumo bi ON bi.codigo = bp.codigo_base_insumo "
		+ "WHERE it.insumoid = :codigoInsumo")
*/

//@Query(value = "SELECT * FROM item_base_preco i ORDER BY codigo_base_insumo \n-- #pageable \n", 
//	   countQuery = "SELECT count(*) FROM item_base_preco",
//	   nativeQuery=true)
// Page<ItemBasePreco> findByBasePreco(BasePreco basePreco, Pageable pageable); 
	
//@Query("SELECT i FROM item_base_preco i WHERE i.codigo_base_preco = ?1 and i.codigo_insumo = ?2" )
// Optional<ItemBasePreco> findByBasePrecoAndCodigoInsumo(BasePreco basePreco, Long codigoInsumo);