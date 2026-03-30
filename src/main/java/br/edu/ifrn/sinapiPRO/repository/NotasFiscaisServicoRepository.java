package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.NotaFiscalServico;
@Repository
public interface NotasFiscaisServicoRepository extends JpaRepository<NotaFiscalServico, Long> {
List<NotaFiscalServico> findByClienteCodigoOrderByDataEmissaoDesc(Long codigoCliente);
}
