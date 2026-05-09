package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.PrestacaoContas;
import com.sinapipro.repository.PrestacaoContasRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

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
