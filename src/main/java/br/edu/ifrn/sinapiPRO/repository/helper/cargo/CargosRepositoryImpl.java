package br.edu.ifrn.sinapiPRO.repository.helper.cargo;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import br.edu.ifrn.sinapiPRO.model.Cargo;
import br.edu.ifrn.sinapiPRO.repository.filter.CargoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;
public class CargosRepositoryImpl implements CargosRepositoryQueries {
	@PersistenceContext
	private EntityManager manager;
	private final PaginacaoUtil paginacaoUtil;

	public CargosRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<Cargo> filtrar(CargoFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(Cargo.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(Cargo.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
