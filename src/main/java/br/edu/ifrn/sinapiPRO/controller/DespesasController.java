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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.Despesa;
import br.edu.ifrn.sinapiPRO.repository.FornecedoresRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;
import br.edu.ifrn.sinapiPRO.service.DespesaService;
import br.edu.ifrn.sinapiPRO.service.PlanoContasService;

@Controller
@RequestMapping("/despesas")
public class DespesasController extends AbstractCrudListController<Despesa> {

	private final ObrasRepository obraRepository;
	private final FornecedoresRepository fornecedorRepository;
	private final PlanoContasService planoContasService;
	private final ContaBancariaService contaBancariaService;

	public DespesasController(
			DespesaService service,
			ObrasRepository obraRepository,
			FornecedoresRepository fornecedorRepository,
			PlanoContasService planoContasService,
			ContaBancariaService contaBancariaService) {
		super(service, "despesa/FormDespesa", "despesa/ListaDespesas", "/despesas", "Despesa salva!", "descricao", "despesas");
		this.obraRepository = obraRepository;
		this.fornecedorRepository = fornecedorRepository;
		this.planoContasService = planoContasService;
		this.contaBancariaService = contaBancariaService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("obras", obraRepository.findAll());
		modelAndView.addObject("fornecedores", fornecedorRepository.findAll());
		modelAndView.addObject("planoContas", planoContasService.findAll());
		modelAndView.addObject("contasBancarias", contaBancariaService.findAtivas());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(defaultValue="false") boolean todas) {
		ModelAndView mv = new ModelAndView("despesa/ListaDespesas");
		mv.addObject("despesas", todas ? getService().findAll() : getService().findAbertas());
		mv.addObject("todas", todas);
		return mv;
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Despesa despesa) {
		return abrirFormulario();
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Despesa despesa, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(despesa, result, attributes);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private DespesaService getService() {
		return (DespesaService) serviceRef();
	}

	@Override
	protected Despesa buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComPagamentos(codigo);
	}
}
