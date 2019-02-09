package br.edu.ifrn.sinapiPRO.repository.helper.baseprecositem;

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

import br.edu.ifrn.sinapiPRO.model.BasePrecoItem;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoItemFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class BasePrecosItemRepositoryImpl implements BasePrecosItemRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<BasePrecoItem> filtrar(BasePrecoItemFilter filtro, Pageable pageable) {
		
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(BasePrecoItem.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	/**
	 * busca todos os items de uma base de preco  
	 */
	@Override
	@Transactional(readOnly = true)	
	public BasePrecoItem buscarComBasePreco(Long codigoItem) {
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(BasePrecoItem.class);
		criteria.createAlias("basePreco", "b", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigoItem));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);			
		return (BasePrecoItem) criteria.uniqueResult();
	}
	
	private Long total(BasePrecoItemFilter filtro) {
		
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(BasePrecoItem.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(BasePrecoItemFilter filtro, Criteria criteria) {
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