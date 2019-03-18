package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.repository.filter.EtapaFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.EtapaService;
import br.edu.ifrn.sinapiPRO.service.ItemService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/etapas")
public class EtapasController {

	
	private EtapaService etapaService;
	private OrcamentoService orcamentoService;
	
	@Autowired
	public EtapasController (EtapaService etapaService, OrcamentoService orcamentoService) {
		this.etapaService = etapaService;
		this.orcamentoService = orcamentoService;
	}
	
	@RequestMapping("/nova")
	public ModelAndView nova(Etapa etapa) {
		return new ModelAndView("etapa/CadastroEtapa");
	}
	
	@RequestMapping(value = { "/nova", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Etapa etapa, BindingResult result, 
			RedirectAttributes attributes){
		if (result.hasErrors()) {
			return nova(etapa);
		}
		
		try{
			etapaService.salvar(etapa);
		} catch(JaCadastradoException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return nova(etapa);
		}
		attributes.addFlashAttribute("mensagem", "Etapa salvo com sucesso!");
		
		return new ModelAndView("redirect:/etapas/nova");// Redirect
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Etapa etapa, BindingResult result ){
		
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		etapa = etapaService.salvar(etapa); 
		return ResponseEntity.ok(etapa);
	}
	
	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Etapa> pesquisar(String nome) {
		// validarTamanhoNome(nome);
		return etapaService.findByNomeStartingWithIgnoreCase(nome);
	}
	
	@GetMapping
	public ModelAndView pesquisar(EtapaFilter etapaFilter, BindingResult result
			,@PageableDefault(size = 25) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("etapa/PesquisaEtapas");
		
		PageWrapper<Etapa> paginaWrapper = new PageWrapper<>(etapaService.filtrar(etapaFilter, pageable)
				, httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Etapa etapa = etapaService.getOne(codigo);
		ModelAndView mv = nova(etapa);
		mv.addObject(etapa);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			etapaService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/adicionarEtapa/{codigo}")
	public ModelAndView addEtapa(@PathVariable("codigo") Etapa etapa, 
								 @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		Optional<Orcamento> orcamentoAtual = orcamentoService.findOrcamentoAtual(usuarioSistema.getUsername());
		
		if (!orcamentoAtual.isPresent()) {
			System.out.println("ADICIONA ETAPA: Orçamento Atual não está presente da variavel global");
			return new ModelAndView("redirect:/orcamentos");
		}
	
		Item item = new Item();
		
		
		item.setTipo(Tipo.ETAPA); 
		item.setItemizacao(etapa.getCodigo().toString()+".");
		item.setDescricao(etapa.getNome());
		item.setEtapa( etapa);
		item.setOrcamento(orcamentoAtual.get());
		item.setQuantidade(BigDecimal.ZERO);
		item.setValorUnitario(BigDecimal.ZERO);
		item.setValorEquipamento(BigDecimal.ZERO);
		item.setValorMaterial(BigDecimal.ZERO);
		item.setValorMaoObra(BigDecimal.ZERO);
		
		orcamentoAtual.get().addItem(item); 
		orcamentoService.salvar(orcamentoAtual.get()); 
		 
		return new ModelAndView("redirect:/atual");

	}
	
}
