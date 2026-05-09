package com.sinapipro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.PrestacaoContas;

@Repository
public interface PrestacaoContasRepository extends JpaRepository<PrestacaoContas, Long> {
    List<PrestacaoContas> findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataLancamentoAsc(
            Long codigoFuncionario, Long codigoCompetencia);
    List<PrestacaoContas> findBySituacaoOrderByDataLancamentoAsc(String situacao);
}
