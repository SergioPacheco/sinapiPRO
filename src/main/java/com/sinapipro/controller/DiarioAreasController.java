package com.sinapipro.controller;

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

import com.sinapipro.controller.support.AbstractCrudListController;
import com.sinapipro.model.DiarioArea;
import com.sinapipro.service.CadastroDiarioAreaService;

@Controller
@RequestMapping("/diarioAreas")
public class DiarioAreasController extends AbstractCrudListController<DiarioArea> {

	public DiarioAreasController(CadastroDiarioAreaService service) {
		super(
				service,
				"diarioarea/CadastroDiarioArea",
				"diarioarea/PesquisaDiarioAreas",
				"/diarioAreas/novo",
				"DiarioArea salvo(a)!",
				"nome",
				"lista");
	}

	@GetMapping("/novo")
	public ModelAndView novo(DiarioArea diarioArea) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid DiarioArea diarioArea, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(diarioArea, result, attributes);
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
