package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.DiarioClima;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioClimaService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/diarioClimas")
public class DiarioClimasController {
	@Autowired
	private CadastroDiarioClimaService service;
	
	@GetMapping("/novo")
	public ModelAndView novo(DiarioClima e) {
		return new ModelAndView("diarioclima/CadastroDiarioClima");
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid DiarioClima e, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(e);
		try {
			service.salvar(e);
		} catch (JaCadastradoException ex) {
			r.rejectValue("nome", ex.getMessage(), ex.getMessage());
			return novo(e);
		}
		a.addFlashAttribute("mensagem", "DiarioClima salvo(a)!"); return new ModelAndView("redirect:/diarioClimas/novo");
	}
	@GetMapping
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("diarioclima/PesquisaDiarioClimas");
		mv.addObject("lista", service.findAll()); return mv;
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
		} catch (ImpossivelExcluirEntidadeException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
