package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
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
import br.edu.ifrn.sinapiPRO.model.Base;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.ItemComposicao;
import br.edu.ifrn.sinapiPRO.repository.Classes;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.repository.Estados;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.CadastroComposicaoService;
import br.edu.ifrn.sinapiPRO.session.TabelaItensComposicaoSession;

@Controller
@RequestMapping("/composicoes")
public class ComposicoesController {
	
	@Autowired
	private Insumos insumos;
	
	@Autowired
	private Estados estados;
	
	@Autowired
	private Classes classes;
	
	@Autowired 
	private TabelaItensComposicaoSession tabelaItensComposicao; 
	
	@Autowired
	private CadastroComposicaoService cadastroComposicaoService;
	
	@Autowired
	private Composicoes composicoes;

	@GetMapping("/nova")
	public ModelAndView nova(Composicao composicao) {
		ModelAndView mv = new ModelAndView("composicao/CadastroComposicao");
		
		setUuid(composicao);
		mv.addObject("bases", Base.values());
		mv.addObject("estados", estados.findAll());
		mv.addObject("classes", classes.findAll());
		mv.addObject("itens", composicao.getItens());
		mv.addObject("valorTotal", tabelaItensComposicao.getValorTotal(composicao.getUuid()));
		return mv;
	}
	
	@PostMapping(value = "/nova")
	public ModelAndView salvar(Composicao composicao, 
			                    BindingResult result, 
			           RedirectAttributes attributes, 
			                 @AuthenticationPrincipal UsuarioSistema usuarioSistema){

		
		composicao.setUsuario(usuarioSistema.getUsuario());

		cadastroComposicaoService.salvar(composicao);
		attributes.addFlashAttribute("mensagem", "Composicao salva com sucesso!");
		return new ModelAndView("redirect:/composicoes/nova");
	}
			
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoInsumo, String uuid){
		Insumo insumo = insumos.getOne(codigoInsumo);
		tabelaItensComposicao.adicionarItem(uuid, insumo, new BigDecimal(1));
		return mvTabelaItensComposicao(uuid);
	}
	
	@PutMapping("/item/{codigoInsumo}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoInsumo") Insumo insumo
				, BigDecimal coeficiente, String uuid){
		 
		tabelaItensComposicao.alterarQuantidadeItens(uuid, insumo, coeficiente);
		return mvTabelaItensComposicao(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoInsumo}")
	public ModelAndView excluirItem (@PathVariable("codigoInsumo") Insumo insumo
			, @PathVariable String uuid){
		tabelaItensComposicao.excluirItem(uuid, insumo);
		return mvTabelaItensComposicao(uuid);
	}
	
	@GetMapping
	public ModelAndView pesquisar(ComposicaoFilter composicaoFilter,
			           @PageableDefault(size = 5) Pageable pageable, 
			                  HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/composicao/PesquisaComposicoes");
		mv.addObject("bases", Base.values());
		mv.addObject("estados", estados.findAll());
		mv.addObject("classes", classes.findAll());
		PageWrapper<Composicao> paginaWrapper = new PageWrapper<>(composicoes.filtrar(composicaoFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Composicao composicao = composicoes.buscarComItens(codigo);
		
		setUuid(composicao);
		for (ItemComposicao	item : composicao.getItens() ) {
			tabelaItensComposicao.adicionarItem(composicao.getUuid(), item.getInsumo(), item.getCoeficiente());
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