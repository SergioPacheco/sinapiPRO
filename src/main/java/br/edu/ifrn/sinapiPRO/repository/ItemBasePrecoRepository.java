package br.edu.ifrn.sinapiPRO.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BaseKey;
import br.edu.ifrn.sinapiPRO.model.ItemBasePreco;

@Repository
public interface ItemBasePrecoRepository extends JpaRepository<ItemBasePreco, BaseKey> {
	
	
	//@Query(value = "SELECT * FROM item_base_preco i ORDER BY codigo_base_insumo \n-- #pageable \n", 
	//	   countQuery = "SELECT count(*) FROM item_base_preco",
	//	   nativeQuery=true)
	// Page<ItemBasePreco> findByBasePreco(BasePreco basePreco, Pageable pageable); 
	
	//@Query("SELECT i FROM item_base_preco i WHERE i.codigo_base_preco = ?1 and i.codigo_insumo = ?2" )
	// Optional<ItemBasePreco> findByBasePrecoAndCodigoInsumo(BasePreco basePreco, Long codigoInsumo);

	 Page<ItemBasePreco> findByBaseKeyBasePrecoID(Long basePrecoID, Pageable pageable);
	
}
