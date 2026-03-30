package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.PrestacaoContas;
import br.edu.ifrn.sinapiPRO.repository.PrestacaoContasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class PrestacaoContasService {

    @Autowired
    private PrestacaoContasRepository repository;

    @Transactional
    public PrestacaoContas salvar(PrestacaoContas prestacao) {
        return repository.saveAndFlush(prestacao);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar o lançamento.");
        }
    }

    @Transactional(readOnly = true)
    public List<PrestacaoContas> findByFuncionarioECompetencia(Long codigoFuncionario, Long codigoCompetencia) {
        return repository.findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataLancamentoAsc(
                codigoFuncionario, codigoCompetencia);
    }

    @Transactional(readOnly = true)
    public List<PrestacaoContas> findPendentes() {
        return repository.findBySituacaoOrderByDataLancamentoAsc("PENDENTE");
    }
}
