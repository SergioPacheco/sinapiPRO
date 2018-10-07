CREATE TABLE base_insumo (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(300) NOT NULL, 
    codigo_base_preco BIGINT(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO base_insumo (codigo, nome, codigo_base_preco) VALUES (1,'Sinapi', '1');
INSERT INTO base_insumo (codigo, nome, codigo_base_preco) VALUES (2,'Base Propria', '1');

CREATE TABLE base_preco (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(300) NOT NULL, 
    codigo_base_insumo BIGINT(20),
    data_referencia DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO base_preco  (codigo, nome, codigo_base_insumo) VALUES (1, 'Sinapi_RN_Janeiro_2018_com_desoneração', 1);

--Many to Many
CREATE TABLE insumo_preco (
    codigo_base_insumo BIGINT NOT NULL,
    codigo_base_preco BIGINT NOT NULL,
    FOREIGN KEY (codigo_base_insumo) REFERENCES base_insumo (codigo) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (codigo_base_preco)  REFERENCES base_preco  (codigo) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (codigo_base_insumo, codigo_base_precos)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT TABLE insumo_preco (codigo_base_insumo, codigo_base_preco) VALUE (1,1) 
INSERT TABLE insumo_preco (codigo_base_insumo, codigo_base_preco) VALUE (2,1) 

CREATE TABLE item_base_preco (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    codigo_base_preco BIGINT(20),
    codigo_insumo BIGINT(20), 
    preco DECIMAL(15,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO base_insumos (codigo, nome, codigo_base_precos) VALUES (1,'Sinapi', '1');
INSERT INTO base_insumos (codigo, nome, codigo_base_precos) VALUES (2,'Base Propria', '1');

INSERT INTO base_precos  (codigo, nome, codigo_base_insumos) VALUES (1, 'Sinapi_RN_Janeiro_2018_com_desoneração', 1);

CREATE TABLE classe (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    sigla VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(300) NOT NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- insumo.tipo 
--	ADM - Administracao 
--  EQ  - Equipamento 
--  MA  - Material 
--  MO  - Mao de Obra

CREATE TABLE insumo (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    codigo_base_preco BIGINT(20),
    codigo_base_insumo BIGINT(20),
    codigo_classe BIGINT(20),
    sku VARCHAR(50) NOT NULL,
    unidade VARCHAR(10),
    tipo VARCHAR(03), 
    descricao TEXT NOT NULL,
    preco_generico DECIMAL(15, 2),
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 
CREATE TABLE composicao (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50), 
    status VARCHAR(20),
    codigo_classe BIGINT(20),
    descricao TEXT,
    unidade VARCHAR(50),
    valor_total DECIMAL(15,2),
    custo_total DECIMAL(15, 2),
    custo_mao_obra DECIMAL(15, 2),
    percentual_mao_obra DECIMAL(15, 7),
    custo_material DECIMAL(15, 2),
    percentual_material DECIMAL(15, 7),
    custo_equipamento DECIMAL(15, 2),
    percentual_equipamento DECIMAL(15, 7),
    custo_servicos_terceiros DECIMAL(15, 2),
    percentual_servicos_terceiros DECIMAL(15, 7),
    custo_outros DECIMAL(15, 7),
    percentual_outros DECIMAL(15, 7),
    data_criacao DATETIME,  
    FOREIGN KEY (codigo_classe) REFERENCES classe(codigo),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE item_composicao (
	tipo CHAR(1),
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    codigo_composicao BIGINT(20),
    codigo_insumo BIGINT(20),
    sku VARCHAR(50),
    descricao TEXT,
    unidade VARCHAR(50),
    coeficiente DECIMAL(20, 7),
    preco_unitario DECIMAL(10, 2),
    custo_total DECIMAL(10, 2),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo),
    FOREIGN KEY (codigo_composicao) REFERENCES composicao(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


