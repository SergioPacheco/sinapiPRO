package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.HistoricoBancario;
import br.edu.ifrn.sinapiPRO.service.HistoricoBancarioService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/historicosBancarios")
public class HistoricosBancariosController {
	@Autowired
	private HistoricoBancarioService service;
	@GetMapping
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView("historicobancario/ListaHistoricosBancarios");
		mv.addObject("historicos", service.findAll());
		return mv;
	}

	
	@GetMapping("/novo")
	public ModelAndView novo(HistoricoBancario h) {
		return new ModelAndView("historicobancario/CadastroHistoricoBancario");
	}

	
	@PostMapping({"/novo","{\\d+}"})
	public ModelAndView salvar(@Valid HistoricoBancario h, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(h);
		service.salvar(h); a.addFlashAttribute("mensagem", "Histórico salvo!"); return new ModelAndView("redirect:/historicosBancarios");
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		ModelAndView mv = novo(service.getOne(codigo));
		mv.addObject(service.getOne(codigo));
		return mv;
	}

	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try {
			service.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
