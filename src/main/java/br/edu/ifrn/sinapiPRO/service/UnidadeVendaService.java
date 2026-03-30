package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.repository.UnidadesVendaRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class UnidadeVendaService {

    @Autowired
    private UnidadesVendaRepository repository;

    @Transactional
    public UnidadeVenda salvar(UnidadeVenda unidade) {
        unidade.getCaracteristicas().forEach(c -> c.setUnidade(unidade));
        return repository.saveAndFlush(unidade);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar. Unidade possui vendas vinculadas.");
        }
    }

    @Transactional(readOnly = true)
    public List<UnidadeVenda> findByObra(Long codigoObra) {
        return repository.findByObraCodigoOrderByIdentificacaoAsc(codigoObra);
    }

    @Transactional(readOnly = true)
    public UnidadeVenda buscarComCaracteristicas(Long codigo) {
        return repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));
    }

    public List<UnidadeVenda> findAll() {
        return repository.findAll();
    }
}
