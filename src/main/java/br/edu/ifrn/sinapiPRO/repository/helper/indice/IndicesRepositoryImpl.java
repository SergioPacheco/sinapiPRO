package br.edu.ifrn.sinapiPRO.repository.helper.indice;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.repository.filter.IndiceFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;
public class IndicesRepositoryImpl implements IndicesRepositoryQueries {
	@PersistenceContext
	private EntityManager manager;
	private final PaginacaoUtil paginacaoUtil;

	public IndicesRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<Indice> filtrar(IndiceFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(Indice.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(Indice.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
