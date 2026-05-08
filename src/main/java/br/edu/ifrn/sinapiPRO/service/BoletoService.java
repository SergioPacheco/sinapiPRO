package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Boleto;
import br.edu.ifrn.sinapiPRO.repository.BoletosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

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
