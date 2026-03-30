package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.ClienteReferencia; import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.service.CadastroClienteReferenciaService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/clientesReferencia")
public class ClientesReferenciaController {
	@Autowired private CadastroClienteReferenciaService service;
	@GetMapping("/novo/{codigoCliente}") public ModelAndView novo(@PathVariable Long codigoCliente, ClienteReferencia r) {
		if (r.getCliente() == null) { Cliente c = new Cliente(); c.setCodigo(codigoCliente); r.setCliente(c); }
		ModelAndView mv = new ModelAndView("clientereferencia/CadastroClienteReferencia");
		mv.addObject("codigoCliente", codigoCliente); return mv; }
	@PostMapping({"/novo/{codigoCliente}","{\\d+}"}) public ModelAndView cadastrar(@PathVariable Long codigoCliente, @Valid ClienteReferencia r, BindingResult br, RedirectAttributes a) {
		if (br.hasErrors()) return novo(codigoCliente, r);
		service.salvar(r); a.addFlashAttribute("mensagem", "Referência salva!"); return new ModelAndView("redirect:/clientesReferencia/cliente/" + codigoCliente); }
	@GetMapping("/cliente/{codigoCliente}") public ModelAndView listar(@PathVariable Long codigoCliente) {
		ModelAndView mv = new ModelAndView("clientereferencia/PesquisaClientesReferencia");
		mv.addObject("referencias", service.findByCliente(codigoCliente));
		mv.addObject("codigoCliente", codigoCliente); return mv; }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) {
		ClienteReferencia r = service.getOne(codigo);
		ModelAndView mv = novo(r.getCliente().getCodigo(), r); mv.addObject(r); return mv; }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
}
