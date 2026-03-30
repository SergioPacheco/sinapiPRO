package br.edu.ifrn.sinapiPRO.repository.helper.clienteendereco;
import java.util.List; import br.edu.ifrn.sinapiPRO.model.ClienteEndereco;
public interface ClientesEnderecoRepositoryQueries { List<ClienteEndereco> findByClienteCodigo(Long codigoCliente); }
