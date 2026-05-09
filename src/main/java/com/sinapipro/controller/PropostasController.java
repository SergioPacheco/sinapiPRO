package com.sinapipro.controller;

import javax.validation.Valid;

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

import com.sinapipro.controller.support.AbstractCrudListController;
import com.sinapipro.model.Proposta;
import com.sinapipro.repository.ClientesRepository;
import com.sinapipro.service.PropostaService;
import com.sinapipro.service.UnidadeVendaService;

@Controller
@RequestMapping("/propostas")
public class PropostasController extends AbstractCrudListController<Proposta> {

	private final UnidadeVendaService unidadeService;
	private final ClientesRepository clienteRepository;

	public PropostasController(
			PropostaService service,
			UnidadeVendaService unidadeService,
			ClientesRepository clienteRepository) {
		super(service, "proposta/FormProposta", "proposta/ListaPropostas", "/propostas", "Proposta salva!", "descricao", "propostas");
		this.unidadeService = unidadeService;
		this.clienteRepository = clienteRepository;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("unidades", unidadeService.findAll());
		modelAndView.addObject("clientes", clienteRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(Proposta proposta) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Proposta proposta, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(proposta, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
