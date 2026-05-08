package br.edu.ifrn.sinapiPRO.controller;

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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.NotaFiscalServico;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.NotaFiscalServicoService;

@Controller
@RequestMapping("/notasFiscaisServico")
public class NotasFiscaisServicoController extends AbstractCrudListController<NotaFiscalServico> {

	private final ClientesRepository clienteRepository;
	private final ObrasRepository obraRepository;

	public NotasFiscaisServicoController(
			NotaFiscalServicoService service,
			ClientesRepository clienteRepository,
			ObrasRepository obraRepository) {
		super(service, "notafiscalservico/FormNotaFiscal", "notafiscalservico/ListaNotasFiscais", "/notasFiscaisServico", "Nota fiscal salva!", "numero", "notas");
		this.clienteRepository = clienteRepository;
		this.obraRepository = obraRepository;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("clientes", clienteRepository.findAll());
		modelAndView.addObject("obras", obraRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(NotaFiscalServico notaFiscalServico) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid NotaFiscalServico notaFiscalServico, BindingResult result, RedirectAttributes attrs) {
		return processarCadastro(notaFiscalServico, result, attrs);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
