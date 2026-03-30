package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.*;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.*;
import br.edu.ifrn.sinapiPRO.service.exception.*;

@Controller
@RequestMapping("/diarioObra")
public class DiarioObraController {

	@Autowired
	private DiarioObraService service;
	@Autowired
	private ObrasRepository obraRepository;
	@Autowired
	private CadastroDiarioClimaService climaService;
	@Autowired
	private CadastroDiarioAreaService areaService;
	@Autowired
	private CadastroDiarioAcidenteService acidenteService;

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		ModelAndView mv = new ModelAndView("diarioobra/ListaDiarioObra");
		mv.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) {
			mv.addObject("diarios", service.findByObra(codigoObra));
			mv.addObject("codigoObra", codigoObra);
		}
		return mv;
	}

	@GetMapping("/novo")
	public ModelAndView novo(DiarioObra d) {
		return montarFormulario(d);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		DiarioObra d = service.buscarComItens(codigo);
		return montarFormulario(d);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid DiarioObra d, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return montarFormulario(d);
		service.salvar(d);
		a.addFlashAttribute("mensagem", "Diário salvo com sucesso!");
		return new ModelAndView("redirect:/diarioObra?codigoObra=" + d.getObra().getCodigo());
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo);
	}
		catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage());
	}
		return ResponseEntity.ok().build();
	}

	private ModelAndView montarFormulario(DiarioObra d) {
		ModelAndView mv = new ModelAndView("diarioobra/FormDiarioObra");
		mv.addObject("diarioObra", d);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("climas", climaService.findAll());
		mv.addObject("areas", areaService.findAll());
		mv.addObject("acidentes", acidenteService.findAll());
		return mv;
	}
}
