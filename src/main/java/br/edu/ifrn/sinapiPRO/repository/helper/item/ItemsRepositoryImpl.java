package br.edu.ifrn.sinapiPRO.repository.helper.item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.filter.ItemFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class ItemsRepositoryImpl implements ItemsRepositoryQueries {
 
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Item> filtrar(ItemFilter filtro, Pageable pageable) {
		 
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Item.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
 
	private Long total(ItemFilter filtro) {
		@SuppressWarnings("deprecation")
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Item.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(ItemFilter filtro, Criteria criteria) {
		
		if (filtro != null) {
			 
			if (filtro.getTipo() != null) {
				criteria.add(Restrictions.eq("tipo", filtro.getTipo()));
			}
			 
		}
	}
	
}
