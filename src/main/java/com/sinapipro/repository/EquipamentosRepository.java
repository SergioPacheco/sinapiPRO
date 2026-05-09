package com.sinapipro.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Equipamento;
@Repository
public interface EquipamentosRepository extends JpaRepository<Equipamento, Long> {
	Optional<Equipamento> findByNomeIgnoreCase(String nome);
}
