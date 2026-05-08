package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;

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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.DiarioClima;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioClimaService;

@Controller
@RequestMapping("/diarioClimas")
public class DiarioClimasController extends AbstractCrudListController<DiarioClima> {

	public DiarioClimasController(CadastroDiarioClimaService service) {
		super(
				service,
				"diarioclima/CadastroDiarioClima",
				"diarioclima/PesquisaDiarioClimas",
				"/diarioClimas/novo",
				"DiarioClima salvo(a)!",
				"nome",
				"lista");
	}

	@GetMapping("/novo")
	public ModelAndView novo(DiarioClima diarioClima) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid DiarioClima diarioClima, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(diarioClima, result, attributes);
	}

	@GetMapping
	public ModelAndView listar() {
		return processarListagem();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
