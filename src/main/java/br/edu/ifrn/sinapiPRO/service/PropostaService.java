package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Proposta;
import br.edu.ifrn.sinapiPRO.repository.PropostasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class PropostaService extends AbstractSimpleCrudService<Proposta, PropostasRepository> {

    private final PropostasRepository repository;

    public PropostaService(PropostasRepository repository) {
        super(repository, "Impossível apagar a proposta.", "Proposta não encontrada.");
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Proposta> findPendentes() {
        return repository.findBySituacaoOrderByDataPropostaDesc("PENDENTE");
    }
}
