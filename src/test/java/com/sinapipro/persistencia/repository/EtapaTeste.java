package com.sinapipro.persistencia.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sinapipro.model.Etapa;
import com.sinapipro.repository.EtapasRepository;


@RunWith(SpringRunner.class) 
@DataJpaTest
public class EtapaTeste {

	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
	  mockMvc = MockMvcBuilders
	            .webAppContextSetup(context)
	            .apply(springSecurity())
	            .alwaysDo(print())
	            .build();
	}	
	
	
	 @Autowired
	 private TestEntityManager entityManager;
	
	 @Autowired
	 private EtapasRepository etapasRepository;
	
	
	 private String nome1 = "PROJETO";
	 private String nome2 = "INSTALAÇOES";
	
	 @Test
	 public void retornaTodosPesistidos() throws IOException {
		 
		 // dado
		 entityManager.persist(new Etapa(nome1));
		 entityManager.persist(new Etapa(nome2));
		  
		 
		 // faça
		 Iterable<Etapa> etapas = etapasRepository.findAll();
		 Etapa etapa1 = etapasRepository.findByNomeIgnoreCase(nome1).get();
		 Etapa etapa2 = etapasRepository.findByNomeIgnoreCase(nome1).get();
		 
		 // então
		 assertThat(etapas).hasSize(2);
		 assertThat(etapas).contains(etapa1);
		 assertThat(etapas).contains(etapa2);
	
		 assertThat(etapa1.getNome()).isEqualTo(nome1);
		 assertThat(etapa2.getNome()).isEqualTo(nome2);
	  }
	
	  @Test
	  public void criaUsuario() {
		// dado 
		// faça   
		etapasRepository.save(new Etapa(nome1));
		Etapa criaEtapa = etapasRepository.findByNomeIgnoreCase(nome1).get();
		// então
	    assertThat(criaEtapa.getNome()).isEqualTo(nome1);
	  }

}