CREATE TABLE classe_sinapi (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    sigla VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(300) NOT NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE insumo_sinapi (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL,
    ano_mes VARCHAR(6),
    unidade VARCHAR(10),
    base VARCHAR(15),
    descricao TEXT NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    codigo_estado BIGINT(20) NOT NULL,
    FOREIGN KEY (codigo_estado) REFERENCES estado(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE composicao_sinapi (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50), 
    base VARCHAR(50),
    ano_mes VARCHAR(06),
    status VARCHAR(20),
    codigo_usuario BIGINT(20),
    codigo_estado BIGINT(20),
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
    FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo),
    FOREIGN KEY (codigo_classe) REFERENCES classe(codigo),
    FOREIGN KEY (codigo_estado) REFERENCES estado(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE item_composicao_sinapi (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    codigo_composicao BIGINT(20),
    codigo_insumo BIGINT(20),
    sku VARCHAR(50),
    tipo VARCHAR(10),
    descricao TEXT,
    unidade VARCHAR(50),
    coeficiente DECIMAL(20, 7),
    preco_unitario DECIMAL(10, 2),
    custo_total DECIMAL(10, 2),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo),
    FOREIGN KEY (codigo_composicao) REFERENCES composicao(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


