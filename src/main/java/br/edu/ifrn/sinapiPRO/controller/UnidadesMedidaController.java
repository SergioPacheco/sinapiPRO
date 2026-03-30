package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.filter.UnidadeMedidaFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroUnidadeMedidaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/unidadesMedida")
public class UnidadesMedidaController {

	@Autowired
	private CadastroUnidadeMedidaService service;

	@GetMapping("/novo")
	public ModelAndView novo(UnidadeMedida u) {
		return new ModelAndView("unidademedida/CadastroUnidadeMedida");
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid UnidadeMedida u, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) return novo(u);
		try { service.salvar(u);
	}
		catch (JaCadastradoException e) { result.rejectValue("nome", e.getMessage(), e.getMessage()); return novo(u);
	}
		attributes.addFlashAttribute("mensagem", "Unidade de medida salva com sucesso!");
		return new ModelAndView("redirect:/unidadesMedida/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(UnidadeMedidaFilter filtro, BindingResult result,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("unidademedida/PesquisaUnidadesMedida");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(filtro, pageable), request));
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		ModelAndView mv = novo(service.getOne(codigo));
		mv.addObject(service.getOne(codigo));
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo);
	}
		catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage());
	}
		return ResponseEntity.ok().build();
	}
}
