package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.AgendamentoManutencao; import br.edu.ifrn.sinapiPRO.model.Veiculo;
import br.edu.ifrn.sinapiPRO.repository.AgendamentosManutencaoRepository; import br.edu.ifrn.sinapiPRO.repository.VeiculosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
@Service public class FrotaService {
    @Autowired private VeiculosRepository veiculosRepository;
    @Autowired private AgendamentosManutencaoRepository agendamentosRepository;
    @Transactional public Veiculo salvarVeiculo(Veiculo v) { return veiculosRepository.saveAndFlush(v); }
    @Transactional public AgendamentoManutencao salvarAgendamento(AgendamentoManutencao a) { return agendamentosRepository.saveAndFlush(a); }
    @Transactional public void excluirVeiculo(Long c) { try { veiculosRepository.deleteById(c); veiculosRepository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar o veículo."); } }
    public List<Veiculo> findVeiculos() { return veiculosRepository.findAll(); }
    public List<AgendamentoManutencao> findAgendamentos(Long codigoVeiculo) { return agendamentosRepository.findByVeiculoCodigoOrderByDataAgendamentoDesc(codigoVeiculo); }
    public Veiculo getVeiculo(Long c) { return veiculosRepository.getOne(c); }
    public AgendamentoManutencao getAgendamento(Long c) { return agendamentosRepository.getOne(c); }
}
