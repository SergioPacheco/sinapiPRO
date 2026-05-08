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
	
	private final PaginacaoUtil paginacaoUtil;

	public ComposicaoRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Composicao> filtrar(ComposicaoFilter filtro, Pageable pageable) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Composicao.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
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

	private void adicionarFiltro(ComposicaoFilter filtro, Criteria criteria) {
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getCodigoComposicao())) {
				criteria.add(Restrictions.eq("codigoComposicao", filtro.getCodigoComposicao()));
			}
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
			}
			if (isComposicaoGrupoPresente(filtro)) {
				criteria.add(Restrictions.eq("composicaoGrupo", filtro.getComposicaoGrupo()));
			}
			if (isBaseInsumoPresente(filtro)) {
				criteria.add(Restrictions.eq("baseInsumo", filtro.getBaseInsumo()));
			}
		}
	}
	
	private boolean isBaseInsumoPresente(ComposicaoFilter filtro) {
		return filtro.getBaseInsumo() != null && filtro.getBaseInsumo().getCodigo() != null;
	}
	
	private boolean isComposicaoGrupoPresente(ComposicaoFilter filtro) {
		return filtro.getComposicaoGrupo() != null && filtro.getComposicaoGrupo().getCodigo() != null;
	}
	
	@Override
	public List<ComposicaoDTO> porDescricao(String descricao) {
		// TODO: Incluir filtro por base de insumo 
		return manager 
				.createQuery(
					  "select new br.edu.ifrn.sinapiPRO.dto"
					  + ".ComposicaoDTO(codigo, codigoComposicao, descricao, unidade,  custoTotal) "
				    + "  from Composicao"
				    + " where lower(descricao)        like lower(:descricao) or"
				    + "       lower(codigoComposicao) like lower(:descricao)", ComposicaoDTO.class)
					.setParameter("descricao", descricao + "%")
					.getResultList();
	}
}
