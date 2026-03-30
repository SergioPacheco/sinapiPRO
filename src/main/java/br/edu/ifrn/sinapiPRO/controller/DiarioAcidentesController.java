package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.DiarioAcidente;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioAcidenteService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/diarioAcidentes")
public class DiarioAcidentesController {
	@Autowired
	private CadastroDiarioAcidenteService service;
	
	@GetMapping("/novo")
	public ModelAndView novo(DiarioAcidente e) {
		return new ModelAndView("diaroacidente/CadastroDiarioAcidente");
	}

	
	@PostMapping({"/novo","{\\d+}"})
	public ModelAndView cadastrar(@Valid DiarioAcidente e, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(e);
		try {
			service.salvar(e);
		} catch (JaCadastradoException ex) {
			r.rejectValue("nome", ex.getMessage(), ex.getMessage());
			return novo(e);
		}
		a.addFlashAttribute("mensagem", "DiarioAcidente salvo(a)!"); return new ModelAndView("redirect:/diarioAcidentes/novo");
	}
	@GetMapping
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("diaroacidente/PesquisaDiarioAcidentes");
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
