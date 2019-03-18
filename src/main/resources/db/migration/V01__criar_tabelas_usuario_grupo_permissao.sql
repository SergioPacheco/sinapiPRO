CREATE TABLE usuario (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    senha VARCHAR(120) NOT NULL,
    ativo BOOLEAN DEFAULT true NOT NULL, 
    estado VARCHAR(02), 
    data_nascimento DATE,
    estado VARCHAR(2),
    codigo_orcamento_atual BIGINT(20)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE grupo (
    codigo BIGINT(20) PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE permissao (
    codigo BIGINT(20) PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE usuario_grupo (
    codigo_usuario BIGINT(20) NOT NULL,
    codigo_grupo BIGINT(20) NOT NULL,
    PRIMARY KEY (codigo_usuario, codigo_grupo),
    FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo),
    FOREIGN KEY (codigo_grupo) REFERENCES grupo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE grupo_permissao (
    codigo_grupo BIGINT(20) NOT NULL,
    codigo_permissao BIGINT(20) NOT NULL,
    PRIMARY KEY (codigo_grupo, codigo_permissao),
    FOREIGN KEY (codigo_grupo) REFERENCES grupo(codigo),
    FOREIGN KEY (codigo_permissao) REFERENCES permissao(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Adicionar usuario, grupo e permissao 

-- Inserir grupos e administrador
INSERT INTO grupo (codigo, nome) VALUES (1, 'Administrador');
INSERT INTO grupo (codigo, nome) VALUES (2, 'Orcamentista');
INSERT INTO grupo (codigo, nome) VALUES (3, 'Cliente');

INSERT INTO usuario (nome, email, senha, ativo, estado, codigo_orcamento_atual, codigo_etapa_selecionada) 
VALUES ('admin', 'admin@sinapipro.com', '$2a$10$g.wT4R0Wnfel1jc/k84OXuwZE02BlACSLfWy6TycGPvvEKvIm86SG', TRUE, "RN", NULL, NULL);
--                                       $2a$10$g.wT4R0Wnfel1jc/k84OXuwZE02BlACSLfWy6TycGPvvEKvIm86SG

-- Inserir permissões e relacionar com o usuario administrador  
INSERT INTO permissao VALUES (1, 'ROLE_CADASTRAR_CIDADE');
INSERT INTO permissao VALUES (2, 'ROLE_CADASTRAR_USUARIO');
INSERT INTO permissao VALUES (3, 'ROLE_CADASTRAR_ORCAMENTO');
INSERT INTO permissao VALUES (4, 'ROLE_CANCELAR_ORCAMENTO');


INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 1);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 2);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 3);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 4);

INSERT INTO usuario_grupo (codigo_usuario, codigo_grupo) 
     VALUES ( (SELECT codigo FROM usuario WHERE email = 'admin@sinapipro.com'), 1);
     
ALTER TABLE usuario	MODIFY ativo BOOLEAN DEFAULT true NOT NULL;     

ALTER TABLE usuario CHANGE COLUMN orcamento_atual orcamento_atual BIGINT(20) NULL DEFAULT NULL ;  
     
     



