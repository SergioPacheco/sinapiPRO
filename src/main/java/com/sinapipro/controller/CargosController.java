package com.sinapipro.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.sinapipro.controller.support.AbstractCrudPageController;
import com.sinapipro.model.Cargo;
import com.sinapipro.repository.filter.CargoFilter;
import com.sinapipro.service.CadastroCargoService;

@Controller
@RequestMapping("/cargos")
public class CargosController extends AbstractCrudPageController<Cargo, CargoFilter> {

	public CargosController(CadastroCargoService service) {
		super(service, "cargo/CadastroCargo", "cargo/PesquisaCargos", "/cargos/novo", "Cargo salvo(a) com sucesso!", "nome");
	}

	@GetMapping("/novo")
	public ModelAndView novo(Cargo cargo) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid Cargo cargo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(cargo, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(CargoFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		return processarPesquisa(filtro, pageable, request);
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
