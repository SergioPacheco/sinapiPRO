package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Despesa; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.DespesaService; import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;
import br.edu.ifrn.sinapiPRO.service.PlanoContasService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/despesas")
public class DespesasController {
	@Autowired private DespesaService service;
	@Autowired private ObrasRepository obraRepository;
	@Autowired private FornecedoresRepository fornecedorRepository;
	@Autowired private PlanoContasService planoContasService;
	@Autowired private ContaBancariaService contaBancariaService;
	@GetMapping public ModelAndView lista(@RequestParam(defaultValue="false") boolean todas) {
		ModelAndView mv = new ModelAndView("despesa/ListaDespesas");
		mv.addObject("despesas", todas ? service.findAll() : service.findAbertas());
		mv.addObject("todas", todas); return mv; }
	@GetMapping("/novo") public ModelAndView novo(Despesa d) { return form(d); }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.buscarComPagamentos(codigo)); }
	@PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid Despesa d, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return form(d);
		service.salvar(d); a.addFlashAttribute("mensagem", "Despesa salva!"); return new ModelAndView("redirect:/despesas"); }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
	private ModelAndView form(Despesa d) {
		ModelAndView mv = new ModelAndView("despesa/FormDespesa");
		mv.addObject("despesa", d);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("fornecedores", fornecedorRepository.findAll());
		mv.addObject("planoContas", planoContasService.findAll());
		mv.addObject("contasBancarias", contaBancariaService.findAtivas());
		return mv; }
}
