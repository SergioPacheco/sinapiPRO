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
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("nome", filtro.getDescricao(), MatchMode.ANYWHERE));
			}

			if (isClassePresente(filtro)) {
				criteria.add(Restrictions.eq("classe", filtro.getClasse()));
			}
			if (isTipoComposicaoPresente(filtro)) {
				criteria.add(Restrictions.eq("tipoComposicao", filtro.getTipoComposicao() ));
			}
			
			if (isBasePrecoPresente(filtro)) {
				criteria.add(Restrictions.eq("basePreco", filtro.getBasePreco()));
			}

		}
	}
	
	private boolean isTipoComposicaoPresente(ComposicaoFilter filtro) {
		return filtro.getTipoComposicao() != null && filtro.getTipoComposicao().getCodigo() != null;
	}
	
	private boolean isClassePresente(ComposicaoFilter filtro) {
		return filtro.getClasse() != null && filtro.getClasse().getCodigo() != null;
	}

	private boolean isBasePrecoPresente(ComposicaoFilter filtro) {
		return filtro.getBasePreco() != null && filtro.getBasePreco().getCodigo() != null;
	}

	@Override
	public List<ComposicaoDTO> porCodigoOuDescricao(String codigoOuDescricao) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
