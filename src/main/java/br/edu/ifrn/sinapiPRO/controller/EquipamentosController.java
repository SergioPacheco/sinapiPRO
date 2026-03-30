package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Equipamento;
import br.edu.ifrn.sinapiPRO.service.CadastroEquipamentoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/equipamentos")
public class EquipamentosController {
	@Autowired
	private CadastroEquipamentoService service;
	
	@GetMapping("/novo")
	public ModelAndView novo(Equipamento e) {
		return new ModelAndView("equipamento/CadastroEquipamento");
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid Equipamento e, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(e);
		service.salvar(e); a.addFlashAttribute("mensagem", "Equipamento salvo!"); return new ModelAndView("redirect:/equipamentos/novo");
	}
	@GetMapping
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("equipamento/ListaEquipamentos");
		mv.addObject("equipamentos", service.findAll());
		return mv;
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
