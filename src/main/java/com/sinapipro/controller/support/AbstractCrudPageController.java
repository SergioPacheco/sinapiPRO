package com.sinapipro.controller.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.page.PageWrapper;
import com.sinapipro.service.exception.JaCadastradoException;
import com.sinapipro.service.support.CrudPageService;

public abstract class AbstractCrudPageController<T, F> {

	private final CrudPageService<T, F> service;
	private final String formView;
	private final String listView;
	private final String redirectPath;
	private final String successMessage;
	private final String duplicateField;

	protected AbstractCrudPageController(
			CrudPageService<T, F> service,
			String formView,
			String listView,
			String redirectPath,
			String successMessage,
			String duplicateField) {
		this.service = service;
		this.formView = formView;
		this.listView = listView;
		this.redirectPath = redirectPath;
		this.successMessage = successMessage;
		this.duplicateField = duplicateField;
	}

	protected ModelAndView abrirFormulario() {
		return criarFormulario();
	}

	protected ModelAndView abrirFormulario(T entidade) {
		ModelAndView modelAndView = abrirFormulario();
		modelAndView.addObject(entidade);
		return modelAndView;
	}

	protected ModelAndView processarCadastro(T entidade, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return abrirFormulario(entidade);
		}

		try {
			service.salvar(entidade);
		} catch (JaCadastradoException exception) {
			result.rejectValue(duplicateField, exception.getMessage(), exception.getMessage());
			return abrirFormulario(entidade);
		}

		attributes.addFlashAttribute("mensagem", successMessage);
		return new ModelAndView("redirect:" + redirectPath);
	}

	protected ModelAndView processarPesquisa(F filtro, Pageable pageable, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView(listView);
		modelAndView.addObject("pagina", new PageWrapper<>(service.filtrar(filtro, pageable), request));
		adicionarObjetosPesquisa(modelAndView, filtro);
		return modelAndView;
	}

	protected ModelAndView carregarEdicao(Long codigo) {
		return abrirFormulario(buscarEntidadeParaEdicao(codigo));
	}

	protected ResponseEntity<Void> excluirPorCodigo(Long codigo) {
		service.excluir(codigo);
		return ResponseEntity.ok().build();
	}

	protected CrudPageService<T, F> serviceRef() {
		return service;
	}

	protected T buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarPorCodigo(codigo);
	}

	protected void adicionarObjetosPesquisa(ModelAndView modelAndView, F filtro) {
	}

	private ModelAndView criarFormulario() {
		ModelAndView modelAndView = new ModelAndView(formView);
		adicionarObjetosFormulario(modelAndView);
		return modelAndView;
	}

	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
	}
}
