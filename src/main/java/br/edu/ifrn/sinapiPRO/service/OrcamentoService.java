package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.EtapasRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.OrcamentoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class OrcamentoService {
	
	private final OrcamentosRepository orcamentosRepository;
	private final UsuarioService usuarioService;
	private final EtapasRepository etapasRepository;
	
	@Autowired
	public OrcamentoService (OrcamentosRepository orcamentoRepository, EtapasRepository etapasRepository,
							 UsuarioService usuarioService) {
		this.orcamentosRepository = orcamentoRepository;
		this.usuarioService = usuarioService;
		this.etapasRepository = etapasRepository;
	}
	
	@Transactional
	public Orcamento salvar(Orcamento orcamento){
		
		orcamentosRepository.save(orcamento);
		
		Optional<Usuario> usuarioExistente = usuarioService
				.findByNome(orcamento.getUsuario().getNome());
		
		if (usuarioExistente.isPresent()) {
	    	Usuario editaUsuario = usuarioExistente.get();
	    	
	    	orcamento.setUsuario(usuarioExistente.get());
	    	orcamentosRepository.save(orcamento);
	    	
	    	editaUsuario.setCodigoOrcamentoAtual(orcamento.getCodigo());
	    	usuarioService.salvar(editaUsuario);
	    	orcamentosRepository.save(orcamento);
	    } else {
	    	throw new RuntimeException("usuario não encontrado ao salvar orcamento");
	    }
		 
		return orcamento;
	}
 
	@Transactional
	public void excluir(Orcamento orcamento) {
		try {
			orcamentosRepository.delete(orcamento);
			orcamentosRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível excluir Orcamento.");

		}
	}
	
	@Transactional(readOnly = true)
	public Page<Orcamento> filtrar(OrcamentoFilter filtro, Pageable pageable) {
		return orcamentosRepository.filtrar(filtro, pageable);
	}
     
	@Transactional(readOnly = true)
	public Optional<Orcamento> findOrcamentoAtual(String email) {
		
		Optional<Usuario> usuarioExistente = usuarioService.findByEmail(email);

	    if (!usuarioExistente.isPresent()) {
	    	throw new RuntimeException("usuario "+email+" não encontrado ao pesquisar orçamento existente");
	    }	
	    Optional<Orcamento> orcamentoExistente = Optional.empty();
	    Usuario usuario = usuarioExistente.get();
	    if (usuario.getCodigoOrcamentoAtual()!=null) {
	       orcamentoExistente =  orcamentosRepository.findById(usuario.getCodigoOrcamentoAtual());
	    }   
	    
	    return orcamentoExistente;
	}
	
	@Transactional(readOnly = true)
	public Optional<Etapa> findEtapaSelecionada(String email) {
		Optional<Usuario> usuarioExistente = usuarioService.findByEmail(email);

	    if (!usuarioExistente.isPresent()) {
	    	System.out.println("nomeUsuario "+email );
	    	throw new RuntimeException("usuario "+ email+" não encontrado ao pesquisar etapa selecionada");
	    }	
	    Optional<Etapa> etapaSelecionada = Optional.empty();
	    Usuario usuario = usuarioExistente.get();
	    if (usuario.getEtapaSelecionada() !=null && usuario.getEtapaSelecionada().getCodigo() !=null) {
	    	 etapaSelecionada = etapasRepository.findById(usuario.getEtapaSelecionada().getCodigo());
	    }
	    
	    return etapaSelecionada;
	}

	public Orcamento buscarComItens(Long codigo) {
		return orcamentosRepository.buscarComItens(codigo);
	}

	@Transactional(readOnly = true)
	public Optional<Long> getCodigoOrcamentoAtual(String username) {
		return usuarioService.findByNome(username)
				.map(Usuario::getCodigoOrcamentoAtual);
	}

	@Transactional
	public void selecionarOrcamento(String email, Long codigoOrcamento) {
		Usuario usuario = usuarioService.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		usuarioService.alteraOrcamentoAtual(usuario, codigoOrcamento);
	}
	
}
 