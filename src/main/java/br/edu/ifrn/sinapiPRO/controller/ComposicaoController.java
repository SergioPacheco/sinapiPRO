package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.dto.ComposicaoDTO;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoClassesRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoGruposRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoItemRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.ComposicaoService;
import br.edu.ifrn.sinapiPRO.session.composicao.TabelaComposicaoItemSession;

@Controller
@RequestMapping("/composicoes")
public class ComposicaoController {
	
	@Autowired
	private TabelaComposicaoItemSession tabelaItens;
	
	@Autowired
	private ComposicaoRepository composicaoRepository;
	
	@Autowired
	private ComposicaoService composicaoService;
	
	@Autowired
	private ComposicaoClassesRepository composicaoClassesRepository;
	
	@Autowired
	private ComposicaoGruposRepository composicoaoGruposRepository;
	
	@Autowired
	private BaseInsumosRepository baseInsumosRepository;
	
	@Autowired
	private InsumosRepository insumosRepository;
	
	@Autowired
	private BasePrecosRepository basePrecosRepository;
	
	@Autowired 
	private ComposicaoItemRepository composicaoItemRepository; 

	@GetMapping("/nova")
	public ModelAndView nova(Composicao composicao) {
		
		ModelAndView mv = new ModelAndView("composicao/CadastroComposicao");
		
		setUuid(composicao);
		
		mv.addObject("itens", composicao.getItens());
		mv.addObject("basePrecos", basePrecosRepository.findAll());
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("composicaoClasses", composicaoClassesRepository.findAll());
		mv.addObject("composicaoGrupos", composicoaoGruposRepository.findAll());
		mv.addObject("valorTotal", tabelaItens.getValorTotal(composicao.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Composicao composicao, 
			                    BindingResult result, 
			           RedirectAttributes attributes, 
			                 @AuthenticationPrincipal UsuarioSistema usuarioSistema){

		composicao.setUsuario(usuarioSistema.getUsuario());
		
		tabelaItens
				.getItens(composicao.getUuid())
				.forEach(i-> composicao.addItem(i));;
		
		//for (ComposicaoItem i:listaItem) {
		//	composicao.addItem(i);
		//}
		 
		composicao.calcularValorTotal();
		composicaoService.salvar(composicao);
		
		// composicaoItemRepository.saveAll(listaItem );
		
		attributes.addFlashAttribute("mensagem", "Composicao salva com sucesso!");
		return new ModelAndView("redirect:/composicoes/"+composicao.getCodigo());
	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigo, String uuid){
		
		if (codigo == null) { System.out.println("codigo ITEM NULO");
		} else { 			  System.out.println("CODIGO INSUMO "+ codigo);
		}
		Insumo insumo = insumosRepository.findById(codigo).get();
		tabelaItens.adicionarItem(uuid, insumo, new BigDecimal(1));
		
		return mvTabelaItensComposicao(uuid); 
	}
	
	@PutMapping("/item/{codigoInsumo}")
	public ModelAndView alterarQuantidadeItem(
			      		@PathVariable("codigoInsumo") Insumo insumo, BigDecimal coeficiente, String uuid){
		 
		tabelaItens.alterarCoeficiente(uuid, insumo, coeficiente); 
		return mvTabelaItensComposicao(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoInsumo}")
	public ModelAndView excluirItem (@PathVariable("codigoInsumo") Insumo insumo, 
								     @PathVariable("uuid") String uuid) {
		
		tabelaItens.excluirItem(uuid, insumo);
		return mvTabelaItensComposicao(uuid);
	}

	@GetMapping
	public ModelAndView pesquisar(ComposicaoFilter composicaoFilter,
			                      @PageableDefault(size = 20) Pageable pageable, 
			                      HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("/composicao/PesquisaComposicoes");
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("composicaoClasses", composicaoClassesRepository.findAll());
		mv.addObject("composicaoGrupos", composicoaoGruposRepository.findAll());
		
		PageWrapper<Composicao> paginaWrapper = new PageWrapper<>(composicaoRepository.filtrar(composicaoFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	/**
	 * Pesquisa todas composições pela descrição por base de insumo 
	 *  
	 * @param codigoBaseInsumo
	 * @param descricao
	 * @return
	 */
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ComposicaoDTO> pesquisar(Long codigoBaseInsumo, String descricao) {
		return composicaoRepository.porDescricao(codigoBaseInsumo, descricao);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		
		Composicao composicao = composicaoRepository.buscarComItens(codigo);
		setUuid(composicao);
		for (ComposicaoItem	item : composicao.getItens() ) {
			if (item.getTipo().equals("INSUMO")) {
				tabelaItens.adicionarItem(composicao.getUuid(), item.getInsumo(), item.getCoeficiente());
			} else {
				System.out.println("COMPOSICAO");
			}
		}
		
		ModelAndView mv = nova(composicao);
		mv.addObject(composicao);
		return mv;
	}
		
	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Composicao composicao, BindingResult result
				, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			composicaoService.cancelar(composicao);
		} catch (AccessDeniedException e) {
			ModelAndView mv = new ModelAndView("error");
			mv.addObject("status", 403);
			return mv;
		}
		
		attributes.addFlashAttribute("mensagem", "Composicao cancelada com sucesso");
		return new ModelAndView("redirect:/composicoes/" + composicao.getCodigo());
	}
	
	private ModelAndView mvTabelaItensComposicao(String uuid) {
		
		ModelAndView mv = new ModelAndView("composicao/TabelaItensComposicao");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		
		return mv;
	}
	
	private void setUuid(Composicao composicao) {
		if(StringUtils.isEmpty(composicao.getUuid())){
			composicao.setUuid(UUID.randomUUID().toString());
		}
	}
	
}