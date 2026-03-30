CREATE TABLE empresa (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    cnpj VARCHAR(20),
    telefone VARCHAR(20),
    email VARCHAR(100),
    endereco VARCHAR(200),
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE departamento (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cargo (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE funcao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE funcionario (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    cpf VARCHAR(20),
    email VARCHAR(100),
    telefone VARCHAR(20),
    data_admissao DATE,
    data_demissao DATE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    codigo_cargo BIGINT,
    codigo_funcao BIGINT,
    codigo_departamento BIGINT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cargo) REFERENCES cargo(codigo),
    FOREIGN KEY (codigo_funcao) REFERENCES funcao(codigo),
    FOREIGN KEY (codigo_departamento) REFERENCES departamento(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cliente_endereco (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_cliente BIGINT NOT NULL,
    tipo VARCHAR(50),
    logradouro VARCHAR(200),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cep VARCHAR(10),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cliente_referencia (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_cliente BIGINT NOT NULL,
    nome VARCHAR(200) NOT NULL,
    telefone VARCHAR(20),
    tipo VARCHAR(50),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
