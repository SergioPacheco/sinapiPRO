package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Atendimento;
@Repository
public interface AtendimentosRepository extends JpaRepository<Atendimento, Long> {
List<Atendimento> findBySituacaoOrderByDataAberturaDesc(String situacao);
}
