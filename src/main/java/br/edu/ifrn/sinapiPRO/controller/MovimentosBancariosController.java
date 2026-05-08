package br.edu.ifrn.sinapiPRO.controller;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.MovimentoBancario;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;
import br.edu.ifrn.sinapiPRO.service.HistoricoBancarioService;
import br.edu.ifrn.sinapiPRO.service.MovimentoBancarioService;

@Controller
@RequestMapping("/movimentosBancarios")
public class MovimentosBancariosController extends AbstractCrudListController<MovimentoBancario> {

	private final ContaBancariaService contaService;
	private final HistoricoBancarioService historicoService;

	public MovimentosBancariosController(
			MovimentoBancarioService service,
			ContaBancariaService contaService,
			HistoricoBancarioService historicoService) {
		super(service, "movimentobancario/FormMovimento", "movimentobancario/ListaMovimentos", "/movimentosBancarios", "Movimento registrado!", "descricao", "movimentos");
		this.contaService = contaService;
		this.historicoService = historicoService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("contas", contaService.findAtivas());
		modelAndView.addObject("historicos", historicoService.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required=false) Long codigoConta,
			@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio,
			@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fim) {
		ModelAndView mv = new ModelAndView("movimentobancario/ListaMovimentos");
		mv.addObject("contas", contaService.findAll());
		if (codigoConta != null) {
			mv.addObject("movimentos", (inicio != null && fim != null)
				? getService().findByContaEPeriodo(codigoConta, inicio, fim)
				: getService().findByConta(codigoConta));
			mv.addObject("codigoConta", codigoConta);
			mv.addObject("contaSelecionada", contaService.buscarPorCodigo(codigoConta));
		}
		mv.addObject("inicio", inicio);
		mv.addObject("fim", fim);
		return mv;
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(MovimentoBancario movimentoBancario) {
		return abrirFormulario();
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid MovimentoBancario movimentoBancario, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(
				movimentoBancario,
				result,
				attributes,
				"/movimentosBancarios?codigoConta=" + movimentoBancario.getContaBancaria().getCodigo());
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private MovimentoBancarioService getService() {
		return (MovimentoBancarioService) serviceRef();
	}
}
