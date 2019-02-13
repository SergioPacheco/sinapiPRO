package br.edu.ifrn.sinapiPRO.repository.helper.orcamentoitem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.OrcamentoItem;
import br.edu.ifrn.sinapiPRO.repository.filter.OrcamentoItemFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class OrcamentoItemsRepositoryImpl implements OrcamentoItemsRepositoryQueries {
 
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<OrcamentoItem> filtrar(OrcamentoItemFilter filtro, Pageable pageable) {
		 
		Criteria criteria = manager.unwrap(Session.class).createCriteria(OrcamentoItem.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
 
	private Long total(OrcamentoItemFilter filtro) {
		@SuppressWarnings("deprecation")
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(OrcamentoItem.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(OrcamentoItemFilter filtro, Criteria criteria) {
		
		if (filtro != null) {
			 
			if (filtro.getTipo() != null) {
				criteria.add(Restrictions.eq("tipo", filtro.getTipo()));
			}
			 
		}
	}
	
}
