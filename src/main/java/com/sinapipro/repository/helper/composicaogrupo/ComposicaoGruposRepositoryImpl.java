package com.sinapipro.repository.helper.composicaogrupo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sinapipro.model.ComposicaoGrupo;
import com.sinapipro.repository.filter.ComposicaoGrupoFilter;
import com.sinapipro.repository.paginacao.PaginacaoUtil;

public class ComposicaoGruposRepositoryImpl implements ComposicaoGruposRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	private final PaginacaoUtil paginacaoUtil;

	public ComposicaoGruposRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<ComposicaoGrupo> filtrar(ComposicaoGrupoFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ComposicaoGrupo.class);
		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		criteria.createAlias("composicaoClasse", "c");
				
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	private Long total(ComposicaoGrupoFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ComposicaoGrupo.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(ComposicaoGrupoFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (filtro.getComposicaoClasse() != null) {
				criteria.add(Restrictions.eq("composicaoClasse", filtro.getComposicaoClasse()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
		}
	}
}