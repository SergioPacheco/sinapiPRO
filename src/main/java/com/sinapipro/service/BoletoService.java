package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Boleto;
import com.sinapipro.repository.BoletosRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class BoletoService extends AbstractSimpleCrudService<Boleto, BoletosRepository> {

    private final BoletosRepository repository;

    public BoletoService(BoletosRepository repository) {
        super(repository, "Impossível apagar o boleto.", "Boleto não encontrado.");
        this.repository = repository;
    }

    @Transactional
    public void cancelar(Long codigo) {
        Boleto boleto = buscarPorCodigo(codigo);
        boleto.setSituacao("CANCELADO");
        repository.saveAndFlush(boleto);
    }

    @Transactional(readOnly = true)
    public List<Boleto> findEmitidos() {
        return repository.findBySituacaoOrderByDataVencimentoAsc("EMITIDO");
    }
}
