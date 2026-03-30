package br.edu.ifrn.sinapiPRO.controller;
import java.time.LocalDate;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity; import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult; import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.MovimentoBancario;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService; import br.edu.ifrn.sinapiPRO.service.HistoricoBancarioService;
import br.edu.ifrn.sinapiPRO.service.MovimentoBancarioService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/movimentosBancarios")
public class MovimentosBancariosController {
	@Autowired private MovimentoBancarioService service;
	@Autowired private ContaBancariaService contaService;
	@Autowired private HistoricoBancarioService historicoService;
	@GetMapping public ModelAndView lista(@RequestParam(required=false) Long codigoConta,
			@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio,
			@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fim) {
		ModelAndView mv = new ModelAndView("movimentobancario/ListaMovimentos");
		mv.addObject("contas", contaService.findAll());
		if (codigoConta != null) {
			mv.addObject("movimentos", (inicio != null && fim != null)
				? service.findByContaEPeriodo(codigoConta, inicio, fim)
				: service.findByConta(codigoConta));
			mv.addObject("codigoConta", codigoConta);
			mv.addObject("contaSelecionada", contaService.getOne(codigoConta));
		}
		mv.addObject("inicio", inicio); mv.addObject("fim", fim);
		return mv; }
	@GetMapping("/novo") public ModelAndView novo(MovimentoBancario m) { return form(m); }
	@PostMapping("/novo") public ModelAndView salvar(@Valid MovimentoBancario m, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return form(m);
		service.salvar(m); a.addFlashAttribute("mensagem", "Movimento registrado!");
		return new ModelAndView("redirect:/movimentosBancarios?codigoConta=" + m.getContaBancaria().getCodigo()); }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
	private ModelAndView form(MovimentoBancario m) {
		ModelAndView mv = new ModelAndView("movimentobancario/FormMovimento");
		mv.addObject("movimentoBancario", m);
		mv.addObject("contas", contaService.findAtivas());
		mv.addObject("historicos", historicoService.findAll());
		return mv; }
}
