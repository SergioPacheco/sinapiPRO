package com.sinapipro.repository.helper.divisaoinsumo;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.sinapipro.model.DivisaoInsumo;
import com.sinapipro.repository.filter.DivisaoInsumoFilter;
import com.sinapipro.repository.paginacao.PaginacaoUtil;
public class DivisoesInsumoRepositoryImpl implements DivisoesInsumoRepositoryQueries {
	@PersistenceContext
	private EntityManager manager;
	private final PaginacaoUtil paginacaoUtil;

	public DivisoesInsumoRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<DivisaoInsumo> filtrar(DivisaoInsumoFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(DivisaoInsumo.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(DivisaoInsumo.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
