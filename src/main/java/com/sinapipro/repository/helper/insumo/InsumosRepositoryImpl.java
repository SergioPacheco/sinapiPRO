package com.sinapipro.repository.helper.insumo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sinapipro.dto.BasePrecoItemDTO;
import com.sinapipro.dto.InsumoDTO;
import com.sinapipro.model.ComposicaoItem;
import com.sinapipro.model.Insumo;
import com.sinapipro.model.Item;
import com.sinapipro.model.Tipo;
import com.sinapipro.repository.filter.InsumoFilter;
import com.sinapipro.repository.filter.ListaInsumosFilter;
import com.sinapipro.repository.paginacao.PaginacaoUtil;

public class InsumosRepositoryImpl implements InsumosRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	private final PaginacaoUtil paginacaoUtil;

	public InsumosRepositoryImpl(PaginacaoUtil paginacaoUtil) {
		this.paginacaoUtil = paginacaoUtil;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Insumo> filtrar(InsumoFilter filtro, Pageable pageable) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Insumo.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	
	
	private Long total(InsumoFilter filtro) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Insumo.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	
	}

	private void adicionarFiltro(InsumoFilter filtro, Criteria criteria) {
		
		criteria.createAlias("baseInsumo", "b");
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getCodigoInsumo())) {
				criteria.add(Restrictions.eq("codigoInsumo", filtro.getCodigoInsumo()));
			}
			if (filtro.getEspecie() != null) {
				criteria.add(Restrictions.eq("especie", filtro.getEspecie()));
			}
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
			}
			if (isBaseInsumoPresente(filtro)) {
				criteria.add(Restrictions.eq("b.codigo", filtro.getBaseInsumo().getCodigo()));
			}
		}
	}

	private boolean isBaseInsumoPresente(InsumoFilter filtro) {
		return filtro.getBaseInsumo() != null && filtro.getBaseInsumo().getCodigo() != null;
	}

	/**
	 *   pesquisa todas as bases de preços cadastradas para o insumo informado
	 */
	@Override                     
	public List<BasePrecoItemDTO> listaPrecosPorInsumo(String codigoInsumo) {
		
		return manager
				.createQuery(
				  "select new com.sinapipro.dto.BasePrecoItemDTO(b.basePreco.baseInsumo.nome as nomeBase, b.anoMes, b.preco, b.precoOnerado) "
				+ "  from BasePrecoItem b "
				+ " where b.codigoInsumo    =:codigo order by b.anoMes", BasePrecoItemDTO.class)
				.setParameter("codigo", codigoInsumo)
				.getResultList();
	}
	 
	/**
	 *  pesquisa insumos por descricao ou codigo, usado jquery.autocomplete 
	 */
	@Override
	public List<InsumoDTO> porDescricao(String descricao) {
				
		return manager 
				.createQuery(
					  "select new com.sinapipro.dto.InsumoDTO(codigo, codigoInsumo, descricao, unidade,  precoPadrao) "
				    + "  from Insumo where lower(descricao)    like lower(:descricao) or "
				    + "                    lower(codigoInsumo) like lower(:descricao) ", InsumoDTO.class)
					.setParameter("descricao", descricao + "%")
					.getResultList();
	}
	 
	/**
	 *  filtra os insumos utilizado no orçamento
	 */
	@Override
	public Page<Item> filtrarInsumos(ListaInsumosFilter filter, Pageable pageable) {
		/*
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Item.class);
		criteria.createAlias("composicao", "c", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("c.itens", "ic", JoinType.LEFT_OUTER_JOIN );
		paginacaoUtil.preparar(criteria, pageable);
		criteria.add( Restrictions.or(Restrictions.eq("tipo", Tipo.INSUMO), 
		        	                  Restrictions.eq("ic.tipo", Tipo.INSUMO) )); 
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		*/
		List<Item> items = new ArrayList<>();
		List<Item> items2 = new ArrayList<>();
		List<ComposicaoItem> TabelaComposicaoItens = new ArrayList<>();
		List<ComposicaoItem> TabelaComposicaoItens2 = new ArrayList<>();
		
		items = manager
			        .createQuery(
					  "  from Item i "
					+ " where i.orcamento  =:orcamento and "
					+       "(i.tipoAsText = 'INSUMO' or "
					+       " i.tipoAsText = 'COMPOSICAO')", Item.class)
					.setParameter("orcamento", filter.getOrcamento())
					.getResultList();
		
		if (items!=null) { // Itens do Orçamento 
			for (Item i:items) {
				if (i == null) {
					continue;
				}
				System.out.println(i.getDescricao());
				if (i.getTipo() == Tipo.INSUMO) {
					items2.add(i);
				} else {
					// mutiplicar item.quantidade x coeficinete antes de adicionar
					if (i.getComposicao() !=null && i.getComposicao().getItens() !=null ) {
						List<ComposicaoItem> citens = i.getComposicao().getItens(); 
						for (ComposicaoItem it:citens) {
							it.setQuantidadeComposicao(it.getCoeficiente().multiply(i.getQuantidade()));
							TabelaComposicaoItens.add(it);
						}
					}
				}
			}
			TabelaComposicaoItens.removeAll(Arrays.asList("", null));
			// TabelaComposicaoItens.removeAll(Collections.singleton(null));
			do {
				if (!TabelaComposicaoItens.isEmpty()) {
					// Itens
					for (ComposicaoItem ci :TabelaComposicaoItens) {
						
						if (ci == null) {
							continue;
						}
						if (ci.getTipo() == Tipo.INSUMO) { 
							Item novoItem = new Item(); 
							novoItem.setUnidade(ci.getUnidade());
							novoItem.setDescricao(ci.getDescricao());
							novoItem.setInsumo(ci.getInsumo());
							novoItem.setQuantidade(ci.getCoeficiente());  // multiplicar coeficinete quantidade_composicao
							novoItem.setValorUnitario(ci.getPrecoUnitario());
							novoItem.setTipo(Tipo.INSUMO);
							items2.add(novoItem);
							continue;
						}
						if (ci.getTipo() == Tipo.COMPOSICAO) {
							if (ci.getComposicao() !=null && ci.getComposicao().getItens() !=null ) {
								TabelaComposicaoItens2.addAll(ci.getComposicao().getItens());
							}	
						}
					}
					TabelaComposicaoItens.clear();
				}
				
				TabelaComposicaoItens2.removeAll(Arrays.asList("", null));
				if (!TabelaComposicaoItens2.isEmpty()) {
					for (ComposicaoItem ci :TabelaComposicaoItens2) {
						
						if (ci == null) {
							continue;
						}
						Item novoItem = new Item(); 
						
						if (ci.getTipo() == Tipo.INSUMO) { 
							novoItem.setUnidade(ci.getUnidade());
							novoItem.setDescricao(ci.getDescricao());
							novoItem.setInsumo(ci.getInsumo());
							novoItem.setQuantidade(ci.getCoeficiente());  // multiplicar coeficinete quantidade_composicao
							novoItem.setValorUnitario(ci.getPrecoUnitario());
							novoItem.setTipo(Tipo.INSUMO);
							items2.add(novoItem);
							continue;
						}
						if (ci.getTipo() == Tipo.COMPOSICAO) {
							if (ci.getComposicao() !=null && ci.getComposicao().getItens() !=null ) {
								TabelaComposicaoItens.addAll(ci.getComposicao().getItens());
							}	
						}
					}
					TabelaComposicaoItens2.clear();
				}
				
			} while (TabelaComposicaoItens2.isEmpty() && !TabelaComposicaoItens.isEmpty());
			
			Collections.sort(items2, new Comparator<Item>() {
		        @Override
		        public int compare(Item i2, Item i1)
		        {
		        	return  i2.getDescricao().compareTo(i1.getDescricao());
		        }
		    });
			// agregate
			
		}
		return new PageImpl<>(items2, pageable, items2.size());
	}
	 
	
	private void adicionarFiltroInsumos(ListaInsumosFilter filtro, Criteria criteria) {
		
		// TODO: filtrar por usuario e orcamento atual tambem
		
		if (filtro != null) {
			if (filtro.getEspecie() != null) {
				criteria.add(Restrictions.eq("especie", filtro.getEspecie()));
			}
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
			}
		}
	}
	
	
}

/* Criteria Builder 
 * 
private static List<Contact> fetchAllContacts(){
    //Open Session
    Session session = sessionFactory.openSession();

    // Deprecated Way
    // Criteria criteria = session.createCriteria(Contact.class);
    // List<Contacts> contacts = criteria.list();  

    //Get Criteria Builder
    CriteriaBuilder builder = session.getCriteriaBuilder();

    //Create Criteria
    CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);
    Root<Contact> contactRoot = criteria.from(Contact.class);
    criteria.select(contactRoot);

    //Use criteria to query with session to fetch all contacts
    List<Contact> contacts = session.createQuery(criteria).getResultList();

    //Close session
    session.close();

    return contacts;
}
*/

