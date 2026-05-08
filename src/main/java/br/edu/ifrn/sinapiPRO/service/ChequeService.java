package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Cheque;
import br.edu.ifrn.sinapiPRO.repository.ChequesRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

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
