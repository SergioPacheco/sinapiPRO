package br.edu.ifrn.sinapiPRO.repository.helper.empresa;
import javax.persistence.EntityManager; import javax.persistence.PersistenceContext;
import org.hibernate.Criteria; import org.hibernate.Session; import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional; import org.springframework.util.StringUtils;
import br.edu.ifrn.sinapiPRO.model.Empresa; import br.edu.ifrn.sinapiPRO.repository.filter.EmpresaFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;
public class EmpresasRepositoryImpl implements EmpresasRepositoryQueries {
	@PersistenceContext private EntityManager manager; @Autowired private PaginacaoUtil paginacaoUtil;
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<Empresa> filtrar(EmpresaFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(Empresa.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(Empresa.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
