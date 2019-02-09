package br.edu.ifrn.sinapiPRO.repository.helper.insumo;

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
	
	@Override                     
	public List<ItemBasePrecoDTO> listaBasePrecoPorInsumo(Long codigoInsumo) {
		
		return manager.createQuery("select new br.edu.ifrn.sinapiPRO.dto.ItemBasePrecoDTO(anoMes, preco) "
				+ "from ItemBasePreco as i where i.baseKey.insumoID =:codigo", ItemBasePrecoDTO.class)
				.setParameter("codigo", codigoInsumo).getResultList();
	}
		
	@Override
	public List<InsumoDTO> porCodigoInsumoOuNome(Long codigoBaseInsumo, String codigoOuNome) {
			
		Long codigoInsumo = 0L;
		if (Lib.IsNumeric(codigoOuNome)) {
			codigoInsumo = Long.valueOf(codigoOuNome);
		}
		
		String jpql = "select new br.edu.ifrn.sinapiPRO.dto.InsumoDTO(codigoInsumo, descricao, unidade,  precoPadrao) "
				    + "from Insumo where InsumoID.codigo_base_insumo=:codigoBaseInsumo and"
				    + "                  InsumoID.codigo_Insumo:=codigoInsumo          or "
				    + "                  lower(descricao) like lower(:codigoOuNome)";
		
		List<InsumoDTO> insumosFiltrados = manager.createQuery(jpql, InsumoDTO.class)
					.setParameter("codigoOuNome", codigoOuNome + "%")
					.setParameter("codigoBaseInsumo", codigoBaseInsumo)
					.setParameter("codigoInsumo", codigoInsumo)
					.getResultList();
		
		return  insumosFiltrados;
	}
	
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

	@Override
	public List<ItemBasePrecoDTO> listaPrecosPorInsumo(Long codigoInsumo) {
		// TODO Auto-generated method stub
		return null;
	}
}
