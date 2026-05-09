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
import com.sinapipro.model.Fornecedor;
import com.sinapipro.repository.filter.FornecedorFilter;
import com.sinapipro.service.EstadoService;
import com.sinapipro.service.FornecedorService;

@Controller
@RequestMapping("/fornecedores")
public class FornecedoresController extends AbstractCrudPageController<Fornecedor, FornecedorFilter> {

	private final EstadoService estadoService;

	public FornecedoresController(FornecedorService service, EstadoService estadoService) {
		super(service, "fornecedor/CadastroFornecedor", "fornecedor/PesquisaFornecedores", "/fornecedores/novo", "Fornecedor salvo com sucesso!", "nome");
		this.estadoService = estadoService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("estados", estadoService.findAll());
	}

	@GetMapping("/novo")
	public ModelAndView novo(Fornecedor fornecedor) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid Fornecedor fornecedor, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(fornecedor, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(
			FornecedorFilter filtro,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
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
