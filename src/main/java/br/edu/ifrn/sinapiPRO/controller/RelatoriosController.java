package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifrn.sinapiPRO.dto.ListaComposicoes;
import br.edu.ifrn.sinapiPRO.dto.ListaInsumos;
import br.edu.ifrn.sinapiPRO.dto.PeriodoRelatorio;
import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.BaseInsumoService;
import br.edu.ifrn.sinapiPRO.service.BasePrecoService;
import br.edu.ifrn.sinapiPRO.service.RelatorioService;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {
	
	@Autowired
	private RelatorioService relatorioService;
	
	@Autowired 
	private BaseInsumoService baseInsumoService; 
	
	@Autowired 
	private BasePrecoService basePrecoService; 
	
	@GetMapping("/listaComposicoes")
	public ModelAndView relatorioListaComposicoes() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioListaComposicoes");
		mv.addObject("basePrecos", basePrecoService.findAll());
		mv.addObject(new ListaComposicoes());
		return mv;
	}
	
	@PostMapping("/listaComposicoes")
	public ResponseEntity<byte[]> gerarRelatorioListaInsumos(ListaComposicoes listaComposicoes, 
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema) throws Exception {
		listaComposicoes.setNomeUsuario(usuarioSistema.getUsername());									 
		byte[] relatorio = relatorioService.gerarRelatorioListaComposicoes(listaComposicoes); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@PostMapping("/imprimirComposicao")
	public ResponseEntity<byte[]> gerarRelatorioImprimirComposicao(Long codigo, 
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema) throws Exception {
		byte[] relatorio = relatorioService
				.gerarRelatorioImprimirComposicao(codigo, usuarioSistema.getUsername()); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@PostMapping("/imprimirOrcamento")
	public ResponseEntity<byte[]> gerarRelatorioImprimirOrcamento(Long codigo, 
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema) throws Exception {
		byte[] relatorio = relatorioService
				.gerarRelatorioImprimirOrcamento(codigo, usuarioSistema.getUsername()); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	
	
	@GetMapping("/listaInsumos")
	public ModelAndView relatorioListagemInsumos() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioListaInsumos");
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("especies",   Especie.values());
		mv.addObject(new ListaInsumos());
		return mv;
	}
	
	@PostMapping("/listaInsumos")
	public ResponseEntity<byte[]> gerarRelatorioListagemInsumos(ListaInsumos listaInsumos) throws Exception {
		
		byte[] relatorio = relatorioService.gerarRelatorioListaInsumos(listaInsumos); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@GetMapping("/orcamentosEmitidos")
	public ModelAndView relatorioOrcamentosEmitidos() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioOrcamentosEmitidos");
		mv.addObject(new PeriodoRelatorio());
		return mv;
	}
	
	 
}












