package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Boleto;
import br.edu.ifrn.sinapiPRO.repository.BoletosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class BoletoService {

    @Autowired
    private BoletosRepository repository;

    @Transactional
    public Boleto salvar(Boleto boleto) {
        return repository.saveAndFlush(boleto);
    }

    @Transactional
    public void cancelar(Long codigo) {
        Boleto boleto = repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado"));
        boleto.setSituacao("CANCELADO");
        repository.saveAndFlush(boleto);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar o boleto.");
        }
    }

    @Transactional(readOnly = true)
    public List<Boleto> findEmitidos() {
        return repository.findBySituacaoOrderByDataVencimentoAsc("EMITIDO");
    }

    public List<Boleto> findAll() {
        return repository.findAll();
    }

    public Boleto getOne(Long codigo) {
        return repository.getOne(codigo);
    }
}
