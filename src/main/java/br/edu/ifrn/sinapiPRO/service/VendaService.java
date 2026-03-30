package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.VendasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class VendaService {

    @Autowired
    private VendasRepository repository;

    @Transactional
    public Venda salvar(Venda venda) {
        venda.getParcelas().forEach(p -> p.setVenda(venda));
        return repository.saveAndFlush(venda);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar a venda.");
        }
    }

    @Transactional(readOnly = true)
    public List<Venda> findByObra(Long codigoObra) {
        return repository.findByUnidadeObraCodigoOrderByDataVendaDesc(codigoObra);
    }

    @Transactional(readOnly = true)
    public Venda buscarComParcelas(Long codigo) {
        return repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    public List<Venda> findAll() {
        return repository.findAll();
    }
}
