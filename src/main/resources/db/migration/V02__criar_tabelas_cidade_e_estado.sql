CREATE TABLE estado (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    sigla VARCHAR(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cidade (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    codigo_estado BIGINT(20) NOT NULL,
    FOREIGN KEY (codigo_estado) REFERENCES estado(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Adicionar estados e cidades

INSERT INTO estado (codigo, nome, sigla) VALUES (1,'Acre','AC'); 	
INSERT INTO estado (codigo, nome, sigla) VALUES (2,'Alagoas','AL');	
INSERT INTO estado (codigo, nome, sigla) VALUES (3,'Amapá','AP');	
INSERT INTO estado (codigo, nome, sigla) VALUES (4,'Amazonas','AM');	
INSERT INTO estado (codigo, nome, sigla) VALUES (5,'Bahia','BA');	
INSERT INTO estado (codigo, nome, sigla) VALUES (6,'Ceará','CE');	
INSERT INTO estado (codigo, nome, sigla) VALUES (7,'Distrito Federal','DF');	
INSERT INTO estado (codigo, nome, sigla) VALUES (8,'Espírito Santo','ES');	
INSERT INTO estado (codigo, nome, sigla) VALUES (9,'Goiás','GO');	
INSERT INTO estado (codigo, nome, sigla) VALUES (10,'Maranhão','MA');	
INSERT INTO estado (codigo, nome, sigla) VALUES (11,'Mato Grosso','MT');	
INSERT INTO estado (codigo, nome, sigla) VALUES (12,'Mato Grosso do Sul','MS');
INSERT INTO estado (codigo, nome, sigla) VALUES (13,'Minas Gerais','MG');	
INSERT INTO estado (codigo, nome, sigla) VALUES (14,'Pará','PA'	);
INSERT INTO estado (codigo, nome, sigla) VALUES (15,'Paraíba','PB');	
INSERT INTO estado (codigo, nome, sigla) VALUES (16,'Paraná','PR');	
INSERT INTO estado (codigo, nome, sigla) VALUES (17,'Pernambuco','PE');	
INSERT INTO estado (codigo, nome, sigla) VALUES (18,'Piauí','PI');	
INSERT INTO estado (codigo, nome, sigla) VALUES (19,'Rio de Janeiro','RJ');	
INSERT INTO estado (codigo, nome, sigla) VALUES (20,'Rio Grande do Norte','RN');	
INSERT INTO estado (codigo, nome, sigla) VALUES (21,'Rio Grande do Sul','RS');	
INSERT INTO estado (codigo, nome, sigla) VALUES (22,'Rondônia','RO');	
INSERT INTO estado (codigo, nome, sigla) VALUES (23,'Roraima','RR');	
INSERT INTO estado (codigo, nome, sigla) VALUES (24,'Santa Catarina','SC');	
INSERT INTO estado (codigo, nome, sigla) VALUES (25,'São Paulo','SP');	
INSERT INTO estado (codigo, nome, sigla) VALUES (26,'Sergipe','SE');	
INSERT INTO estado (codigo, nome, sigla) VALUES (27,'Tocantins','TO');	


INSERT INTO cidade (nome, codigo_estado) VALUES ('Rio Branco',1);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Maceió', 2);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Macapá', 3);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Manaus', 4);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Salvador', 5);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Fortaleza', 6);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Brasília', 7);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Vitória', 8);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Goiânia', 9);
INSERT INTO cidade (nome, codigo_estado) VALUES ('São Luís', 10);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Cuiabá', 11);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Campo Grande', 12); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Belo Horizonte', 13) 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Belém', 14); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('João Pessoa', 15); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Curitiba', 16); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Recife', 17); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Teresina', 18); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Rio de Janeiro', 19); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Natal', 20); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Porto Alegre',21);  
INSERT INTO cidade (nome, codigo_estado) VALUES ('Porto Velho', 22); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Boa Vista', 23); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Florianópolis', 24); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('São Paulo', 25);
INSERT INTO cidade (nome, codigo_estado) VALUES ('Aracaju', 26); 
INSERT INTO cidade (nome, codigo_estado) VALUES ('Palmas', 27); 



