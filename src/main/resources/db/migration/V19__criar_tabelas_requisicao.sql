CREATE TABLE requisicao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    numero INT,
    codigo_obra BIGINT NOT NULL,
    data_requisicao DATE NOT NULL,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE requisicao_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_requisicao BIGINT NOT NULL,
    codigo_insumo BIGINT,
    descricao VARCHAR(200) NOT NULL,
    unidade VARCHAR(20),
    quantidade DECIMAL(19,4) DEFAULT 0,
    quantidade_atendida DECIMAL(19,4) DEFAULT 0,
    situacao VARCHAR(20) DEFAULT 'PENDENTE',
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_requisicao) REFERENCES requisicao(codigo),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
