package com.sinapipro.controller.support;

import java.util.List;
import java.util.function.Function;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.service.support.CrudListService;

public abstract class AbstractObraScopedCrudListController<T> extends AbstractCrudListController<T> {

	private final ObrasRepository obraRepository;
	private final Function<Long, List<T>> obraFinder;
	private final Function<T, Long> obraIdExtractor;

	protected AbstractObraScopedCrudListController(
			CrudListService<T> service,
			String formView,
			String listView,
			String redirectPath,
			String successMessage,
			String duplicateField,
			String listAttributeName,
			ObrasRepository obraRepository,
			Function<Long, List<T>> obraFinder,
			Function<T, Long> obraIdExtractor) {
		super(service, formView, listView, redirectPath, successMessage, duplicateField, listAttributeName);
		this.obraRepository = obraRepository;
		this.obraFinder = obraFinder;
		this.obraIdExtractor = obraIdExtractor;
	}

	@Override
	protected final void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("obras", obraRepository.findAll());
		adicionarObjetosFormularioEspecificos(modelAndView);
	}

	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
	}

	protected ModelAndView processarListagemPorObra(Long codigoObra) {
		ModelAndView modelAndView = criarListagem();
		modelAndView.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) {
			adicionarEntidadesListagem(modelAndView, obraFinder.apply(codigoObra));
			modelAndView.addObject("codigoObra", codigoObra);
		}
		adicionarObjetosListagemPorObra(modelAndView, codigoObra);
		return modelAndView;
	}

	protected void adicionarObjetosListagemPorObra(ModelAndView modelAndView, Long codigoObra) {
	}

	protected ModelAndView processarCadastroPorObra(T entidade, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(entidade, result, attributes, redirectPathRef() + "?codigoObra=" + obraIdExtractor.apply(entidade));
	}
}
