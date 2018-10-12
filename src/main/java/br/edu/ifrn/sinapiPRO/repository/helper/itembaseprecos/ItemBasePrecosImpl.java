package br.edu.ifrn.sinapiPRO.repository.helper.itembaseprecos;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Cidade;
import br.edu.ifrn.sinapiPRO.model.ItemBasePreco;
import br.edu.ifrn.sinapiPRO.repository.filter.ItemBasePrecoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class ItemBasePrecosImpl implements ItemBasePrecosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<ItemBasePreco> filtrar(ItemBasePrecoFilter filtro, Pageable pageable) {
		
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ItemBasePreco.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	//Buscar a cidade com seu estado por JoinType
	@Override
	@Transactional(readOnly = true)	
	public ItemBasePreco buscarComBasePreco(Long codigoItem) {
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ItemBasePreco.class);
		criteria.createAlias("basePreco", "b", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigoItem));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);			
		return (ItemBasePreco) criteria.uniqueResult();
	}
	
	private Long total(ItemBasePrecoFilter filtro) {
		
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ItemBasePreco.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(ItemBasePrecoFilter filtro, Criteria criteria) {
		if(filtro != null){
			if (filtro.getCodigoInsumo() != null) {
				criteria.add(Restrictions.eq("codigInsumo", filtro.getCodigoInsumo()));
			}
			if (filtro.getBasePreco() != null) {
				criteria.add(Restrictions.eq("BasePreco", filtro.getBasePreco() ));
			}
		}
		
	}
}