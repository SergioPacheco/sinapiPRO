package com.sinapipro.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractCrudPageController;
import com.sinapipro.model.Obra;
import com.sinapipro.repository.EstadosRepository;
import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.repository.filter.ObraFilter;
import com.sinapipro.service.ObraService;

@Controller
@RequestMapping("/obras")
public class ObrasController extends AbstractCrudPageController<Obra, ObraFilter> {

	private final EstadosRepository estados;
	private final ObraService service;
	private final ObrasRepository obrasRepository;

	public ObrasController(EstadosRepository estados, ObraService service, ObrasRepository obrasRepository) {
		super(service, "obra/CadastroObra", "obra/PesquisaObras", "/obras/nova", "Obra salva com sucesso!", "cei");
		this.estados = estados;
		this.service = service;
		this.obrasRepository = obrasRepository;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("estados", estados.findAll());
	}

	@GetMapping("/nova")
	public ModelAndView nova(Obra obra) {
		return abrirFormulario();
	}

	@PostMapping({"/nova", "/{codigo}"})
	public ModelAndView salvar(@Valid Obra obra, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(obra, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(ObraFilter obraFilter, @PageableDefault(size = 5) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(obraFilter, pageable, httpServletRequest);
	}

	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Obra> pesquisar(String nome) {
		validarTamanhoNome(nome);
		return obrasRepository.findByNomeStartingWithIgnoreCase(nome);
	}

	private void validarTamanhoNome(String nome) {
		if (StringUtils.isEmpty(nome) || nome.length() < 3) {
			throw new IllegalArgumentException();
		}
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
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
	protected Obra buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarComCidadeEstado(codigo);
	}
}
