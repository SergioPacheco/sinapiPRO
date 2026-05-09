package com.sinapipro.repository.helper.clientereferencia;
import java.util.List;
import com.sinapipro.model.ClienteReferencia;
public interface ClientesReferenciaRepositoryQueries { List<ClienteReferencia> findByClienteCodigo(Long codigoCliente);
}
