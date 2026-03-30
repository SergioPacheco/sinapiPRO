package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Proposta;
import br.edu.ifrn.sinapiPRO.repository.PropostasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class PropostaService {

    @Autowired
    private PropostasRepository repository;

    @Transactional
    public Proposta salvar(Proposta proposta) {
        return repository.saveAndFlush(proposta);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar a proposta.");
        }
    }

    @Transactional(readOnly = true)
    public List<Proposta> findPendentes() {
        return repository.findBySituacaoOrderByDataPropostaDesc("PENDENTE");
    }

    public List<Proposta> findAll() {
        return repository.findAll();
    }

    public Proposta getOne(Long codigo) {
        return repository.getOne(codigo);
    }
}
