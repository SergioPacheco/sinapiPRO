package br.edu.ifrn.sinapiPRO.repository.helper.unidademedida;

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
import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.filter.UnidadeMedidaFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class UnidadesMedidaRepositoryImpl implements UnidadesMedidaRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	private final PaginacaoUtil paginacaoUtil;

	public UnidadesMedidaRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}

	@SuppressWarnings("unchecked")
	@Override @Transactional(readOnly = true)
	public Page<UnidadeMedida> filtrar(UnidadeMedidaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(UnidadeMedida.class);
		paginacaoUtil.preparar(criteria, pageable);
		if (filtro != null && !StringUtils.isEmpty(filtro.getNome()))
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		Long total = (Long) manager.unwrap(Session.class).createCriteria(UnidadeMedida.class)
				.add(filtro != null && !StringUtils.isEmpty(filtro.getNome())
						? Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE)
						: Restrictions.sqlRestriction("1=1"))
				.setProjection(Projections.rowCount()).uniqueResult();
		return new PageImpl<>(criteria.list(), pageable, total);
	}
}
