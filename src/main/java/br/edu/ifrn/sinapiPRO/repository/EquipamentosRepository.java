package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Equipamento;
@Repository public interface EquipamentosRepository extends JpaRepository<Equipamento, Long> {
	Optional<Equipamento> findByNomeIgnoreCase(String nome); }
