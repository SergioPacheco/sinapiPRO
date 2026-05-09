package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.UnidadeVenda;
import com.sinapipro.repository.UnidadesVendaRepository;
import com.sinapipro.service.support.AbstractObraScopedCrudService;

@Service
public class UnidadeVendaService extends AbstractObraScopedCrudService<UnidadeVenda, UnidadesVendaRepository> {

    private final UnidadesVendaRepository repository;

    public UnidadeVendaService(UnidadesVendaRepository repository) {
        super(repository, "Impossível apagar. Unidade possui vendas vinculadas.", "Unidade não encontrada.");
        this.repository = repository;
    }

    @Override
    @Transactional
    public UnidadeVenda salvar(UnidadeVenda unidade) {
        unidade.getCaracteristicas().forEach(caracteristica -> caracteristica.setUnidade(unidade));
        return repository.saveAndFlush(unidade);
    }

    @Transactional(readOnly = true)
    public UnidadeVenda buscarComCaracteristicas(Long codigo) {
        return buscarPorCodigo(codigo);
    }
}
