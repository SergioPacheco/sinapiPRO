package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.*; import br.edu.ifrn.sinapiPRO.service.FrotaService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/frota")
public class FrotaController {
    @Autowired private FrotaService service;
    @GetMapping public ModelAndView lista() { ModelAndView mv = new ModelAndView("veiculo/ListaVeiculos"); mv.addObject("veiculos", service.findVeiculos()); return mv; }
    @GetMapping("/novo") public ModelAndView novoVeiculo(Veiculo v) { return new ModelAndView("veiculo/FormVeiculo"); }
    @PostMapping({"/novo","/{codigo}"}) public ModelAndView salvarVeiculo(@Valid Veiculo v, BindingResult r, RedirectAttributes attrs) {
        if (r.hasErrors()) return novoVeiculo(v);
        service.salvarVeiculo(v); attrs.addFlashAttribute("mensagem", "Veículo salvo!"); return new ModelAndView("redirect:/frota"); }
    @GetMapping("/{codigo}") public ModelAndView editarVeiculo(@PathVariable Long codigo) { ModelAndView mv = novoVeiculo(service.getVeiculo(codigo)); mv.addObject(service.getVeiculo(codigo)); return mv; }
    @GetMapping("/{codigoVeiculo}/manutencao") public ModelAndView manutencoes(@PathVariable Long codigoVeiculo) {
        ModelAndView mv = new ModelAndView("veiculo/ListaManutencoes");
        mv.addObject("veiculo", service.getVeiculo(codigoVeiculo));
        mv.addObject("agendamentos", service.findAgendamentos(codigoVeiculo)); return mv; }
    @GetMapping("/{codigoVeiculo}/manutencao/novo") public ModelAndView novoAgendamento(@PathVariable Long codigoVeiculo, AgendamentoManutencao a) {
        if (a.getVeiculo() == null) { Veiculo v = new Veiculo(); v.setCodigo(codigoVeiculo); a.setVeiculo(v); }
        ModelAndView mv = new ModelAndView("veiculo/FormManutencao");
        mv.addObject("agendamentoManutencao", a); mv.addObject("veiculo", service.getVeiculo(codigoVeiculo)); return mv; }
    @PostMapping("/{codigoVeiculo}/manutencao") public ModelAndView salvarAgendamento(@PathVariable Long codigoVeiculo, @Valid AgendamentoManutencao a, BindingResult r, RedirectAttributes attrs) {
        if (r.hasErrors()) return novoAgendamento(codigoVeiculo, a);
        service.salvarAgendamento(a); attrs.addFlashAttribute("mensagem", "Agendamento salvo!"); return new ModelAndView("redirect:/frota/" + codigoVeiculo + "/manutencao"); }
    @DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try { service.excluirVeiculo(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        return ResponseEntity.ok().build(); }
}
