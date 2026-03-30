package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Comissao;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.ComissoesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ComissaoService {

    @Autowired
    private ComissoesRepository repository;

    @Transactional
    public Comissao salvar(Comissao comissao) {
        if (comissao.getPercentual() != null && comissao.getVenda() != null) {
            Venda venda = comissao.getVenda();
            BigDecimal valorCalculado = venda.getValorVenda()
                    .multiply(comissao.getPercentual())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            comissao.setValor(valorCalculado);
        }
        return repository.saveAndFlush(comissao);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar a comissão.");
        }
    }

    @Transactional(readOnly = true)
    public List<Comissao> findByVenda(Long codigoVenda) {
        return repository.findByVendaCodigoOrderByNomeCorretorAsc(codigoVenda);
    }

    @Transactional(readOnly = true)
    public List<Comissao> findPendentes() {
        return repository.findBySituacaoOrderByDataPagamentoAsc("PENDENTE");
    }

    public List<Comissao> findAll() {
        return repository.findAll();
    }

    public Comissao getOne(Long codigo) {
        return repository.getOne(codigo);
    }
}
