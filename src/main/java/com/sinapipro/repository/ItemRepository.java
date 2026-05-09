package com.sinapipro.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sinapipro.model.Item;
import com.sinapipro.repository.helper.item.ItemRepositoryQueries;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryQueries {
	
	@Query("SELECT SUM(i.valorUnitario * i.quantidade) FROM Item i "
	  	  + "WHERE i.orcamento.codigo=:codigo AND "
		  + "      i.especie = 'MAO_DE_OBRA'" ) 
  	public BigDecimal somaValorMaoObra(Long codigo); 
	
	@Query("SELECT SUM(i.valorUnitario * i.quantidade) FROM Item i WHERE i.orcamento.codigo=:codigo "
			+ "AND i.especie = 'MATERIAL'" ) 
	public BigDecimal somaValorMaterial(Long codigo); 
	
	@Query("SELECT SUM(i.valorUnitario * i.quantidade) FROM Item i WHERE i.orcamento.codigo=:codigo "
			+ "AND i.especie = 'EQUIPAMENTO'" ) 
	public BigDecimal somaValorEquipamento(Long codigo); 
	
	@Query("SELECT SUM(i.valorUnitario * i.quantidade) FROM Item i WHERE i.orcamento.codigo=:codigo" ) 
	public BigDecimal somaValorTotal(Long codigo); 
	
}
