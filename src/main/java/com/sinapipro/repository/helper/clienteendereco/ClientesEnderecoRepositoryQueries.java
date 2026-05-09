package com.sinapipro.repository.helper.clienteendereco;
import java.util.List;
import com.sinapipro.model.ClienteEndereco;
public interface ClientesEnderecoRepositoryQueries { List<ClienteEndereco> findByClienteCodigo(Long codigoCliente);
}
