package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.ifrn.sinapiPRO.model.BaseKey;
import br.edu.ifrn.sinapiPRO.model.ItemBasePreco;
import br.edu.ifrn.sinapiPRO.repository.BasePrecos;
import br.edu.ifrn.sinapiPRO.repository.ItemBasePrecoRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/itemBasePreco")
public class ItemBasePrecosController {

 	@Autowired
	private ItemBasePrecoRepository itemBasePrecoRepository;
 	
 	@Autowired
 	private BasePrecos basePrecoRepository; 
 	
 	 
 	@GetMapping("/basePreco/{codigoBase}/itens")
    public Page<ItemBasePreco> pesquisa(@PathVariable (value = "codigoBase") Long codigoBase, 
    								    Pageable pageable) {
 		
        return itemBasePrecoRepository.findByBaseKeyBasePrecoID(codigoBase, pageable);
    }
     
	
 	@PostMapping("/basePreco/{codigoBasePreco}/itens")
    public ItemBasePreco cadastra(@PathVariable (value = "codigoBasePreco") Long codigoBase,
                                  @Valid @RequestBody ItemBasePreco itemBasePreco) {
 		
        return basePrecoRepository.findById(codigoBase).map(basePreco -> {
            
        	itemBasePreco.setBasePreco(basePreco);
            
            return itemBasePrecoRepository.save(itemBasePreco);
        }).orElseThrow(() -> new ResourceNotFoundException("Erro ao salvar item da base."));
    }
   
 	

    @PutMapping("/basePreco/{codigoBasePreco}/item/{codigoInsumo")
    public ItemBasePreco editar(@PathVariable (value = "codigoBase") Long codigoBase,
                                @PathVariable (value = "codigoItem") Long codigoInsumo,
                                @Valid @RequestBody ItemBasePreco itemPedido) {
    	
        if(!basePrecoRepository.existsById(codigoBase)) {
            throw new ResourceNotFoundException("Erro ao pesquisar item");
        }
        
        BaseKey baseKey = new BaseKey(codigoBase, codigoInsumo);
        
        return itemBasePrecoRepository.findById(baseKey).map(itemBasePreco -> {
        	
            itemBasePreco.setPreco(itemPedido.getPreco()); 
            //
            // todos os campos
            
            return itemBasePrecoRepository.save(itemBasePreco);
        }).orElseThrow(() -> new ResourceNotFoundException("Item Não Econtrado"));
    
    }
 	
    @DeleteMapping("/basePreco/{codigoBase}/item/{codigoInsumo}")
    public ResponseEntity<?> excluir(@PathVariable (value = "codigoBase") Long codigoBase,
                                     @PathVariable (value = "codigoInsumo") Long codigoInsumo) {
        if(!basePrecoRepository.existsById(codigoBase)) {
            throw new ResourceNotFoundException("Item não encontrado");
        }

        BaseKey baseKey = new BaseKey(codigoBase, codigoInsumo);
        
        return itemBasePrecoRepository.findById(baseKey).map(item -> {
             itemBasePrecoRepository.delete(item);
             return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("codigoItem "+codigoBase+" "  + codigoInsumo + " não encontrado"));
    }
     
    
}       
 
