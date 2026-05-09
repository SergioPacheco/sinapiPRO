package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Proposta;
import com.sinapipro.repository.PropostasRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

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
