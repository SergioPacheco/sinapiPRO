package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.Competencia;
import br.edu.ifrn.sinapiPRO.service.CompetenciaService;
import br.edu.ifrn.sinapiPRO.service.EncerrarCompetenciaService;

@Controller
@RequestMapping("/competencias")
public class CompetenciasController extends AbstractCrudListController<Competencia> {

	private final CompetenciaService service;
	private final EncerrarCompetenciaService encerrarCompetenciaService;

	public CompetenciasController(CompetenciaService service, EncerrarCompetenciaService encerrarCompetenciaService) {
		super(service, "competencia/CadastroCompetencia", "competencia/ListaCompetencias", "/competencias", "Competência salva!", "mes", "competencias");
		this.service = service;
		this.encerrarCompetenciaService = encerrarCompetenciaService;
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(Competencia competencia) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Competencia competencia, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(competencia, result, attributes);
	}

	@PostMapping("/{codigo}/encerrar")
	public ModelAndView encerrar(@PathVariable Long codigo, RedirectAttributes attributes) {
		try {
			EncerrarCompetenciaService.RelatorioEncerramento rel = encerrarCompetenciaService.encerrarCompetencia(codigo);
			String msg = String.format(
					"Competência %s encerrada. %d funcionário(s) com saldo transferido para %s.",
					rel.getCompetencia(),
					rel.getFuncionariosTransferidos(),
					rel.getProximaCompetencia());
			if (rel.getAlertasCount() > 0) {
				msg += " ⚠️ " + rel.getAlertasCount() + " alerta(s) de saldo excessivo.";
			}
			attributes.addFlashAttribute("mensagem", msg);
		} catch (RuntimeException exception) {
			attributes.addFlashAttribute("erro", exception.getMessage());
		}
		return new ModelAndView("redirect:/competencias");
	}
}
