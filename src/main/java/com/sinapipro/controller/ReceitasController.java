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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractCrudListController;
import com.sinapipro.model.Receita;
import com.sinapipro.repository.ClientesRepository;
import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.service.ContaBancariaService;
import com.sinapipro.service.PlanoContasService;
import com.sinapipro.service.ReceitaService;

@Controller
@RequestMapping("/receitas")
public class ReceitasController extends AbstractCrudListController<Receita> {

	private final ObrasRepository obraRepository;
	private final ClientesRepository clienteRepository;
	private final PlanoContasService planoContasService;
	private final ContaBancariaService contaBancariaService;

	public ReceitasController(
			ReceitaService service,
			ObrasRepository obraRepository,
			ClientesRepository clienteRepository,
			PlanoContasService planoContasService,
			ContaBancariaService contaBancariaService) {
		super(service, "receita/FormReceita", "receita/ListaReceitas", "/receitas", "Receita salva!", "descricao", "receitas");
		this.obraRepository = obraRepository;
		this.clienteRepository = clienteRepository;
		this.planoContasService = planoContasService;
		this.contaBancariaService = contaBancariaService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("obras", obraRepository.findAll());
		modelAndView.addObject("clientes", clienteRepository.findAll());
		modelAndView.addObject("planoContas", planoContasService.findAll());
		modelAndView.addObject("contasBancarias", contaBancariaService.findAtivas());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(defaultValue="false") boolean todas) {
		ModelAndView mv = new ModelAndView("receita/ListaReceitas");
		mv.addObject("receitas", todas ? getService().findAll() : getService().findAbertas());
		mv.addObject("todas", todas);
		return mv;
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Receita receita) {
		return abrirFormulario();
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Receita receita, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(receita, result, attributes);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private ReceitaService getService() {
		return (ReceitaService) serviceRef();
	}

	@Override
	protected Receita buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComRecebimentos(codigo);
	}
}
