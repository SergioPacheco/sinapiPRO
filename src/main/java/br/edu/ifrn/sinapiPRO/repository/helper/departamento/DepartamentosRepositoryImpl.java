package br.edu.ifrn.sinapiPRO.repository.helper.departamento;
import javax.persistence.EntityManager; import javax.persistence.PersistenceContext;
import org.hibernate.Criteria; import org.hibernate.Session; import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional; import org.springframework.util.StringUtils;
import br.edu.ifrn.sinapiPRO.model.Departamento; import br.edu.ifrn.sinapiPRO.repository.filter.DepartamentoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;
public class DepartamentosRepositoryImpl implements DepartamentosRepositoryQueries {
	@PersistenceContext private EntityManager manager; @Autowired private PaginacaoUtil paginacaoUtil;
	@SuppressWarnings("unchecked") @Override @Transactional(readOnly = true)
	public Page<Departamento> filtrar(DepartamentoFilter f, Pageable p) {
		Criteria c = manager.unwrap(Session.class).createCriteria(Departamento.class); paginacaoUtil.preparar(c, p);
		if (f != null && !StringUtils.isEmpty(f.getNome())) c.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		Criteria ct = manager.unwrap(Session.class).createCriteria(Departamento.class);
		if (f != null && !StringUtils.isEmpty(f.getNome())) ct.add(Restrictions.ilike("nome", f.getNome(), MatchMode.ANYWHERE));
		ct.setProjection(Projections.rowCount());
		return new PageImpl<>(c.list(), p, (Long) ct.uniqueResult());
	}
}
