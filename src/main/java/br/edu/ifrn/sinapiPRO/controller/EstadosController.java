package br.edu.ifrn.sinapiPRO.controller;

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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudPageController;
import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.filter.EstadoFilter;
import br.edu.ifrn.sinapiPRO.service.EstadoService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/estados")
public class EstadosController extends AbstractCrudPageController<Estado, EstadoFilter> {

	private final EstadoService service;

	public EstadosController(EstadoService service) {
		super(service, "estado/CadastroEstado", "estado/PesquisaEstados", "/estados/novo", "Estado salvo com sucesso", "nome");
		this.service = service;
	}

	@GetMapping("/novo")
	public ModelAndView novo(Estado estado) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid Estado estado, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(estado, result, attributes);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Estado estado, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
		}

		try {
			estado = service.salvar(estado);
		} catch (JaCadastradoException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}

		return ResponseEntity.ok(estado);
	}

	@GetMapping
	public ModelAndView pesquisar(EstadoFilter estadoFilter, @PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(estadoFilter, pageable, httpServletRequest);
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
