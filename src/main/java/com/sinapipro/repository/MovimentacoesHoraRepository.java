package com.sinapipro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.MovimentacaoHora;

@Repository
public interface MovimentacoesHoraRepository extends JpaRepository<MovimentacaoHora, Long> {
    List<MovimentacaoHora> findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataMovimentacaoAsc(
            Long codigoFuncionario, Long codigoCompetencia);
}
