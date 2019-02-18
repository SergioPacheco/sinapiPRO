package br.edu.ifrn.sinapiPRO.repository.helper.composicao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.dto.ComposicaoDTO;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class ComposicaoRepositoryImpl implements ComposicaoRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Composicao> filtrar(ComposicaoFilter filtro, Pageable pageable) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Composicao.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	@Transactional(readOnly = true)
	@Override
	public Composicao buscarComItens(Long codigo) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Composicao.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Composicao) criteria.uniqueResult();
	}
	
	private Long total(ComposicaoFilter filtro) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Composicao.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}

	@Override
	public List<ComposicaoDTO> porDescricao(Long codigoBaseInsumo, String descricao) {
		
		return manager 
				.createQuery(
					  "select new br.edu.ifrn.sinapiPRO.dto.ComposicaoDTO(i.codigoComposicao, b.nome as nomeBaseInsumo, i.descricao, i.unidade,  i.custoTotal) "
				    + "  from Composicao as i"
					+ " right join BaseInsumo b" 		  
				    + " where b.codigo:= codigoBaseInsumo and"
				    + "       lower(i.descricao) like lower(:descricao)", ComposicaoDTO.class)
					.setParameter("descricao", descricao + "%")
					.setParameter("codigoBaseInsumo", codigoBaseInsumo)
					.getResultList();
	}
	
	private void adicionarFiltro(ComposicaoFilter filtro, Criteria criteria) {
		criteria.createAlias("baseInsumo", "b");
		criteria.createAlias("composicaoClasse", "c");
		criteria.createAlias("composicaoGrupo", "g");
		
		if (filtro != null) {
			if(filtro.getCodigoComposicao()!=null){
				criteria.add(Restrictions.eq("codigoComposicao", filtro.getCodigoComposicao()));
			}
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
			}
			if (isComposicaoClassePresente(filtro)) {
				criteria.add(Restrictions.eq("c.codigo", filtro.getComposicaoClasse().getCodigo()));
			}
			if (isComposicaoGrupoPresente(filtro)) {
				criteria.add(Restrictions.eq("g.codigo", filtro.getComposicaoGrupo().getCodigo()));
			}
			if (isBaseInsumoPresente(filtro)) {
				criteria.add(Restrictions.eq("b.codigo", filtro.getBaseInsumo().getCodigo()));
			}
		}
	}
	
	private boolean isBaseInsumoPresente(ComposicaoFilter filtro) {
		return filtro.getBaseInsumo() != null && filtro.getBaseInsumo().getCodigo() != null;
	}
	
	private boolean isComposicaoGrupoPresente(ComposicaoFilter filtro) {
		return filtro.getComposicaoGrupo() != null && filtro.getComposicaoGrupo().getCodigo() != null;
	}
	
	private boolean isComposicaoClassePresente(ComposicaoFilter filtro) {
		return filtro.getComposicaoClasse() != null && filtro.getComposicaoClasse().getCodigo() != null;
	}
}
