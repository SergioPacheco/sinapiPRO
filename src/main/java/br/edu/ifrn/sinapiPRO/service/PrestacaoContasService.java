package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.PrestacaoContas;
import br.edu.ifrn.sinapiPRO.repository.PrestacaoContasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class PrestacaoContasService extends AbstractSimpleCrudService<PrestacaoContas, PrestacaoContasRepository> {

    private final PrestacaoContasRepository repository;

    public PrestacaoContasService(PrestacaoContasRepository repository) {
        super(repository, "Impossível apagar o lançamento.", "Lançamento não encontrado.");
        this.repository = repository;
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
