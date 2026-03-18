package br.edu.ifrn.sinapiPRO.repository.helper.fornecedor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.repository.filter.FornecedorFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class FornecedoresRepositoryImpl implements FornecedoresRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Fornecedor> filtrar(FornecedorFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Fornecedor.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(FornecedorFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Fornecedor.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(FornecedorFilter filtro, Criteria criteria) {
		if (filtro != null && !StringUtils.isEmpty(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
	}
}
