package com.sinapipro.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Despesa;
@Repository
public interface DespesasRepository extends JpaRepository<Despesa, Long> {
	List<Despesa> findBySituacaoOrderByDataVencimentoAsc(String situacao);
	List<Despesa> findByDataVencimentoBetweenOrderByDataVencimentoAsc(LocalDate inicio, LocalDate fim);
}
