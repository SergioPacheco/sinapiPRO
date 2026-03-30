CREATE TABLE plano_contas (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(20) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nivel INT DEFAULT 1,
    codigo_pai BIGINT,
    ativo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_pai) REFERENCES plano_contas(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE conta_bancaria (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    banco VARCHAR(100) NOT NULL,
    agencia VARCHAR(20),
    conta VARCHAR(30) NOT NULL,
    descricao VARCHAR(200),
    saldo_inicial DECIMAL(19,4) DEFAULT 0,
    saldo_atual DECIMAL(19,4) DEFAULT 0,
    ativa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE historico_bancario (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(200) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
