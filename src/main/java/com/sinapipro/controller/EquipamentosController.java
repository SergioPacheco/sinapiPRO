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
import com.sinapipro.model.Equipamento;
import com.sinapipro.service.CadastroEquipamentoService;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentosController extends AbstractCrudListController<Equipamento> {

	public EquipamentosController(CadastroEquipamentoService service) {
		super(
				service,
				"equipamento/CadastroEquipamento",
				"equipamento/ListaEquipamentos",
				"/equipamentos/novo",
				"Equipamento salvo!",
				"nome",
				"equipamentos");
	}

	@GetMapping("/novo")
	public ModelAndView novo(Equipamento equipamento) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid Equipamento equipamento, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(equipamento, result, attributes);
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
