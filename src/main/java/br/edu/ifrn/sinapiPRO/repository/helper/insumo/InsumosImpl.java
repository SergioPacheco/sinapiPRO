package br.edu.ifrn.sinapiPRO.repository.helper.insumo;


import java.util.Collections;

/*
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.dto.InsumoDTO;
import br.edu.ifrn.sinapiPRO.dto.ItemBasePrecoDTO;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.filter.InsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.paginacao.PaginacaoUtil;
import br.edu.ifrn.sinapiPRO.utils.Lib;

public class InsumosImpl implements InsumosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Insumo> filtrar(InsumoFilter filtro, Pageable pageable) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Insumo.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	/*orm.xml - native query */
	@Override                     
	public List<ItemBasePrecoDTO> listaPrecosPorInsumo(Long codigoInsumo) {
	
		List<ItemBasePrecoDTO> lista = manager
						.createNamedQuery("Insumos.listaPrecos", ItemBasePrecoDTO.class)
						.getResultList();
		 
		return lista;
	}
	
	@Override                     
	public List<ItemBasePrecoDTO> listaBasePrecoPorInsumo(Long codigoInsumo) {
		
		return manager.createQuery("select new br.edu.ifrn.sinapiPRO.dto.ItemBasePrecoDTO(anoMes, preco) "
				+ "from ItemBasePreco as i where i.baseKey.insumoID =:codigo", ItemBasePrecoDTO.class)
				.setParameter("codigo", codigoInsumo).getResultList();
	}
		
	@Override
	public List<InsumoDTO> porCodigoOuDescricao(String codigoOuDescricao) {
			
		Long codigo = 0L;
		if (Lib.IsNumeric(codigoOuDescricao)) {
			codigo = Long.valueOf(codigoOuDescricao);
		}
		
		String jpql = "select new br.edu.ifrn.sinapiPRO.dto.InsumoDTO(codigo, codigoInsumo, descricao, unidade,  precoPadrao) "
				    + "from Insumo where codigoInsumo=:codigo or lower(descricao) like lower(:codigoOuDescricao)";
		
		List<InsumoDTO> insumosFiltrados = manager.createQuery(jpql, InsumoDTO.class)
					.setParameter("codigoInsumoOuDescricao", codigoOuDescricao + "%")
					.setParameter("codigo", codigo)
					.getResultList();
		
		return  insumosFiltrados;
	}
	
	/*
	@Override
	public ValorItensEstoque valorItensEstoque() {
		String query = "select new br.edu.ifrn.sinapiPRO.dto.ValorItensEstoque(sum(valor * quantidadeEstoque), sum(quantidadeEstoque)) from Insumo";
		return manager.createQuery(query, ValorItensEstoque.class).getSingleResult();
	}
	
	String jpql = "select new com.algaworks.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
				+ "from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";
	*/
	
	private Long total(InsumoFilter filtro) {
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Insumo.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	
	}

	private void adicionarFiltro(InsumoFilter filtro, Criteria criteria) {
		
		if (filtro != null) {
			if(filtro.getCodigoInsumo()!=null){
				criteria.add(Restrictions.eq("codigoInsumo", filtro.getCodigoInsumo()));
			}
			if (!StringUtils.isEmpty(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
			}
		}
	}
}
