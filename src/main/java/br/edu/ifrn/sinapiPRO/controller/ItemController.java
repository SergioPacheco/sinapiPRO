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
import br.edu.ifrn.sinapiPRO.repository.ItemsRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ItemFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.ItemService;
import br.edu.ifrn.sinapiPRO.session.orcamento.TabelasItensOrcamentoSession;
 
@Controller
@RequestMapping("/item")
public class ItemController {
	
	
	@Autowired
	private Etapas etapasRepository;
	
	@Autowired
	private Composicoes composicoesRepository;
	
	@Autowired
	private Insumos insumosRepository;
	
	@Autowired
	private TabelasItensOrcamentoSession tabelaItens;
	
	@Autowired
	private ItemsRepository itemsRepository;
	
	@Autowired
	private ItemService itemService;
	
	//@Autowired
	//private OrcamentoValidator orcamentoValidator;
	
	@Autowired
	private OrcamentosRepository orcamentosRepository;
	
	@GetMapping("/novo")
	public ModelAndView novo(Item item) {
		ModelAndView mv = new ModelAndView("item/CadastroItem");
		
		//setUuid(item);
		
		//mv.addObject("itens", orcamento.getItens());
		//mv.addObject("valorTotalItens", tabelaItens.getValorTotal(orcamento.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = "/novo", params = "salvar")
	public ModelAndView salvar(Item item, 
			                BindingResult result, 
			           RedirectAttributes attributes, 
			      @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		// validarOrcamento(orcamento, result);
		
		if (result.hasErrors()) {
			return novo(item);
		}
		
		// item.setUsuario(usuarioSistema.getUsuario());
		
		itemService.salvar(item);
		attributes.addFlashAttribute("mensagem", "Item salvo com sucesso");
		return new ModelAndView("redirect:/item/novo");
	}
		
	@PostMapping("/etapa")
	public ModelAndView adicionarEtapa(Long codigoEtapa, String uuid) {
		
		Etapa etapa = etapasRepository.findById(codigoEtapa).get();
		tabelaItens.adicionarItem(uuid, etapa);
		return mvTabelaItensOrcamento(uuid);
	}
	
	@PostMapping("/insumo")
	public ModelAndView adicionarInsumo(Long codigoEtapa, Long codigoInsumo, String uuid) {
		
		Insumo insumo = insumosRepository.findById(codigoInsumo).get();
		Etapa etapa = etapasRepository.findById(codigoEtapa).get();
		tabelaItens.adicionarItem(uuid, etapa, insumo, BigDecimal.ONE);
		return mvTabelaItensOrcamento(uuid);
	}
	
	@PostMapping("/composicao")
	public ModelAndView adicionarComposicao(Long codigoEtapa, Long codigoComposicao, String uuid) {
		
		Composicao composicao = composicoesRepository.getOne(codigoComposicao);
		Etapa etapa = etapasRepository.findById(codigoEtapa).get();
		tabelaItens.adicionarItem(uuid, etapa, composicao, BigDecimal.ONE);
		
		return mvTabelaItensOrcamento(uuid);
	}
	
	@PutMapping("/item/{etapa}/{tipo}/{codigoItem}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("etapa") Long codigoEtapa,	
											  @PathVariable("tipo") String tipo,
											  @PathVariable("codigoItem") Long codigoItem, String uuid, Integer quantidade) {
		
		// tabelaItens.alterarQuantidade(uuid, tipo, codigoEtapa, codigoItem, quantidade);  
		
		return mvTabelaItensOrcamento(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoItem}/{tipo}")
	public ModelAndView excluirItem(@PathVariable("uuid") String uuid, 
			                        @PathVariable("codigoItem") Long codigoItem, 
			                        @PathVariable("tipo") String tipo) {
	 
		Etapa etapa = etapasRepository.findById(codigoItem).get();
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
	
	@GetMapping("{codigoOrcamento}")
	public ModelAndView pesquisar(@PathVariable Long codigoOrcamento,
			                      ItemFilter itemFilter, 
							      Orcamento orcamento,
			                      @PageableDefault(size = 20) Pageable pageable, 
			                      HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("orcamento/PesquisaItem");
		
		if (orcamento == null) {
			Optional<Orcamento> orcamentoGET = orcamentosRepository.findById(codigoOrcamento);
			if (orcamentoGET.isPresent()) { 
				orcamento = orcamentoGET.get();
			}
		}
		
		mv.addObject("orcamentoHead", orcamento);
	
		PageWrapper<Item> paginaWrapper = new PageWrapper<>(itemsRepository.filtrar(itemFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	
	
	@GetMapping("/editar/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		
	 
		Item itemEditar = itemsRepository.findById(codigo).get();
		Orcamento orcamento = itemEditar.getOrcamento(); 
		
		// setUuid(orcamento);
		
		//List<Item> itens = itemsRepository.findAll
		
		//for (Item i : orcamento.getItens()) {
		//	tabelaItens.adicionarItem(tipo, orcamento.getUuid(), item.getComposicao(), item.getQuantidade());
		//}
		
		ModelAndView mv = novo(itemEditar);
		
		mv.addObject(orcamento);
		return mv;
		
	}
	
	
	private ModelAndView mvTabelaItensOrcamento(String uuid) {
		
		ModelAndView mv = new ModelAndView("item/TabelaItensOrcamento");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	/*
	private void validarOrcamento(Orcamento orcamento, BindingResult result) {
		orcamento.adicionarItens(tabelaItens.getItens(orcamento.getUuid()));
		
		orcamentoValidator.validate(orcamento, result);
	}
	*/
	
	private void setUuid(Orcamento orcamento) {
		if (StringUtils.isEmpty(orcamento.getUuid())) {
			orcamento.setUuid(UUID.randomUUID().toString());
		}
	}
	
}

