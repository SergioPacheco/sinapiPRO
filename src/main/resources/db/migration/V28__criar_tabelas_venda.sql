CREATE TABLE proposta (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_unidade BIGINT NOT NULL,
    codigo_cliente BIGINT NOT NULL,
    data_proposta DATE NOT NULL,
    valor_proposto DECIMAL(19,4) NOT NULL,
    situacao VARCHAR(20) DEFAULT 'PENDENTE',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_unidade) REFERENCES unidade_venda(codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE venda (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_unidade BIGINT NOT NULL,
    codigo_cliente BIGINT NOT NULL,
    codigo_proposta BIGINT,
    data_venda DATE NOT NULL,
    valor_venda DECIMAL(19,4) NOT NULL,
    situacao VARCHAR(20) DEFAULT 'ATIVA',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_unidade) REFERENCES unidade_venda(codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo),
    FOREIGN KEY (codigo_proposta) REFERENCES proposta(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE parcela_venda (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_venda BIGINT NOT NULL,
    numero INT NOT NULL,
    valor DECIMAL(19,4) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_venda) REFERENCES venda(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
