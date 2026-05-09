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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractCrudListController;
import com.sinapipro.model.Atendimento;
import com.sinapipro.repository.ClientesRepository;
import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.service.AtendimentoService;
import com.sinapipro.service.AtendimentoSlaService;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentosController extends AbstractCrudListController<Atendimento> {

	private final AtendimentoSlaService slaService;
	private final ClientesRepository clienteRepository;
	private final ObrasRepository obraRepository;

	public AtendimentosController(
			AtendimentoService service,
			ClientesRepository clienteRepository,
			ObrasRepository obraRepository,
			AtendimentoSlaService slaService) {
		super(service, "atendimento/FormAtendimento", "atendimento/ListaAtendimentos", "/atendimentos", "Atendimento salvo!", "descricao", "atendimentos");
		this.clienteRepository = clienteRepository;
		this.obraRepository = obraRepository;
		this.slaService = slaService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("clientes", clienteRepository.findAll());
		modelAndView.addObject("obras", obraRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(Atendimento atendimento) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Atendimento atendimento, BindingResult result, RedirectAttributes attrs) {
		return processarCadastro(atendimento, result, attrs);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@PostMapping("/{codigo}/encerrar")
	public ModelAndView encerrar(
			@PathVariable Long codigo,
			@RequestParam(required = false) String observacaoEncerramento,
			RedirectAttributes attrs) {
		try {
			slaService.encerrar(codigo, observacaoEncerramento);
			attrs.addFlashAttribute("mensagem", "Atendimento encerrado com sucesso!");
		} catch (RuntimeException e) {
			attrs.addFlashAttribute("erro", e.getMessage());
		}
		return new ModelAndView("redirect:/atendimentos");
	}

	@PostMapping("/processarEscalacoes")
	public ModelAndView processarEscalacoes(RedirectAttributes attrs) {
		int count = slaService.processarEscalacoes();
		attrs.addFlashAttribute("mensagem", count + " atendimento(s) escalado(s) por SLA vencido.");
		return new ModelAndView("redirect:/atendimentos");
	}

	@GetMapping("/emRisco")
	public ModelAndView emRisco() {
		ModelAndView mv = new ModelAndView("atendimento/ListaAtendimentos");
		mv.addObject("atendimentos", slaService.findAtendimentosEmRisco());
		mv.addObject("titulo", "Atendimentos em Risco de SLA");
		return mv;
	}
}
