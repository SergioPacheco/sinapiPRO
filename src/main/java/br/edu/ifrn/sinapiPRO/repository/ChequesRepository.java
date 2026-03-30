package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Cheque;

@Repository
public interface ChequesRepository extends JpaRepository<Cheque, Long> {
    List<Cheque> findBySituacaoOrderByDataEmissaoDesc(String situacao);
    List<Cheque> findByContaBancariaCodigoOrderByDataEmissaoDesc(Long codigoConta);
}
