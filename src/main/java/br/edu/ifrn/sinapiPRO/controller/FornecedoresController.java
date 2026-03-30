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
import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.repository.filter.FornecedorFilter;
import br.edu.ifrn.sinapiPRO.service.EstadoService;
import br.edu.ifrn.sinapiPRO.service.FornecedorService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/fornecedores")
public class FornecedoresController {

	@Autowired
	private FornecedorService service;

	@Autowired
	private EstadoService estadoService;

	@GetMapping("/novo")
	public ModelAndView novo(Fornecedor fornecedor) {
		ModelAndView mv = new ModelAndView("fornecedor/CadastroFornecedor");
		mv.addObject("estados", estadoService.findAll());
		return mv;
	}

	@PostMapping({ "/novo", "{\\d+}" })
	public ModelAndView cadastrar(@Valid Fornecedor fornecedor, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) return novo(fornecedor);
		try {
			service.salvar(fornecedor);
		} catch (JaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(fornecedor);
		}
		attributes.addFlashAttribute("mensagem", "Fornecedor salvo com sucesso!");
		return new ModelAndView("redirect:/fornecedores/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(FornecedorFilter filtro, BindingResult result,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("fornecedor/PesquisaFornecedores");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(filtro, pageable), request));
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Fornecedor f = service.getOne(codigo);
		ModelAndView mv = novo(f);
		mv.addObject(f);
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try { service.excluir(codigo);
	}
		catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage());
	}
		return ResponseEntity.ok().build();
	}
}
