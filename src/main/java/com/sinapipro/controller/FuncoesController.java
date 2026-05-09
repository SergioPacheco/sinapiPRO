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
import com.sinapipro.model.Funcao;
import com.sinapipro.repository.filter.FuncaoFilter;
import com.sinapipro.service.CadastroFuncaoService;

@Controller
@RequestMapping("/funcoes")
public class FuncoesController extends AbstractCrudPageController<Funcao, FuncaoFilter> {

	public FuncoesController(CadastroFuncaoService service) {
		super(service, "funcao/CadastroFuncao", "funcao/PesquisaFuncoes", "/funcoes/novo", "Funcao salvo(a) com sucesso!", "nome");
	}

	@GetMapping("/novo")
	public ModelAndView novo(Funcao funcao) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid Funcao funcao, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(funcao, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(FuncaoFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
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
