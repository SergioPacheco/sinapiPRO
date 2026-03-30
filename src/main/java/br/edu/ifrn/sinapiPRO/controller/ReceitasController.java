package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Receita; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.ReceitaService; import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;
import br.edu.ifrn.sinapiPRO.service.PlanoContasService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/receitas")
public class ReceitasController {
	@Autowired private ReceitaService service;
	@Autowired private ObrasRepository obraRepository;
	@Autowired private ClientesRepository clienteRepository;
	@Autowired private PlanoContasService planoContasService;
	@Autowired private ContaBancariaService contaBancariaService;
	@GetMapping public ModelAndView lista(@RequestParam(defaultValue="false") boolean todas) {
		ModelAndView mv = new ModelAndView("receita/ListaReceitas");
		mv.addObject("receitas", todas ? service.findAll() : service.findAbertas());
		mv.addObject("todas", todas); return mv; }
	@GetMapping("/novo") public ModelAndView novo(Receita r) { return form(r); }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.buscarComRecebimentos(codigo)); }
	@PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid Receita r, BindingResult br, RedirectAttributes a) {
		if (br.hasErrors()) return form(r);
		service.salvar(r); a.addFlashAttribute("mensagem", "Receita salva!"); return new ModelAndView("redirect:/receitas"); }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
	private ModelAndView form(Receita r) {
		ModelAndView mv = new ModelAndView("receita/FormReceita");
		mv.addObject("receita", r);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("clientes", clienteRepository.findAll());
		mv.addObject("planoContas", planoContasService.findAll());
		mv.addObject("contasBancarias", contaBancariaService.findAtivas());
		return mv; }
}
