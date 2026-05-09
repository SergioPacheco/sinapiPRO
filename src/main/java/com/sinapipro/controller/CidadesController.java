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
import com.sinapipro.model.Cidade;
import com.sinapipro.repository.CidadesRepository;
import com.sinapipro.repository.EstadosRepository;
import com.sinapipro.repository.filter.CidadeFilter;
import com.sinapipro.service.CidadeService;

@Controller
@RequestMapping("/cidades")
public class CidadesController extends AbstractCrudPageController<Cidade, CidadeFilter> {

	private final CidadesRepository cidades;
	private final EstadosRepository estados;
	private final CidadeService service;

	public CidadesController(CidadesRepository cidades, EstadosRepository estados, CidadeService service) {
		super(service, "cidade/CadastroCidade", "cidade/PesquisaCidades", "/cidades/nova", "Cidade salva com sucesso!", "nome");
		this.cidades = cidades;
		this.estados = estados;
		this.service = service;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("estados", estados.findAll());
	}

	@Override
	protected void adicionarObjetosPesquisa(ModelAndView mv, CidadeFilter filtro) {
		mv.addObject("estados", estados.findAll());
	}

	@GetMapping("/nova")
	public ModelAndView nova(Cidade cidade) {
		return abrirFormulario();
	}

	@Cacheable(value = "cidades", key = "#codigoEstado")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(@RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return cidades.findByEstadoCodigo(codigoEstado);
	}

	@PostMapping({"/nova", "/{codigo}"})
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(cidade, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(cidadeFilter, pageable, httpServletRequest);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@Override
	protected Cidade buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarComEstado(codigo);
	}
}
