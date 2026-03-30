package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Venda;

@Repository
public interface VendasRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByUnidadeObraCodigoOrderByDataVendaDesc(Long codigoObra);
    List<Venda> findByClienteCodigoOrderByDataVendaDesc(Long codigoCliente);
}
