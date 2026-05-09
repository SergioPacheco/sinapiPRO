package com.sinapipro.controller.support;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.model.Cliente;
import com.sinapipro.service.support.CrudListService;

public abstract class AbstractClienteOwnedCrudController<T> {

	private final CrudListService<T> service;
	private final Function<Long, java.util.List<T>> clienteFinder;
	private final Function<T, Cliente> clienteExtractor;
	private final BiConsumer<T, Cliente> clienteSetter;
	private final String formView;
	private final String listView;
	private final String listAttributeName;
	private final String redirectBasePath;
	private final String successMessage;

	protected AbstractClienteOwnedCrudController(
			CrudListService<T> service,
			Function<Long, java.util.List<T>> clienteFinder,
			Function<T, Cliente> clienteExtractor,
			BiConsumer<T, Cliente> clienteSetter,
			String formView,
			String listView,
			String listAttributeName,
			String redirectBasePath,
			String successMessage) {
		this.service = service;
		this.clienteFinder = clienteFinder;
		this.clienteExtractor = clienteExtractor;
		this.clienteSetter = clienteSetter;
		this.formView = formView;
		this.listView = listView;
		this.listAttributeName = listAttributeName;
		this.redirectBasePath = redirectBasePath;
		this.successMessage = successMessage;
	}

	protected ModelAndView novo(Long codigoCliente, T entidade) {
		garantirCliente(entidade, codigoCliente);
		return abrirFormulario(codigoCliente, entidade, false);
	}

	protected ModelAndView cadastrar(Long codigoCliente, @Valid T entidade, BindingResult result, RedirectAttributes attributes) {
		garantirCliente(entidade, codigoCliente);
		if (result.hasErrors()) {
			return abrirFormulario(codigoCliente, entidade, true);
		}
		service.salvar(entidade);
		attributes.addFlashAttribute("mensagem", successMessage);
		return new ModelAndView("redirect:" + redirectBasePath + "/cliente/" + codigoCliente);
	}

	protected ModelAndView listar(@PathVariable Long codigoCliente) {
		ModelAndView modelAndView = new ModelAndView(listView);
		modelAndView.addObject(listAttributeName, clienteFinder.apply(codigoCliente));
		modelAndView.addObject("codigoCliente", codigoCliente);
		return modelAndView;
	}

	protected ModelAndView editar(Long codigo) {
		T entidade = service.buscarPorCodigo(codigo);
		Long codigoCliente = clienteExtractor.apply(entidade).getCodigo();
		return abrirFormulario(codigoCliente, entidade, true);
	}

	protected ResponseEntity<Void> excluir(Long codigo) {
		service.excluir(codigo);
		return ResponseEntity.ok().build();
	}

	private ModelAndView abrirFormulario(Long codigoCliente, T entidade, boolean adicionarEntidade) {
		ModelAndView modelAndView = new ModelAndView(formView);
		modelAndView.addObject("codigoCliente", codigoCliente);
		if (adicionarEntidade) {
			modelAndView.addObject(entidade);
		}
		return modelAndView;
	}

	private void garantirCliente(T entidade, Long codigoCliente) {
		if (clienteExtractor.apply(entidade) == null) {
			Cliente cliente = new Cliente();
			cliente.setCodigo(codigoCliente);
			clienteSetter.accept(entidade, cliente);
		}
	}
}
