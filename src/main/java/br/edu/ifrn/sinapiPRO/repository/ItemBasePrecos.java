package br.edu.ifrn.sinapiPRO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.ItemBasePreco;
import br.edu.ifrn.sinapiPRO.repository.helper.itembaseprecos.ItemBasePrecosQueries;

@Repository
public interface ItemBasePrecos extends JpaRepository<ItemBasePreco, Long>, ItemBasePrecosQueries {

 
}
