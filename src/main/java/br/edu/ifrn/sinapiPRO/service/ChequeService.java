package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Cheque;
import br.edu.ifrn.sinapiPRO.repository.ChequesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ChequeService {

    @Autowired
    private ChequesRepository repository;

    @Transactional
    public Cheque salvar(Cheque cheque) {
        return repository.saveAndFlush(cheque);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar o cheque.");
        }
    }

    @Transactional(readOnly = true)
    public List<Cheque> findEmitidos() {
        return repository.findBySituacaoOrderByDataEmissaoDesc("EMITIDO");
    }

    public List<Cheque> findAll() {
        return repository.findAll();
    }

    public Cheque getOne(Long codigo) {
        return repository.getOne(codigo);
    }
}
