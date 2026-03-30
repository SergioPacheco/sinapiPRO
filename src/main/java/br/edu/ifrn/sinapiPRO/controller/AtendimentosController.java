package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Atendimento; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.AtendimentoService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/atendimentos")
public class AtendimentosController {
    @Autowired private AtendimentoService service;
    @Autowired private ClientesRepository clienteRepository;
    @Autowired private ObrasRepository obraRepository;
    @GetMapping public ModelAndView lista() { ModelAndView mv = new ModelAndView("atendimento/ListaAtendimentos"); mv.addObject("atendimentos", service.findAll()); return mv; }
    @GetMapping("/novo") public ModelAndView novo(Atendimento a) { return form(a); }
    @GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.getOne(codigo)); }
    @PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid Atendimento a, BindingResult r, RedirectAttributes attrs) {
        if (r.hasErrors()) return form(a);
        service.salvar(a); attrs.addFlashAttribute("mensagem", "Atendimento salvo!"); return new ModelAndView("redirect:/atendimentos"); }
    @DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        return ResponseEntity.ok().build(); }
    private ModelAndView form(Atendimento a) {
        ModelAndView mv = new ModelAndView("atendimento/FormAtendimento");
        mv.addObject("atendimento", a); mv.addObject("clientes", clienteRepository.findAll()); mv.addObject("obras", obraRepository.findAll()); return mv; }

    @Autowired
    private br.edu.ifrn.sinapiPRO.service.AtendimentoSlaService slaService;

    @PostMapping("/{codigo}/encerrar")
    public ModelAndView encerrar(@PathVariable Long codigo,
            @RequestParam(required = false) String observacaoEncerramento,
            org.springframework.web.servlet.mvc.support.RedirectAttributes attrs) {
        try {
            slaService.encerrar(codigo, observacaoEncerramento);
            attrs.addFlashAttribute("mensagem", "Atendimento encerrado com sucesso!");
        } catch (RuntimeException e) {
            attrs.addFlashAttribute("erro", e.getMessage());
        }
        return new ModelAndView("redirect:/atendimentos");
    }

    @PostMapping("/processarEscalacoes")
    public ModelAndView processarEscalacoes(org.springframework.web.servlet.mvc.support.RedirectAttributes attrs) {
        int count = slaService.processarEscalacoes();
        attrs.addFlashAttribute("mensagem", count + " atendimento(s) escalado(s) por SLA vencido.");
        return new ModelAndView("redirect:/atendimentos");
    }

    @GetMapping("/emRisco")
    public ModelAndView emRisco() {
        ModelAndView mv = new ModelAndView("atendimento/ListaAtendimentos");
        mv.addObject("atendimentos", slaService.findAtendimentosEmRisco());
        mv.addObject("titulo", "Atendimentos em Risco de SLA");
        return mv;
    }
}
