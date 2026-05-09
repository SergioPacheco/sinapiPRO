package com.sinapipro.controller.support;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.service.exception.JaCadastradoException;
import com.sinapipro.service.support.CrudListService;

public abstract class AbstractCrudListController<T> {

	private final CrudListService<T> service;
	private final String formView;
	private final String listView;
	private final String redirectPath;
	private final String successMessage;
	private final String duplicateField;
	private final String listAttributeName;

	protected AbstractCrudListController(
			CrudListService<T> service,
			String formView,
			String listView,
			String redirectPath,
			String successMessage,
			String duplicateField,
			String listAttributeName) {
		this.service = service;
		this.formView = formView;
		this.listView = listView;
		this.redirectPath = redirectPath;
		this.successMessage = successMessage;
		this.duplicateField = duplicateField;
		this.listAttributeName = listAttributeName;
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
		return processarCadastro(entidade, result, attributes, redirectPath);
	}

	protected ModelAndView processarCadastro(T entidade, BindingResult result, RedirectAttributes attributes, String redirectPath) {
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

	protected ModelAndView processarListagem() {
		ModelAndView modelAndView = criarListagem();
		adicionarEntidadesListagem(modelAndView, service.findAll());
		return modelAndView;
	}

	protected ModelAndView carregarEdicao(Long codigo) {
		return abrirFormulario(buscarEntidadeParaEdicao(codigo));
	}

	protected ResponseEntity<Void> excluirPorCodigo(Long codigo) {
		service.excluir(codigo);
		return ResponseEntity.ok().build();
	}

	protected CrudListService<T> serviceRef() {
		return service;
	}

	protected String redirectPathRef() {
		return redirectPath;
	}

	protected T buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarPorCodigo(codigo);
	}

	protected ModelAndView criarListagem() {
		ModelAndView modelAndView = new ModelAndView(listView);
		adicionarObjetosListagem(modelAndView);
		return modelAndView;
	}

	protected void adicionarEntidadesListagem(ModelAndView modelAndView, List<T> entidades) {
		modelAndView.addObject(listAttributeName, entidades);
	}

	protected void adicionarObjetosListagem(ModelAndView modelAndView) {
	}

	private ModelAndView criarFormulario() {
		ModelAndView modelAndView = new ModelAndView(formView);
		adicionarObjetosFormulario(modelAndView);
		return modelAndView;
	}

	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
	}
}
