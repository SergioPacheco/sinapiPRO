package com.sinapipro.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractClienteOwnedCrudController;
import com.sinapipro.model.ClienteEndereco;
import com.sinapipro.service.CadastroClienteEnderecoService;

@Controller
@RequestMapping("/clientesEndereco")
public class ClientesEnderecoController extends AbstractClienteOwnedCrudController<ClienteEndereco> {

	public ClientesEnderecoController(CadastroClienteEnderecoService service) {
		super(
				service,
				service::findByCliente,
				ClienteEndereco::getCliente,
				ClienteEndereco::setCliente,
				"clienteendereco/CadastroClienteEndereco",
				"clienteendereco/PesquisaClientesEndereco",
				"enderecos",
				"/clientesEndereco",
				"Endereço salvo!");
	}

	@GetMapping("/novo/{codigoCliente}")
	public ModelAndView novo(@PathVariable Long codigoCliente, ClienteEndereco entidade) {
		return super.novo(codigoCliente, entidade);
	}

	@PostMapping({"/novo/{codigoCliente}", "/{codigo}"})
	public ModelAndView cadastrar(
			@PathVariable Long codigoCliente,
			@Valid ClienteEndereco entidade,
			BindingResult result,
			RedirectAttributes attributes) {
		return super.cadastrar(codigoCliente, entidade, result, attributes);
	}

	@GetMapping("/cliente/{codigoCliente}")
	public ModelAndView listar(@PathVariable Long codigoCliente) {
		return super.listar(codigoCliente);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return super.editar(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<Void> excluir(@PathVariable Long codigo) {
		return super.excluir(codigo);
	}
}
