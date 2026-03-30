package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Boleto;

@Repository
public interface BoletosRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findBySituacaoOrderByDataVencimentoAsc(String situacao);
}
