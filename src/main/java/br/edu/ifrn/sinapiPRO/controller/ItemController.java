package br.edu.ifrn.sinapiPRO.controller;
 
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.repository.Etapas;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.OrcamentoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.session.orcamento.TabelasItensOrcamentoSession;
import br.edu.ifrn.sinapiPRO.utils.Lib;
 
@Controller
@RequestMapping("/item")
public class ItemController {
	 
	@Autowired private Etapas etapasRepository;
	@Autowired private Composicoes composicoesRepository;
	@Autowired private Insumos insumosRepository;
	@Autowired private TabelasItensOrcamentoSession tabelaItens;
	@Autowired private OrcamentoValidator orcamentoValidator;
	@Autowired private OrcamentosRepository orcamentosRepository;
	@Autowired private OrcamentoService orcamentoService;
	
	@GetMapping("/novo")
	public ModelAndView novo(Orcamento orcamento) {
		ModelAndView mv = new ModelAndView("orcamento/CadastroItem");
		
		setUuid(orcamento);
		
		mv.addObject("itens", orcamento.getItens());
		mv.addObject("valorTotalItens",       tabelaItens.getValorTotal(orcamento.getUuid()));
		mv.addObject("valorMaoObraItens",     tabelaItens.getValorMaoObra(orcamento.getUuid()));
		mv.addObject("valorMaterialItens",    tabelaItens.getValorMaterial(orcamento.getUuid()));
		mv.addObject("valorEquipamentoItens", tabelaItens.getValorEquipamento(orcamento.getUuid()));
		
		return mv;
	}
	 
	@PostMapping(value = "/novo", params = "salvar")
	public ModelAndView salvar(Orcamento orcamento, 
			                BindingResult result, 
			           RedirectAttributes attributes, 
			      @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		validarOrcamento(orcamento, result);
		
		orcamento.setUsuario(usuarioSistema.getUsuario());
		
		orcamentoService.salvar(orcamento);
		attributes.addFlashAttribute("mensagem", "Orcamento salvo com sucesso");
		
		String str = "redirect:/item/"+orcamento.getCodigo().toString();
		return new ModelAndView(str);
	}
		
	
	@PostMapping("/etapa")
	public ModelAndView adicionarEtapa(Long codigoEtapa,	String uuid) {

		Etapa etapa = pesquisaEtapa(codigoEtapa);
	  
		tabelaItens.adicionarItem(uuid, etapa);
		return mvTabelaItensOrcamento(uuid);
	}
	
	@PostMapping("/composicao")
	public ModelAndView adicionarComposicao(Long codigoEtapa,	Long codigoComposicao, String uuid) {
		System.out.println("adicionarComposicao "+codigoComposicao );
		
		Etapa etapa = pesquisaEtapa(codigoEtapa);
				
		Optional<Composicao> optionalComposicao = composicoesRepository.findById(codigoComposicao);
		if(optionalComposicao.isPresent()) { 
			tabelaItens.adicionarItem(uuid, etapa, optionalComposicao.get(), BigDecimal.ONE);
		}
		return mvTabelaItensOrcamento(uuid);
	}
	
	@PostMapping("/insumo") 
	public ModelAndView adicionarInsumo(Long codigoEtapa, Long codigoInsumo, String uuid) {
		
		System.out.println("Incluir insumo=" + codigoInsumo +" Etapa="+codigoEtapa);
		
		Etapa etapa = pesquisaEtapa(codigoEtapa);
		
		Optional<Insumo> insumoOptional = insumosRepository.findByCodigoInsumo(codigoInsumo);
		if (insumoOptional.isPresent()) {
			tabelaItens.adicionarItem(uuid, etapa, insumoOptional.get(), BigDecimal.ONE);
		} else {
			System.out.println("Insumo não entrado=" + codigoInsumo +" Etapa="+codigoEtapa);
		}
		return mvTabelaItensOrcamento(uuid);
	}
	 
	
	@PutMapping("/{etapa}/{tipo}/{codigoItem}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("etapa") Long codigoEtapa,	
											  @PathVariable("tipo") String tipo,
											  @PathVariable("codigoItem") Long codigoItem, 
											  String uuid, 
											  BigDecimal quantidade) {
		
		Etapa etapa = pesquisaEtapa(codigoEtapa);
		
		if (tipo.equals("C")) {
			System.out.println("ALTERAR QUANTIDADE - composicao="+codigoItem+" etapa="+codigoEtapa);
			Composicao composicao = composicoesRepository.findById(codigoItem).get();
			tabelaItens.alterarQuantidade(uuid, etapa, composicao, quantidade);   
		} else {
			if (tipo.equals("I")) {
				System.out.println("ALTERAR QUANTIDADE - insumo="+codigoItem+" etapa="+codigoEtapa);
				Insumo insumo = insumosRepository.getOne(codigoItem);
				tabelaItens.alterarQuantidade(uuid, etapa, insumo, quantidade);
			} 
		}
		
		return mvTabelaItensOrcamento(uuid);
	}
	
