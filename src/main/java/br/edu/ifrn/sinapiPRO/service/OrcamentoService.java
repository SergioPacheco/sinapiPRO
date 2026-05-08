package br.edu.ifrn.sinapiPRO.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.OrcamentoSituacao;
import br.edu.ifrn.sinapiPRO.model.TipoOrcamento;
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
	private final AuditService auditService;

	public OrcamentoService (OrcamentosRepository orcamentoRepository, EtapasRepository etapasRepository,
							 UsuarioService usuarioService, AuditService auditService) {
		this.orcamentosRepository = orcamentoRepository;
		this.usuarioService = usuarioService;
		this.etapasRepository = etapasRepository;
		this.auditService = auditService;
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

		auditService.registrar("Orcamento", orcamento.getCodigo(), "SALVAR", orcamento.getNome());
		return orcamento;
	}
 
	@Transactional
	public void excluir(Orcamento orcamento) {
		try {
			Long codigo = orcamento.getCodigo();
			orcamentosRepository.delete(orcamento);
			orcamentosRepository.flush();
			auditService.registrar("Orcamento", codigo, "EXCLUIR", null);
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

	@Transactional
	public Orcamento copiarOrcamento(Long codigoOrigem, TipoOrcamento novoTipo) {
		Orcamento origem = orcamentosRepository.buscarComItens(codigoOrigem);
		if (origem == null) throw new RuntimeException("Orçamento não encontrado");

		Orcamento copia = new Orcamento();
		copia.setNome(origem.getNome() + " (" + novoTipo.getDescricao() + ")");
		copia.setTipoOrcamento(novoTipo);
		copia.setBaseInsumo(origem.getBaseInsumo());
		copia.setBasePreco(origem.getBasePreco());
		copia.setEstado(origem.getEstado());
		copia.setDataCriacao(LocalDateTime.now());
		copia.setCliente(origem.getCliente());
		copia.setUsuario(origem.getUsuario());
		copia.setObra(origem.getObra());
		copia.setDesoneracao(origem.getDesoneracao());
		copia.setSituacao(OrcamentoSituacao.ABERTO);
		copia.setObservacao(origem.getObservacao());
		copia.setPercentualBdi(origem.getPercentualBdi());
		copia.setPercentualLeisSociais(origem.getPercentualLeisSociais());
		copia.setPercentualTaxaAdm(origem.getPercentualTaxaAdm());
		copia.setPercentualBdiInsumo(origem.getPercentualBdiInsumo());
		copia.setPercentualBdiServico(origem.getPercentualBdiServico());
		copia.setPercentualBdiTerceiro(origem.getPercentualBdiTerceiro());
		copia.setPercentualBdiFerramenta(origem.getPercentualBdiFerramenta());
		copia.setTipoArredondamento(origem.getTipoArredondamento());
		copia.setDecimaisArredondamento(origem.getDecimaisArredondamento());

		orcamentosRepository.saveAndFlush(copia);

		for (Item orig : origem.getItens()) {
			Item novo = new Item();
			novo.setOrcamento(copia);
			novo.setTipo(orig.getTipo());
			novo.setDescricao(orig.getDescricao());
			novo.setItemizacao(orig.getItemizacao());
			novo.setEspecie(orig.getEspecie());
			novo.setUnidade(orig.getUnidade());
			novo.setQuantidade(orig.getQuantidade());
			novo.setValorUnitario(orig.getValorUnitario());
			novo.setValorMaoObra(orig.getValorMaoObra());
			novo.setValorMaterial(orig.getValorMaterial());
			novo.setValorEquipamento(orig.getValorEquipamento());
			novo.setEtapa(orig.getEtapa());
			novo.setComposicao(orig.getComposicao());
			novo.setInsumo(orig.getInsumo());
			novo.setTipoCusto(orig.getTipoCusto());
			copia.getItens().add(novo);
		}
		orcamentosRepository.saveAndFlush(copia);
		auditService.registrar("Orcamento", copia.getCodigo(), "COPIAR",
				"Origem: " + codigoOrigem + " → " + novoTipo.getDescricao());
		return copia;
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
