package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.dto.BaselineComparativoDTO;
import br.edu.ifrn.sinapiPRO.model.OrcamentoBaseline;
import br.edu.ifrn.sinapiPRO.service.BaselineService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;

@Controller
@RequestMapping("/baseline")
public class BaselineController {

	private final BaselineService baselineService;
	private final OrcamentoService orcamentoService;

	public BaselineController(BaselineService baselineService, OrcamentoService orcamentoService) {
		this.baselineService = baselineService;
		this.orcamentoService = orcamentoService;
	}

	@GetMapping("/{codigoOrcamento}")
	public ModelAndView listar(@PathVariable Long codigoOrcamento) {
		ModelAndView mv = new ModelAndView("baseline/Baseline");
		mv.addObject("orcamento", orcamentoService.buscarComItens(codigoOrcamento));
		mv.addObject("baselines", baselineService.listarBaselines(codigoOrcamento));
		return mv;
	}

	@PostMapping("/{codigoOrcamento}")
	public ModelAndView gravar(@PathVariable Long codigoOrcamento,
							   @RequestParam String descricao,
							   RedirectAttributes attributes) {
		baselineService.gravarBaseline(codigoOrcamento, descricao);
		attributes.addFlashAttribute("mensagem", "Baseline gravado com sucesso!");
		return new ModelAndView("redirect:/baseline/" + codigoOrcamento);
	}

	@GetMapping("/{codigoOrcamento}/{codigoBaseline}")
	public ModelAndView comparativo(@PathVariable Long codigoOrcamento,
									@PathVariable Long codigoBaseline) {
		List<BaselineComparativoDTO> comparativo = baselineService.compararBaseline(codigoBaseline);
		ModelAndView mv = new ModelAndView("baseline/BaselineComparativo");
		mv.addObject("orcamento", orcamentoService.buscarComItens(codigoOrcamento));
		mv.addObject("comparativo", comparativo);
		mv.addObject("codigoBaseline", codigoBaseline);
		return mv;
	}
}
