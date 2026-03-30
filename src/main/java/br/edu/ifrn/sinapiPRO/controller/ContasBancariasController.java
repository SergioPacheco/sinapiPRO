package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.ContaBancaria;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/contasBancarias")
public class ContasBancariasController {
	@Autowired
	private ContaBancariaService service;
	@GetMapping
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView("contabancaria/ListaContasBancarias");
		mv.addObject("contas", service.findAll());
		return mv;
	}

	
	@GetMapping("/novo")
	public ModelAndView novo(ContaBancaria c) {
		return new ModelAndView("contabancaria/CadastroContaBancaria");
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid ContaBancaria c, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(c);
		service.salvar(c); a.addFlashAttribute("mensagem", "Conta bancária salva!"); return new ModelAndView("redirect:/contasBancarias");
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
