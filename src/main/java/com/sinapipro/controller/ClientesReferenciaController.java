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
import com.sinapipro.model.ClienteReferencia;
import com.sinapipro.service.CadastroClienteReferenciaService;

@Controller
@RequestMapping("/clientesReferencia")
public class ClientesReferenciaController extends AbstractClienteOwnedCrudController<ClienteReferencia> {

	public ClientesReferenciaController(CadastroClienteReferenciaService service) {
		super(
				service,
				service::findByCliente,
				ClienteReferencia::getCliente,
				ClienteReferencia::setCliente,
				"clientereferencia/CadastroClienteReferencia",
				"clientereferencia/PesquisaClientesReferencia",
				"referencias",
				"/clientesReferencia",
				"Referência salva!");
	}

	@GetMapping("/novo/{codigoCliente}")
	public ModelAndView novo(@PathVariable Long codigoCliente, ClienteReferencia entidade) {
		return super.novo(codigoCliente, entidade);
	}

	@PostMapping({"/novo/{codigoCliente}", "/{codigo}"})
	public ModelAndView cadastrar(
			@PathVariable Long codigoCliente,
			@Valid ClienteReferencia entidade,
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
