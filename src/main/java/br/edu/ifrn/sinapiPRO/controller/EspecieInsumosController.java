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
import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroEspecieInsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/especiesInsumo")
public class EspecieInsumosController {

	@Autowired
	private CadastroEspecieInsumoService service;

	@GetMapping("/novo")
	public ModelAndView novo(EspecieInsumo especieInsumo) {
		return new ModelAndView("especieinsumo/CadastroEspecieInsumo");
	}

	@PostMapping({ "/novo", "{\\d+}" })
	public ModelAndView cadastrar(@Valid EspecieInsumo especieInsumo, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(especieInsumo);
		}
		try {
			service.salvar(especieInsumo);
		} catch (JaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(especieInsumo);
		}
		attributes.addFlashAttribute("mensagem", "Espécie de insumo salva com sucesso!");
		return new ModelAndView("redirect:/especiesInsumo/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(EspecieInsumoFilter filtro, BindingResult result,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("especieinsumo/PesquisaEspecieInsumos");
		PageWrapper<EspecieInsumo> paginaWrapper = new PageWrapper<>(service.filtrar(filtro, pageable), request);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		EspecieInsumo especieInsumo = service.getOne(codigo);
		ModelAndView mv = novo(especieInsumo);
		mv.addObject(especieInsumo);
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
