package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Proposta;

@Repository
public interface PropostasRepository extends JpaRepository<Proposta, Long> {
    List<Proposta> findBySituacaoOrderByDataPropostaDesc(String situacao);
}
