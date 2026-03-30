CREATE TABLE tabela_preco (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    codigo_obra BIGINT NOT NULL,
    data_vigencia DATE,
    ativa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tabela_preco_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_tabela BIGINT NOT NULL,
    codigo_unidade BIGINT NOT NULL,
    valor DECIMAL(19,4) NOT NULL,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_tabela) REFERENCES tabela_preco(codigo),
    FOREIGN KEY (codigo_unidade) REFERENCES unidade_venda(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE comissao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_venda BIGINT NOT NULL,
    nome_corretor VARCHAR(200) NOT NULL,
    percentual DECIMAL(10,4) DEFAULT 0,
    valor DECIMAL(19,4) DEFAULT 0,
    data_pagamento DATE,
    situacao VARCHAR(20) DEFAULT 'PENDENTE',
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_venda) REFERENCES venda(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
