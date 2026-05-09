package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.AgendamentoManutencao;
@Repository
public interface AgendamentosManutencaoRepository extends JpaRepository<AgendamentoManutencao, Long> {
List<AgendamentoManutencao> findByVeiculoCodigoOrderByDataAgendamentoDesc(Long codigoVeiculo);
}
