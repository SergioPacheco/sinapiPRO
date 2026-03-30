package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.service.ContratoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/contratos")
public class ContratosController {
	@Autowired
	private ContratoService service;
	@Autowired
	private ObrasRepository obraRepository;
	@Autowired
	private ClientesRepository clienteRepository;
	@GetMapping
	public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		ModelAndView mv = new ModelAndView("contrato/ListaContratos");
		mv.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) { mv.addObject("contratos", service.findByObra(codigoObra)); mv.addObject("codigoObra", codigoObra);
	}
		return mv;
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Contrato c) {
		return form(c);
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return form(service.buscarComItens(codigo));
	}

	
	@PostMapping({"/novo","/{codigo}"})
	public ModelAndView salvar(@Valid Contrato c, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return form(c);
		service.salvar(c); a.addFlashAttribute("mensagem", "Contrato salvo!");
		return new ModelAndView("redirect:/contratos?codigoObra=" + c.getObra().getCodigo());
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
	private ModelAndView form(Contrato c) {
		ModelAndView mv = new ModelAndView("contrato/FormContrato");
		mv.addObject("contrato", c);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("clientes", clienteRepository.findAll());
		return mv;
	}
}
