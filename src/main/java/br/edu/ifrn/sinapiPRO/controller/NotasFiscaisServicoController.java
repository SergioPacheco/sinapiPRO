package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.NotaFiscalServico; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.NotaFiscalServicoService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/notasFiscaisServico")
public class NotasFiscaisServicoController {
    @Autowired private NotaFiscalServicoService service;
    @Autowired private ClientesRepository clienteRepository;
    @Autowired private ObrasRepository obraRepository;
    @GetMapping public ModelAndView lista() { ModelAndView mv = new ModelAndView("notafiscalservico/ListaNotasFiscais"); mv.addObject("notas", service.findAll()); return mv; }
    @GetMapping("/novo") public ModelAndView novo(NotaFiscalServico nf) { return form(nf); }
    @GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.getOne(codigo)); }
    @PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid NotaFiscalServico nf, BindingResult r, RedirectAttributes attrs) {
        if (r.hasErrors()) return form(nf);
        service.salvar(nf); attrs.addFlashAttribute("mensagem", "Nota fiscal salva!"); return new ModelAndView("redirect:/notasFiscaisServico"); }
    @DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        return ResponseEntity.ok().build(); }
    private ModelAndView form(NotaFiscalServico nf) {
        ModelAndView mv = new ModelAndView("notafiscalservico/FormNotaFiscal");
        mv.addObject("notaFiscalServico", nf); mv.addObject("clientes", clienteRepository.findAll()); mv.addObject("obras", obraRepository.findAll()); return mv; }
}
