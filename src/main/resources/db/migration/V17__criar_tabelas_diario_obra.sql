-- Cadastros auxiliares
CREATE TABLE diario_clima (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE diario_area (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE diario_acidente (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Diário principal
CREATE TABLE diario_obra (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_obra BIGINT NOT NULL,
    data DATE NOT NULL,
    entrada1 VARCHAR(10),
    saida1 VARCHAR(10),
    entrada2 VARCHAR(10),
    saida2 VARCHAR(10),
    observacao TEXT,
    codigo_clima BIGINT,
    codigo_area BIGINT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_clima) REFERENCES diario_clima(codigo),
    FOREIGN KEY (codigo_area) REFERENCES diario_area(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Itens do diário
CREATE TABLE diario_mao_obra (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_diario BIGINT NOT NULL,
    descricao VARCHAR(200),
    quantidade INT DEFAULT 0,
    horas_trabalhadas DECIMAL(10,2) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_diario) REFERENCES diario_obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE diario_equipamento (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_diario BIGINT NOT NULL,
    descricao VARCHAR(200),
    quantidade INT DEFAULT 0,
    horas_trabalhadas DECIMAL(10,2) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_diario) REFERENCES diario_obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE diario_ocorrencia (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_diario BIGINT NOT NULL,
    descricao TEXT,
    codigo_acidente BIGINT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_diario) REFERENCES diario_obra(codigo),
    FOREIGN KEY (codigo_acidente) REFERENCES diario_acidente(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE diario_servico (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_diario BIGINT NOT NULL,
    descricao VARCHAR(200),
    quantidade DECIMAL(10,2) DEFAULT 0,
    unidade VARCHAR(20),
    percentual_executado DECIMAL(10,2) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_diario) REFERENCES diario_obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
