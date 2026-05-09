package com.sinapipro.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractCrudPageController;
import com.sinapipro.model.ComposicaoGrupo;
import com.sinapipro.repository.ComposicaoClassesRepository;
import com.sinapipro.repository.ComposicaoGruposRepository;
import com.sinapipro.repository.filter.ComposicaoGrupoFilter;
import com.sinapipro.service.ComposicaoGrupoService;

@Controller
@RequestMapping("/composicaoGrupos")
public class ComposicaoGruposController extends AbstractCrudPageController<ComposicaoGrupo, ComposicaoGrupoFilter> {

	private final ComposicaoClassesRepository composicaoClassesRepository;
	private final ComposicaoGruposRepository composicaoGruposRepository;

	public ComposicaoGruposController(
			ComposicaoGrupoService service,
			ComposicaoClassesRepository composicaoClassesRepository,
			ComposicaoGruposRepository composicaoGruposRepository) {
		super(service, "composicaoGrupo/CadastroComposicaoGrupo", "composicaoGrupo/PesquisaComposicaoGrupos", "/composicaoGrupos/nova", "Grupo salvo com sucesso!", "nome");
		this.composicaoClassesRepository = composicaoClassesRepository;
		this.composicaoGruposRepository = composicaoGruposRepository;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("composicaoClasses", composicaoClassesRepository.findAll());
	}

	@Override
	protected void adicionarObjetosPesquisa(ModelAndView mv, ComposicaoGrupoFilter filtro) {
		mv.addObject("ComposicaoClasses", composicaoClassesRepository.findAll());
	}

	@GetMapping("/nova")
	public ModelAndView nova(ComposicaoGrupo composicaoGrupo) {
		return abrirFormulario();
	}

	@Cacheable(value = "grupos", key = "#codigoComposicaoClasse")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ComposicaoGrupo> pesquisarPorCodigoComposicaoClasse(@RequestParam(name = "classe", defaultValue = "-1") Long codigoComposicaoClasse) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		return composicaoGruposRepository.findAllByComposicaoClasseCodigo(codigoComposicaoClasse);
	}

	@PostMapping({"/nova", "/novo", "/{codigo}"})
	@CacheEvict(value = "grupos", key = "#composicaoGrupo.composicaoClasse.codigo", condition = "#composicaoGrupo.temClasse()")
	public ModelAndView salvar(@Valid ComposicaoGrupo composicaoGrupo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(composicaoGrupo, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(ComposicaoGrupoFilter composicaoGrupoFilter, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(composicaoGrupoFilter, pageable, httpServletRequest);
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
