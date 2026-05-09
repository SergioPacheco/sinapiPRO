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
import com.sinapipro.model.AgendamentoManutencao;
import com.sinapipro.model.Veiculo;
import com.sinapipro.service.AlertaManutencaoService;
import com.sinapipro.service.FrotaService;

@Controller
@RequestMapping("/frota")
public class FrotaController extends AbstractCrudListController<Veiculo> {

	private final FrotaService service;
	private final AlertaManutencaoService alertaService;

	public FrotaController(FrotaService service, AlertaManutencaoService alertaService) {
		super(service, "veiculo/FormVeiculo", "veiculo/ListaVeiculos", "/frota", "Veículo salvo!", "placa", "veiculos");
		this.service = service;
		this.alertaService = alertaService;
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novoVeiculo(Veiculo veiculo) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvarVeiculo(@Valid Veiculo veiculo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(veiculo, result, attributes);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editarVeiculo(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@GetMapping("/{codigoVeiculo}/manutencao")
	public ModelAndView manutencoes(@PathVariable Long codigoVeiculo) {
		ModelAndView mv = new ModelAndView("veiculo/ListaManutencoes");
		mv.addObject("veiculo", service.getVeiculo(codigoVeiculo));
		mv.addObject("agendamentos", service.findAgendamentos(codigoVeiculo));
		return mv;
	}

	@GetMapping("/{codigoVeiculo}/manutencao/novo")
	public ModelAndView novoAgendamento(@PathVariable Long codigoVeiculo, AgendamentoManutencao agendamento) {
		if (agendamento.getVeiculo() == null) {
			Veiculo veiculo = new Veiculo();
			veiculo.setCodigo(codigoVeiculo);
			agendamento.setVeiculo(veiculo);
		}
		ModelAndView mv = new ModelAndView("veiculo/FormManutencao");
		mv.addObject("agendamentoManutencao", agendamento);
		mv.addObject("veiculo", service.getVeiculo(codigoVeiculo));
		return mv;
	}

	@PostMapping("/{codigoVeiculo}/manutencao")
	public ModelAndView salvarAgendamento(
			@PathVariable Long codigoVeiculo,
			@Valid AgendamentoManutencao agendamento,
			BindingResult result,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novoAgendamento(codigoVeiculo, agendamento);
		}
		service.salvarAgendamento(agendamento);
		attributes.addFlashAttribute("mensagem", "Agendamento salvo!");
		return new ModelAndView("redirect:/frota/" + codigoVeiculo + "/manutencao");
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@GetMapping("/alertas")
	public ModelAndView alertas() {
		ModelAndView mv = new ModelAndView("veiculo/AlertasManutencao");
		mv.addObject("alertas", alertaService.gerarAlertas());
		return mv;
	}
}
