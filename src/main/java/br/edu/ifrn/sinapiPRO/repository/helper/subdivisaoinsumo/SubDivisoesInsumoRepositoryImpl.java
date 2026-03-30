package br.edu.ifrn.sinapiPRO.repository.helper.subdivisaoinsumo;
import javax.persistence.EntityManager; import javax.persistence.PersistenceContext;
import org.hibernate.Criteria; import org.hibernate.Session; import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional; import org.springframework.util.StringUtils;
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo; import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;
public class SubDivisoesInsumoRepositoryImpl implements SubDivisoesInsumoRepositoryQueries {
	@PersistenceContext private EntityManager manager; @Autowired private PaginacaoUtil paginacaoUtil;
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<SubDivisaoInsumo> filtrar(SubDivisaoInsumoFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(SubDivisaoInsumo.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(SubDivisaoInsumo.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
