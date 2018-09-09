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

public class ComposicoesImpl implements ComposicoesQueries {

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
	
	@Override
	public List<ComposicaoDTO> porSkuOuDescricao(String skuOuDescricao) {
		String jpql = "select new br.edu.ifrn.sinapiPRO.dto.ComposicaoDTO(codigo, sku, descricao, valor) "
				+ "from Composicao where lower(sku) like lower(:skuOuDescricao) or lower(descricao) like lower(:skuOuDescricao)";
		List<ComposicaoDTO> composicoesFiltradas = manager.createQuery(jpql, ComposicaoDTO.class)
					.setParameter("skuOuDescricao", skuOuDescricao + "%")
					.getResultList();
		return composicoesFiltradas;
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
			if (!StringUtils.isEmpty(filtro.getSku())) {
				criteria.add(Restrictions.eq("sku", filtro.getSku()));
			}
			
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
			}

			if (isEstadoPresente(filtro)) {
				criteria.add(Restrictions.eq("estado", filtro.getEstado()));
			}

			if (isClassePresente(filtro)) {
				criteria.add(Restrictions.eq("classe", filtro.getClasse()));
			}
			
			if (filtro.getBase() != null) {
				criteria.add(Restrictions.eq("base", filtro.getBase()));
			}

		}
	}
	
	private boolean isEstadoPresente(ComposicaoFilter filtro) {
		return filtro.getEstado() != null && filtro.getEstado().getCodigo() != null;
	}
	
	private boolean isClassePresente(ComposicaoFilter filtro) {
		return filtro.getClasse() != null && filtro.getClasse().getCodigo() != null;
	}
}
