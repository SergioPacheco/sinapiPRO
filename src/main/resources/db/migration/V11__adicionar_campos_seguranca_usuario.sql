ALTER TABLE usuario ADD COLUMN primeiro_acesso BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE usuario ADD COLUMN data_ultimo_acesso DATETIME NULL;

CREATE TABLE historico_senha (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_usuario BIGINT NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    data_criacao DATETIME NOT NULL,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE audit_log (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    entidade VARCHAR(100) NOT NULL,
    codigo_entidade BIGINT,
    acao VARCHAR(30) NOT NULL,
    usuario VARCHAR(100),
    data_hora DATETIME NOT NULL,
    detalhes TEXT,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
