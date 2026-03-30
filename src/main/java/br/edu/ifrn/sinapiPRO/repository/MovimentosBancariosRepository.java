package br.edu.ifrn.sinapiPRO.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.MovimentoBancario;
@Repository
public interface MovimentosBancariosRepository extends JpaRepository<MovimentoBancario, Long> {
	List<MovimentoBancario> findByContaBancariaCodigoOrderByDataMovimentoDesc(Long codigoConta);
	List<MovimentoBancario> findByContaBancariaCodigoAndDataMovimentoBetweenOrderByDataMovimentoAsc(Long codigoConta, LocalDate inicio, LocalDate fim);
}
