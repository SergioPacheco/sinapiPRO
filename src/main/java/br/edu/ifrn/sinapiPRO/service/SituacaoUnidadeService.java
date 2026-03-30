package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.SituacaoUnidade;
import br.edu.ifrn.sinapiPRO.repository.SituacoesUnidadeRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class SituacaoUnidadeService {

    @Autowired
    private SituacoesUnidadeRepository repository;

    @Transactional
    public SituacaoUnidade salvar(SituacaoUnidade situacao) {
        return repository.saveAndFlush(situacao);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
        }
    }

    public List<SituacaoUnidade> findAll() {
        return repository.findAll();
    }

    public SituacaoUnidade getOne(Long codigo) {
        return repository.getOne(codigo);
    }
}
