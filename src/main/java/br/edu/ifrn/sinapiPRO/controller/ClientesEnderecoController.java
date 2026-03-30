package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.ClienteEndereco;
import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.service.CadastroClienteEnderecoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/clientesEndereco")
public class ClientesEnderecoController {
	@Autowired
	private CadastroClienteEnderecoService service;
	
	@GetMapping("/novo/{codigoCliente}")
	public ModelAndView novo(@PathVariable Long codigoCliente, ClienteEndereco e) {
		if (e.getCliente() == null) { Cliente c = new Cliente(); c.setCodigo(codigoCliente); e.setCliente(c);
	}
		ModelAndView mv = new ModelAndView("clienteendereco/CadastroClienteEndereco");
		mv.addObject("codigoCliente", codigoCliente); return mv;
	}
	
	@PostMapping({"/novo/{codigoCliente}", "/{codigo}"})
	public ModelAndView cadastrar(@PathVariable Long codigoCliente, @Valid ClienteEndereco e, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(codigoCliente, e);
		service.salvar(e); a.addFlashAttribute("mensagem", "Endereço salvo!"); return new ModelAndView("redirect:/clientesEndereco/cliente/" + codigoCliente);
	}
	
	@GetMapping("/cliente/{codigoCliente}")
	public ModelAndView listar(@PathVariable Long codigoCliente) {
		ModelAndView mv = new ModelAndView("clienteendereco/PesquisaClientesEndereco");
		mv.addObject("enderecos", service.findByCliente(codigoCliente));
		mv.addObject("codigoCliente", codigoCliente); return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		ClienteEndereco e = service.getOne(codigo);
		ModelAndView mv = novo(e.getCliente().getCodigo(), e); mv.addObject(e); return mv;
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
