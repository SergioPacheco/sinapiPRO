package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.List;

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

import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.model.Despesa;
import br.edu.ifrn.sinapiPRO.model.Medicao;
import br.edu.ifrn.sinapiPRO.service.ContratoService;
import br.edu.ifrn.sinapiPRO.service.MedicaoContratoService;
import br.edu.ifrn.sinapiPRO.service.MedicaoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/medicoes")
public class MedicoesController {

	private final MedicaoService service;
	private final ContratoService contratoService;
	private final MedicaoContratoService medicaoContratoService;

	public MedicoesController(
			MedicaoService service,
			ContratoService contratoService,
			MedicaoContratoService medicaoContratoService) {
		this.service = service;
		this.contratoService = contratoService;
		this.medicaoContratoService = medicaoContratoService;
	}

	@GetMapping("/contrato/{codigoContrato}")
	public ModelAndView lista(@PathVariable Long codigoContrato) {
		Contrato contrato = buscarContrato(codigoContrato);
		List<Medicao> medicoes = service.findByContrato(codigoContrato);

		ModelAndView mv = new ModelAndView("medicao/ListaMedicoes");
		mv.addObject("contrato", contrato);
		mv.addObject("medicoes", medicoes);
		mv.addObject("percentualAcumulado", medicaoContratoService.calcularPercentualAcumulado(codigoContrato));
		mv.addObject("saldoDisponivel", medicaoContratoService.calcularSaldoDisponivel(codigoContrato));
		return mv;
	}

	@GetMapping("/novo/{codigoContrato}")
	public ModelAndView novo(@PathVariable Long codigoContrato) {
		Contrato contrato = buscarContrato(codigoContrato);
		Medicao medicao = new Medicao();
		medicao.setContrato(contrato);
		medicao.setNumero(service.findByContrato(codigoContrato).size() + 1);
		return form(medicao, contrato);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Medicao medicao = service.buscarComItens(codigo);
		return form(medicao, buscarContrato(medicao.getContrato().getCodigo()));
	}

	@PostMapping({"/novo/{codigoContrato}", "/{codigo}"})
	public ModelAndView salvar(
			@PathVariable(required = false) Long codigoContrato,
			@Valid Medicao medicao,
			BindingResult result,
			RedirectAttributes attributes) {
		Long contratoCodigo = resolveCodigoContrato(codigoContrato, medicao);
		if (result.hasErrors()) {
			return form(medicao, buscarContrato(contratoCodigo));
		}

		service.salvar(medicao);
		attributes.addFlashAttribute("mensagem", "Medição salva!");
		return redirectParaContrato(contratoCodigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try {
			service.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}
		return ResponseEntity.ok().build();
	}

	private ModelAndView form(Medicao medicao, Contrato contrato) {
		ModelAndView mv = new ModelAndView("medicao/FormMedicao");
		mv.addObject("medicao", medicao);
		mv.addObject("contrato", contrato);
		return mv;
	}

	@PostMapping("/{codigo}/aprovar")
	public ModelAndView aprovar(
			@PathVariable Long codigo,
			@RequestParam(required = false) BigDecimal percentualRetencao,
			RedirectAttributes attributes) {
		try {
			Despesa despesa = medicaoContratoService.aprovarMedicao(codigo, percentualRetencao);
			attributes.addFlashAttribute("mensagem",
					"Medição aprovada! Despesa gerada: " + despesa.getDescricao()
							+ " — R$ " + String.format("%.2f", despesa.getValor()));
		} catch (RuntimeException exception) {
			attributes.addFlashAttribute("erro", exception.getMessage());
		}
		return redirectParaContrato(service.buscarComItens(codigo).getContrato().getCodigo());
	}

	@PostMapping("/{codigo}/calcular")
	public ModelAndView calcular(@PathVariable Long codigo, RedirectAttributes attributes) {
		Medicao medicao = service.buscarComItens(codigo);
		medicaoContratoService.calcularMedicao(medicao);
		attributes.addFlashAttribute("mensagem", "Valores calculados com sucesso!");
		return new ModelAndView("redirect:/medicoes/" + codigo);
	}

	private Contrato buscarContrato(Long codigoContrato) {
		return contratoService.buscarComItens(codigoContrato);
	}

	private Long resolveCodigoContrato(Long codigoContrato, Medicao medicao) {
		if (medicao.getContrato() != null && medicao.getContrato().getCodigo() != null) {
			return medicao.getContrato().getCodigo();
		}
		return codigoContrato;
	}

	private ModelAndView redirectParaContrato(Long codigoContrato) {
		return new ModelAndView("redirect:/medicoes/contrato/" + codigoContrato);
	}
}
