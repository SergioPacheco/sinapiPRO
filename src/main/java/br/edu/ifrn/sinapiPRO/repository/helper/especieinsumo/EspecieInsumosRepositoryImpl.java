package br.edu.ifrn.sinapiPRO.repository.helper.especieinsumo;

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

import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class EspecieInsumosRepositoryImpl implements EspecieInsumosRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;

	private final PaginacaoUtil paginacaoUtil;

	public EspecieInsumosRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<EspecieInsumo> filtrar(EspecieInsumoFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(EspecieInsumo.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(EspecieInsumoFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(EspecieInsumo.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(EspecieInsumoFilter filtro, Criteria criteria) {
		if (filtro != null && !StringUtils.isEmpty(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
	}
}
