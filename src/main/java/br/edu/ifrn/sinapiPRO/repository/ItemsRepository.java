package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.helper.item.ItemsRepositoryQueries;

 
@Repository
public interface ItemsRepository extends JpaRepository<Item, Long>, ItemsRepositoryQueries {

	public Optional<Item> findByEtapa(Etapa etapa);
	public Optional<Item> findByInsumo(Insumo insumo);
	public Optional<Item> findByComposicao(Composicao etapa);
}