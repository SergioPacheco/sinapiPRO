package br.edu.ifrn.sinapiPRO.repository.helper.item;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.filter.AtualFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;

public class ItemRepositoryImpl implements ItemRepositoryQueries {
 
	@PersistenceContext
	private EntityManager manager;
	
	private final PaginacaoUtil paginacaoUtil;

	public ItemRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Item> filtrar(AtualFilter filtro, Pageable pageable) {
		 
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Item.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
 
	private Long total(AtualFilter filtro) {
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Item.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(AtualFilter filtro, Criteria criteria) {
		
		criteria.add(Restrictions.eq("orcamento", filtro.getOrcamento() ));
		
		if (filtro != null) {
			if (isEtapaPresente(filtro)) {
				criteria.add(Restrictions.eq("etapa", filtro.getEtapa()));
				
			}
			if (!StringUtils.isEmpty(filtro.getDescricaoItem())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricaoItem(), MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc("itemizacao"));
	}
		
	private boolean isEtapaPresente(AtualFilter filtro) {
		return filtro.getEtapa() != null && filtro.getEtapa().getCodigo() != null;
	}
	
	@Override   
	public List<Etapa> findEtapasOrcamento(Long codigo) {
		
			List<Etapa> listaEtapa = new ArrayList<>();
			List<Item> listaItem= manager
						.createQuery("from Item i where i.orcamento.codigo=:codigo and i.tipo = 'ETAPA'", Item.class)
							.setParameter("codigo", codigo)
							.getResultList();
		
			for (Item i:listaItem) {
				listaEtapa.add(i.getEtapa());
			}
			
		return listaEtapa;
	}
	
	
}
