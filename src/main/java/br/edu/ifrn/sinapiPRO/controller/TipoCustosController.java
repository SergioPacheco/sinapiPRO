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
import br.edu.ifrn.sinapiPRO.model.TipoCusto;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoCustoFilter;
import br.edu.ifrn.sinapiPRO.service.TipoCustoService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/tiposCusto")
public class TipoCustosController {

	@Autowired
	private TipoCustoService service;

	@GetMapping("/novo")
	public ModelAndView novo(TipoCusto tipoCusto) {
		return new ModelAndView("tipocusto/CadastroTipoCusto");
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid TipoCusto tipoCusto, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(tipoCusto);
		}
		try {
			service.salvar(tipoCusto);
		} catch (JaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(tipoCusto);
		}
		attributes.addFlashAttribute("mensagem", "Tipo de custo salvo com sucesso!");
		return new ModelAndView("redirect:/tiposCusto/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(TipoCustoFilter filtro, BindingResult result,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("tipocusto/PesquisaTipoCustos");
		PageWrapper<TipoCusto> paginaWrapper = new PageWrapper<>(service.filtrar(filtro, pageable), request);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		TipoCusto tipoCusto = service.getOne(codigo);
		ModelAndView mv = novo(tipoCusto);
		mv.addObject(tipoCusto);
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
