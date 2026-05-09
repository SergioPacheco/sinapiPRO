package com.sinapipro.repository.helper.funcao;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.sinapipro.model.Funcao;
import com.sinapipro.repository.filter.FuncaoFilter;
import com.sinapipro.repository.paginacao.PaginacaoUtil;
public class FuncoesRepositoryImpl implements FuncoesRepositoryQueries {
	@PersistenceContext
	private EntityManager manager;
	private final PaginacaoUtil paginacaoUtil;

	public FuncoesRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<Funcao> filtrar(FuncaoFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(Funcao.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(Funcao.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
