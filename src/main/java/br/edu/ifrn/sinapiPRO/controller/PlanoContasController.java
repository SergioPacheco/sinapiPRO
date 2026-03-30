package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.PlanoContas;
import br.edu.ifrn.sinapiPRO.service.PlanoContasService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/planoContas")
public class PlanoContasController {
	@Autowired
	private PlanoContasService service;
	@GetMapping
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView("planocontas/ListaPlanoContas");
		mv.addObject("contas", service.findAll());
		return mv;
	}

	
	@GetMapping("/novo")
	public ModelAndView novo(PlanoContas p) {
		ModelAndView mv = new ModelAndView("planocontas/CadastroPlanoContas");
		mv.addObject("pais", service.findAll());
		return mv;
	}

	
	@PostMapping({"/novo","{\\d+}"})
	public ModelAndView salvar(@Valid PlanoContas p, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) { ModelAndView mv = novo(p); mv.addObject(p); return mv;
	}
		service.salvar(p); a.addFlashAttribute("mensagem", "Conta salva!"); return new ModelAndView("redirect:/planoContas");
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
