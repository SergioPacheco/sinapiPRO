package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Atendimento; import br.edu.ifrn.sinapiPRO.repository.AtendimentosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
@Service public class AtendimentoService {
    @Autowired private AtendimentosRepository repository;
    @Transactional public Atendimento salvar(Atendimento a) { a.getOrdensServico().forEach(os -> os.setAtendimento(a)); return repository.saveAndFlush(a); }
    @Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar o atendimento."); } }
    @Transactional(readOnly = true) public List<Atendimento> findAbertos() { return repository.findBySituacaoOrderByDataAberturaDesc("ABERTO"); }
    public List<Atendimento> findAll() { return repository.findAll(); }
    public Atendimento getOne(Long c) { return repository.getOne(c); }
}
