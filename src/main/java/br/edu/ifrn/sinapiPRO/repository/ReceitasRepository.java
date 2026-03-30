package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Receita;
@Repository
public interface ReceitasRepository extends JpaRepository<Receita, Long> {
	List<Receita> findBySituacaoOrderByDataVencimentoAsc(String situacao);
}
