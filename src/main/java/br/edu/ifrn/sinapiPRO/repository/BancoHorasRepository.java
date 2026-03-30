package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.BancoHoras;

@Repository
public interface BancoHorasRepository extends JpaRepository<BancoHoras, Long> {
    List<BancoHoras> findByCompetenciaCodigoOrderByFuncionarioNomeAsc(Long codigoCompetencia);
    Optional<BancoHoras> findByFuncionarioCodigoAndCompetenciaCodigo(Long codigoFuncionario, Long codigoCompetencia);
}
