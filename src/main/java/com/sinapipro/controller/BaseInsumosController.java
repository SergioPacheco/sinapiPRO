package com.sinapipro.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractCrudPageController;
import com.sinapipro.model.BaseInsumo;
import com.sinapipro.repository.filter.BaseInsumoFilter;
import com.sinapipro.service.BaseInsumoService;
import com.sinapipro.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/baseInsumos")
public class BaseInsumosController extends AbstractCrudPageController<BaseInsumo, BaseInsumoFilter> {

	private final BaseInsumoService service;

	public BaseInsumosController(BaseInsumoService service) {
		super(service, "baseInsumo/CadastroBaseInsumo", "baseInsumo/PesquisaBaseInsumo", "/baseInsumos/nova", "Base de Insumo salva com sucesso!", "nome");
		this.service = service;
	}

	@GetMapping("/nova")
	public ModelAndView nova(BaseInsumo baseInsumo) {
		return abrirFormulario();
	}

	@PostMapping({"/nova", "/{codigo}"})
	public ModelAndView cadastrar(@Valid BaseInsumo baseInsumo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(baseInsumo, result, attributes);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid BaseInsumo baseInsumo, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
		}

		try {
			baseInsumo = service.salvar(baseInsumo);
		} catch (JaCadastradoException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}

		return ResponseEntity.ok(baseInsumo);
	}

	@GetMapping
	public ModelAndView pesquisar(BaseInsumoFilter baseInsumoFilter, @PageableDefault(size = 5) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(baseInsumoFilter, pageable, httpServletRequest);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
