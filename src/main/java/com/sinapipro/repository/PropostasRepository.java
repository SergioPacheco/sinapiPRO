package com.sinapipro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Proposta;

@Repository
public interface PropostasRepository extends JpaRepository<Proposta, Long> {
    List<Proposta> findBySituacaoOrderByDataPropostaDesc(String situacao);
}
