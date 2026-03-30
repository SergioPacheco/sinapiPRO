package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Veiculo;
@Repository
public interface VeiculosRepository extends JpaRepository<Veiculo, Long> {
List<Veiculo> findByAtivoTrue();
}
