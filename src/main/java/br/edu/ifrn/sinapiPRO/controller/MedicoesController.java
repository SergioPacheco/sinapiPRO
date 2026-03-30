package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.model.Medicao;
import br.edu.ifrn.sinapiPRO.service.ContratoService;
import br.edu.ifrn.sinapiPRO.service.MedicaoContratoService;
import br.edu.ifrn.sinapiPRO.service.MedicaoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/medicoes")
public class MedicoesController {
	@Autowired
	private MedicaoService service;
	@Autowired
	private ContratoService contratoService;
	@Autowired
	private MedicaoContratoService medicaoContratoService;

	@GetMapping("/contrato/{codigoContrato}")
	public ModelAndView lista(@PathVariable Long codigoContrato) {
		Contrato contrato = contratoService.buscarComItens(codigoContrato);
		ModelAndView mv = new ModelAndView("medicao/ListaMedicoes");
		mv.addObject("contrato", contrato);
		mv.addObject("medicoes", service.findByContrato(codigoContrato));
		mv.addObject("percentualAcumulado", medicaoContratoService.calcularPercentualAcumulado(codigoContrato));
		mv.addObject("saldoDisponivel", medicaoContratoService.calcularSaldoDisponivel(codigoContrato));
		return mv;
	}
	
	@GetMapping("/novo/{codigoContrato}")
	public ModelAndView novo(@PathVariable Long codigoContrato) {
		Contrato contrato = contratoService.buscarComItens(codigoContrato);
		Medicao m = new Medicao(); m.setContrato(contrato);
		m.setNumero(service.findByContrato(codigoContrato).size() + 1);
		return form(m, contrato);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Medicao m = service.buscarComItens(codigo);
		return form(m, contratoService.buscarComItens(m.getContrato().getCodigo()));
	}
	
	@PostMapping({"/novo/{codigoContrato}","/{codigo}"})
	public ModelAndView salvar(
			@PathVariable(required=false) Long codigoContrato, @Valid Medicao m, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return form(m, contratoService.buscarComItens(m.getContrato().getCodigo()));
		service.salvar(m); a.addFlashAttribute("mensagem", "Medição salva!");
		return new ModelAndView("redirect:/medicoes/contrato/" + m.getContrato().getCodigo());
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { Medicao m = service.buscarComItens(codigo); service.excluir(codigo);
	}
		catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage());
	}
		return ResponseEntity.ok().build();
	}
	private ModelAndView form(Medicao m, Contrato contrato) {
		ModelAndView mv = new ModelAndView("medicao/FormMedicao");
		mv.addObject("medicao", m);
		mv.addObject("contrato", contrato);
		return mv;
	}

	@PostMapping("/{codigo}/aprovar")
	public ModelAndView aprovar(@PathVariable Long codigo,
			@RequestParam(required = false) java.math.BigDecimal percentualRetencao,
			org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) {
		try {
			br.edu.ifrn.sinapiPRO.model.Despesa despesa =
					medicaoContratoService.aprovarMedicao(codigo, percentualRetencao);
			attributes.addFlashAttribute("mensagem",
					"Medição aprovada! Despesa gerada: " + despesa.getDescricao()
					+ " — R$ " + String.format("%.2f", despesa.getValor()));
		} catch (RuntimeException e) {
			attributes.addFlashAttribute("erro", e.getMessage());
		}
		Medicao m = service.buscarComItens(codigo);
		return new ModelAndView("redirect:/medicoes/contrato/" + m.getContrato().getCodigo());
	}

	@PostMapping("/{codigo}/calcular")
	public ModelAndView calcular(@PathVariable Long codigo,
			org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) {
		Medicao m = service.buscarComItens(codigo);
		medicaoContratoService.calcularMedicao(m);
		attributes.addFlashAttribute("mensagem", "Valores calculados com sucesso!");
		return new ModelAndView("redirect:/medicoes/" + codigo);
	}
}
