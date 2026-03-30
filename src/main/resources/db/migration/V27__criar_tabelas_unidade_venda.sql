CREATE TABLE situacao_unidade (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cor VARCHAR(20),
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE unidade_venda (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_obra BIGINT NOT NULL,
    identificacao VARCHAR(50) NOT NULL,
    tipo VARCHAR(50),
    bloco VARCHAR(20),
    andar VARCHAR(20),
    area_privativa DECIMAL(10,2),
    area_total DECIMAL(10,2),
    valor_base DECIMAL(19,4) DEFAULT 0,
    codigo_situacao BIGINT,
    descricao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_situacao) REFERENCES situacao_unidade(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE caracteristica_unidade (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_unidade BIGINT NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    valor VARCHAR(100),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_unidade) REFERENCES unidade_venda(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
