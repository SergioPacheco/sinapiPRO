package br.edu.ifrn.sinapiPRO.repository.helper.classe;

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

import br.edu.ifrn.sinapiPRO.model.ClasseComposicao;
import br.edu.ifrn.sinapiPRO.repository.filter.ClasseComposicaoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.classe.ClassesRepositoryQueries;

import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class ClassesRepositoryImpl implements ClassesRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<ClasseComposicao> filtrar(ClasseComposicaoFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ClasseComposicao.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	private Long total(ClasseComposicaoFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(ClasseComposicao.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(ClasseComposicaoFilter filtro, Criteria criteria) {
		if(filtro != null){

			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
		}
	}
}