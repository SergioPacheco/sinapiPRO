package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.dto.BasePrecoItemDTO;
import br.edu.ifrn.sinapiPRO.dto.InsumoDTO;
import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.InsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.filter.ListaInsumosFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class InsumoService {

	private final InsumosRepository insumosRepository;

	public InsumoService(InsumosRepository insumosRepository) {
		this.insumosRepository = insumosRepository;
	}
	
	@Transactional
	public Insumo salvar(Insumo insumo) {
		
		return insumosRepository.saveAndFlush(insumo);
	}
	 
	@Transactional
	public void excluir(Insumo insumo) {
		try {
			insumosRepository.delete(insumo);
			insumosRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Insumo. Já foi usado em algum orçamento.");
		}
	}
	
	@Transactional(readOnly = true)
	public Optional<Insumo> findByBaseInsumoAndCodigoInsumo(BaseInsumo baseInsumo, String codigoInsumo) {
		return insumosRepository.findByBaseInsumoAndCodigoInsumo(baseInsumo, codigoInsumo);
	}
	
	@Transactional(readOnly = true)
	public List<BasePrecoItemDTO> listaPrecosPorInsumo(String codigoInsumo) {
		return insumosRepository.listaPrecosPorInsumo(codigoInsumo);
	}
	
	@Transactional(readOnly = true)
	public List<InsumoDTO> porDescricao(String descricao) {
		return insumosRepository.porDescricao(descricao); 
	}

	/**
	 *  filtra os insumos
	 */
	@Transactional(readOnly = true)
	public Page<Insumo> filtrar(InsumoFilter filtro, Pageable pageable) {
		return insumosRepository.filtrar(filtro, pageable);
	}
	
	/**
	 *  filtra a lista de insumos do orcamento
	 */
	public Page<Item> filtrarInsumos(ListaInsumosFilter filtro, Pageable pageable) {
		return insumosRepository.filtrarInsumos(filtro, pageable);
	}
	 
}
