CREATE TABLE equipamento (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(200),
    tipo VARCHAR(50),
    numero_serie VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE estoque (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_obra BIGINT NOT NULL,
    codigo_insumo BIGINT NOT NULL,
    quantidade_atual DECIMAL(19,4) DEFAULT 0,
    quantidade_minima DECIMAL(19,4) DEFAULT 0,
    localizacao VARCHAR(100),
    PRIMARY KEY (codigo),
    UNIQUE KEY uk_estoque_obra_insumo (codigo_obra, codigo_insumo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE movimento_estoque (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_estoque BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    quantidade DECIMAL(19,4) NOT NULL,
    data_movimento DATE NOT NULL,
    documento VARCHAR(100),
    observacao VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_estoque) REFERENCES estoque(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
