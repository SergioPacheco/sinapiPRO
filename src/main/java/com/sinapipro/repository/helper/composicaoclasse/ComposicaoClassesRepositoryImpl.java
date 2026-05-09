package com.sinapipro.repository.helper.composicaoclasse;

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

import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.repository.filter.ComposicaoClasseFilter;
import com.sinapipro.repository.helper.composicaoclasse.ComposicaoClassesRepositoryQueries;
import com.sinapipro.repository.paginacao.PaginacaoUtil;

public class ComposicaoClassesRepositoryImpl implements ComposicaoClassesRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	private final PaginacaoUtil paginacaoUtil;

	public ComposicaoClassesRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<ComposicaoClasse> filtrar(ComposicaoClasseFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ComposicaoClasse.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(ComposicaoClasseFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ComposicaoClasse.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(ComposicaoClasseFilter filtro, Criteria criteria) {
		if(filtro != null){

			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
		}
	}
}