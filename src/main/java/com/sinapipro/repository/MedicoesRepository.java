package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Medicao;
@Repository
public interface MedicoesRepository extends JpaRepository<Medicao, Long> {
	List<Medicao> findByContratoCodigoOrderByNumeroAsc(Long codigoContrato);
}
