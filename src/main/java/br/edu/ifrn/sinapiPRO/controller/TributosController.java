package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Tributo;
import br.edu.ifrn.sinapiPRO.repository.filter.TributoFilter;
import br.edu.ifrn.sinapiPRO.service.EstadoService;
import br.edu.ifrn.sinapiPRO.service.TributoService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/tributos")
public class TributosController {

	@Autowired
	private TributoService service;

	@Autowired
	private EstadoService estadoService;

	@GetMapping("/novo")
	public ModelAndView novo(Tributo tributo) {
		ModelAndView mv = new ModelAndView("tributo/CadastroTributo");
		mv.addObject("estados", estadoService.findAll());
		return mv;
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid Tributo tributo, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(tributo);
		}
		try {
			service.salvar(tributo);
		} catch (JaCadastradoException e) {
			result.rejectValue("descricao", e.getMessage(), e.getMessage());
			return novo(tributo);
		}
		attributes.addFlashAttribute("mensagem", "Tributo salvo com sucesso!");
		return new ModelAndView("redirect:/tributos/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(TributoFilter filtro, BindingResult result,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("tributo/PesquisaTributos");
		PageWrapper<Tributo> paginaWrapper = new PageWrapper<>(service.filtrar(filtro, pageable), request);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Tributo tributo = service.getOne(codigo);
		ModelAndView mv = novo(tributo);
		mv.addObject(tributo);
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			service.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
