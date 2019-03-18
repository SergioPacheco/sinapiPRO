package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class ClienteService {
	
	@Autowired
	private ClientesRepository clientes;
	
	@Transactional
	public void salvar(Cliente cliente){
		
		Optional<Cliente> clienteExistente = clientes.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());
		
		if(clienteExistente.isPresent() && cliente.isNovo()){
			System.out.println("isNovo: "+cliente.isNovo()+""+cliente.getCodigo());
			System.out.println("clienteExistente: "+ clienteExistente.isPresent());
			throw new JaCadastradoException ("CPF/CNPJ já cadastrado!");
		}
		clientes.save(cliente);
	}
	
	@Transactional
	public void excluir(Long codigo) {
		try {
			clientes.deleteById(codigo);  
			clientes.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar o cliente. Já foi usado em algum orçamento.");

		}
	}
}
