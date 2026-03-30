package br.edu.ifrn.sinapiPRO.repository.helper.clientereferencia;
import java.util.List; import br.edu.ifrn.sinapiPRO.model.ClienteReferencia;
public interface ClientesReferenciaRepositoryQueries { List<ClienteReferencia> findByClienteCodigo(Long codigoCliente); }
