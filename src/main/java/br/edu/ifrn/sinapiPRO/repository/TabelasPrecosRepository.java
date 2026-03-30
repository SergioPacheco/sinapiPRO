package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.TabelaPreco;

@Repository
public interface TabelasPrecosRepository extends JpaRepository<TabelaPreco, Long> {
    List<TabelaPreco> findByObraCodigoAndAtivaTrue(Long codigoObra);
}