	@DeleteMapping("/{uuid}/{etapa}/{tipo}/{codigoItem}")
	public ModelAndView excluirItem(@PathVariable("uuid") String uuid, 
									@PathVariable("codigoEtapa") Long codigoEtapa,
			                        @PathVariable("codigoItem") Long codigoItem, 
			                        @PathVariable("tipo") String tipo) {
	 
		Etapa etapa = etapasRepository.findById(codigoEtapa).get();
		if (tipo.equals("E")) {
			tabelaItens.excluirItem(uuid, etapa);
		} else {
			if (tipo.equals("C")) {
				Composicao composicao = composicoesRepository.getOne(codigoItem);
				tabelaItens.excluirItem(uuid, etapa, composicao);
			} else {
				if (tipo.equals("I")) {
					Insumo insumo = insumosRepository.findById(codigoItem).get();
					tabelaItens.excluirItem(uuid, etapa, insumo);
				}	
			}
		}
		
		return mvTabelaItensOrcamento(uuid);
	}
	
	@GetMapping 
	public ModelAndView pesquisar(@PathVariable Long codigoOrcamento,
			                      OrcamentoFilter orcamentoFilter, 
							      Orcamento orcamento,
			                      @PageableDefault(size = 20) Pageable pageable, 
			                      HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("orcamento/PesquisaOrcamento");
		
		PageWrapper<Orcamento> paginaWrapper = new PageWrapper<>(orcamentosRepository
				.filtrar(orcamentoFilter, pageable), httpServletRequest);
		
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
	 
		Orcamento orcamento = orcamentosRepository.buscarComItens(codigo);
		
		setUuid(orcamento);
		
		
		for (Item item : orcamento.getItens()) {
			
			if (item.getTipo().equals("C")) {
			   tabelaItens.adicionarItem(orcamento.getUuid(), item.getEtapa(), item.getComposicao(), item.getQuantidade());
			} else {
				if (item.getTipo().equals("I")) {
					tabelaItens.adicionarItem(orcamento.getUuid(), item.getEtapa(),	item.getInsumo(), item.getQuantidade());
				} else {
					if (item.getTipo().equals("E")) {
						tabelaItens.adicionarItem(orcamento.getUuid(), item.getEtapa());
					}
				}
			}
		}
		
		ModelAndView mv = novo(orcamento);
		mv.addObject(orcamento);
		return mv;

	}
	
	
	private ModelAndView mvTabelaItensOrcamento(String uuid) {
		ModelAndView mv = new ModelAndView("orcamento/TabelaItensOrcamento");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorMaterial", tabelaItens.getValorMaterial(uuid));	
		mv.addObject("valorMaoObra", tabelaItens.getValorMaoObra(uuid));
		mv.addObject("valorEquipamento", tabelaItens.getValorEquipamento(uuid));
		
		return mv;
	}
	

	private void validarOrcamento(Orcamento orcamento, BindingResult result) {
		orcamento.adicionarItens(tabelaItens.getItens(orcamento.getUuid()));
		orcamento.calcularValorTotal();
		orcamento.calcularValorMaoObra(); 
		orcamento.calcularValorMaterial(); 
		orcamento.calcularValorEquipamento();
		
		orcamentoValidator.validate(orcamento, result);
	}
	 
	
	private void setUuid(Orcamento orcamento) {
		if (StringUtils.isEmpty(orcamento.getUuid())) {
			orcamento.setUuid(UUID.randomUUID().toString());
		}
	}
	
	private Etapa pesquisaEtapa(Long codigoEtapa) { 
		
		if (Lib.Empty(codigoEtapa)) {
			System.out.println("Codigo Etapa esta vazio !!!! ");
			codigoEtapa=1L;
		}
		Etapa etapa = new Etapa();
		Optional<Etapa> etapaOptional = etapasRepository.findById(codigoEtapa);
		if (!etapaOptional.isPresent()) { 
			etapa.setNome("N O V A   E T A P A  P A R A  E D I T A R");
			etapa=etapasRepository.saveAndFlush(etapa);
		} else { 
			etapa = etapaOptional.get();
		}
		
		return etapa;
	}
}

