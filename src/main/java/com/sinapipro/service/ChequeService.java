package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Cheque;
import com.sinapipro.repository.ChequesRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class ChequeService extends AbstractSimpleCrudService<Cheque, ChequesRepository> {

    private final ChequesRepository repository;

    public ChequeService(ChequesRepository repository) {
        super(repository, "Impossível apagar o cheque.", "Cheque não encontrado.");
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cheque> findEmitidos() {
        return repository.findBySituacaoOrderByDataEmissaoDesc("EMITIDO");
    }
}
