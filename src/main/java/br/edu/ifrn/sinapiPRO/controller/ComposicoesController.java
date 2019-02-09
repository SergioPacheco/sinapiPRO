package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.ClassesRepository;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.repository.GrupoComposicaoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.ComposicaoService;
import br.edu.ifrn.sinapiPRO.session.composicao.TabelaItensComposicaoSession;

@Controller
@RequestMapping("/composicoes")
public class ComposicoesController {
	
	@Autowired
	private Composicoes composicoes;
	
	@Autowired
	private ClassesRepository classesRepository;
	
	@Autowired
	private GrupoComposicaoRepository grupoComposicoes;
	
	@Autowired
	private BaseInsumosRepository baseInsumosRepository;
	
	@Autowired
	private BasePrecosRepository basePrecosRepository;
	
	@Autowired 
	private TabelaItensComposicaoSession tabelaItensComposicao; 
	
	@Autowired
	private ComposicaoService cadastroComposicaoService;
	

	@GetMapping("/nova")
	public ModelAndView nova(Composicao composicao) {
		
		ModelAndView mv = new ModelAndView("composicao/CadastroComposicao");
		
		setUuid(composicao);
		
		mv.addObject("basePrecos", basePrecosRepository.findAll());
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("classesComposicoes", classesRepository.findAll());
		mv.addObject("gruposComposicoes", grupoComposicoes.findAll());
		mv.addObject("itens", composicao.getItens());
		mv.addObject("valorTotal", tabelaItensComposicao.getValorTotal(composicao.getUuid()));
		
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Composicao composicao, 
			                    BindingResult result, 
			           RedirectAttributes attributes, 
			                 @AuthenticationPrincipal UsuarioSistema usuarioSistema){

		cadastroComposicaoService.salvar(composicao);
		attributes.addFlashAttribute("mensagem", "Composicao salva com sucesso!");
		return new ModelAndView("redirect:/composicoes/nova");
	}
			
	@PostMapping("/item")
	public ModelAndView adicionarItem(String tipo, Long codigoItem,  String uuid){
		
		// Insumo insumo = insumos.getOne(codigoInsumo);
		tabelaItensComposicao.adicionarItem(tipo, uuid, codigoItem,  new BigDecimal(0),  new BigDecimal(1));
		return mvTabelaItensComposicao(uuid);
	}
	
	@PutMapping("/item/{codigoItem}/{tipo}")
	public ModelAndView alterarQuantidadeItem(
			      		@PathVariable("codigoItem") Long codigoItem, 
			      		@PathVariable("tipo") String tipo, BigDecimal coeficiente, String uuid){
		 
		tabelaItensComposicao.alterarCoeficiente(uuid, tipo, codigoItem, coeficiente); 
		return mvTabelaItensComposicao(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoItem}/{tipo}")
	public ModelAndView excluirItem (@PathVariable("codigoItem") Long codigoItem, 
								     @PathVariable("uuid") String uuid,
									 @PathVariable("tipo") String tipo) {
		
		tabelaItensComposicao.excluirItem(uuid, codigoItem);
		return mvTabelaItensComposicao(uuid);
	}

	@GetMapping
	public ModelAndView pesquisar(ComposicaoFilter composicaoFilter,
			                      @PageableDefault(size = 5) Pageable pageable, 
			                      HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("/composicao/PesquisaComposicoes");
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("basePrecos", basePrecosRepository.findAll());
		mv.addObject("classesComposicoes", classesRepository.findAll());
		mv.addObject("gruposComposicoes", grupoComposicoes.findAll());
		
		PageWrapper<Composicao> paginaWrapper = new PageWrapper<>(composicoes.filtrar(composicaoFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ComposicaoDTO> pesquisar(String codigoOuNome) {
		return composicoes.porCodigoOuNome(codigoOuNome);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		
		Composicao composicao = composicoes.buscarComItens(codigo);
		setUuid(composicao);
		for (ComposicaoItem	item : composicao.getItens() ) {
			
			tabelaItensComposicao.adicionarItem( item.getTipo(),
					                       composicao.getUuid(),
					                             item.getCodigoItem(), 
					                             item.getPrecoUnitario(),
					                             item.getCoeficiente());
		}
		
		ModelAndView mv = nova(composicao);
		mv.addObject(composicao);
		return mv;
	}
		
	private ModelAndView mvTabelaItensComposicao(String uuid) {
		
		ModelAndView mv = new ModelAndView("composicao/TabelaItensComposicao");
		mv.addObject("itens", tabelaItensComposicao.getItens(uuid));
		mv.addObject("valorTotal", tabelaItensComposicao.getValorTotal(uuid));
		
		return mv;
	}
	
	private void setUuid(Composicao composicao) {
		
		if(StringUtils.isEmpty(composicao.getUuid())){
			composicao.setUuid(UUID.randomUUID().toString());
		}
		
	}
}