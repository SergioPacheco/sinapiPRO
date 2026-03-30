package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Comissao;

@Repository
public interface ComissoesRepository extends JpaRepository<Comissao, Long> {
    List<Comissao> findByVendaCodigoOrderByNomeCorretorAsc(Long codigoVenda);
    List<Comissao> findBySituacaoOrderByDataPagamentoAsc(String situacao);
}
