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

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.BasePrecoItem;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecoItemRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/itemBasePreco")
public class BasePrecosItemController {


    
}       
 
