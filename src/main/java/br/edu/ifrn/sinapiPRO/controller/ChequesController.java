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
import br.edu.ifrn.sinapiPRO.model.Cheque;
import br.edu.ifrn.sinapiPRO.service.ChequeService;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;

@Controller
@RequestMapping("/cheques")
public class ChequesController extends AbstractCrudListController<Cheque> {

	private final ContaBancariaService contaBancariaService;

	public ChequesController(ChequeService service, ContaBancariaService contaBancariaService) {
		super(service, "cheque/FormCheque", "cheque/ListaCheques", "/cheques", "Cheque salvo!", "numero", "cheques");
		this.contaBancariaService = contaBancariaService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("contas", contaBancariaService.findAtivas());
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(Cheque cheque) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Cheque cheque, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(cheque, result, attributes);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
