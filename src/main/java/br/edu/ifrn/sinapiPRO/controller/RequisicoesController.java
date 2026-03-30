package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Requisicao;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.service.RequisicaoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/requisicoes")
public class RequisicoesController {
	@Autowired
	private RequisicaoService service;
	@Autowired
	private ObrasRepository obraRepository;
	@Autowired
	private InsumosRepository insumoRepository;
	@GetMapping
	public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		ModelAndView mv = new ModelAndView("requisicao/ListaRequisicoes");
		mv.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) { mv.addObject("requisicoes", service.findByObra(codigoObra)); mv.addObject("codigoObra", codigoObra);
	}
		return mv;
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Requisicao r) {
		return form(r);
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return form(service.buscarComItens(codigo));
	}

	
	@PostMapping({"/novo","/{codigo}"})
	public ModelAndView salvar(@Valid Requisicao r, BindingResult br, RedirectAttributes a) {
		if (br.hasErrors()) return form(r);
		service.salvar(r); a.addFlashAttribute("mensagem", "Requisição salva!");
		return new ModelAndView("redirect:/requisicoes?codigoObra=" + r.getObra().getCodigo());
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
	private ModelAndView form(Requisicao r) {
		ModelAndView mv = new ModelAndView("requisicao/FormRequisicao");
		mv.addObject("requisicao", r);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("insumos", insumoRepository.findAll());
		return mv;
	}
}
