CREATE TABLE tipo_custo (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tributo (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    descricao VARCHAR(100) NOT NULL,
    percentual DECIMAL(10,4),
    codigo_estado BIGINT(20),
    FOREIGN KEY (codigo_estado) REFERENCES estado(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tributo_insumo (
    codigo_tributo BIGINT(20) NOT NULL,
    codigo_insumo BIGINT(20) NOT NULL,
    PRIMARY KEY (codigo_tributo, codigo_insumo),
    FOREIGN KEY (codigo_tributo) REFERENCES tributo(codigo),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tributo_composicao (
    codigo_tributo BIGINT(20) NOT NULL,
    codigo_composicao BIGINT(20) NOT NULL,
    PRIMARY KEY (codigo_tributo, codigo_composicao),
    FOREIGN KEY (codigo_tributo) REFERENCES tributo(codigo),
    FOREIGN KEY (codigo_composicao) REFERENCES composicao(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE item_orcamento ADD COLUMN codigo_tipo_custo BIGINT(20) NULL;
ALTER TABLE item_orcamento ADD CONSTRAINT fk_item_tipo_custo FOREIGN KEY (codigo_tipo_custo) REFERENCES tipo_custo(codigo);
