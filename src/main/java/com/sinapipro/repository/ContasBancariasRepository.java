package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.ContaBancaria;
@Repository
public interface ContasBancariasRepository extends JpaRepository<ContaBancaria, Long> {
	List<ContaBancaria> findByAtivaTrue();
}
