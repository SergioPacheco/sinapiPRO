-- Inserir grupos e administrador
INSERT INTO grupo (codigo, nome) VALUES (1, 'Administrador');
INSERT INTO grupo (codigo, nome) VALUES (2, 'Orcamentista');
INSERT INTO grupo (codigo, nome) VALUES (3, 'Cliente');

INSERT INTO usuario (nome, email, senha, ativo) 
VALUES ('admin', 'admin', '$2a$10$g.wT4R0Wnfel1jc/k84OXuwZE02BlACSLfWy6TycGPvvEKvIm86SG', 1);

-- Inserir permissões e relacionar com o usuario administrador  
INSERT INTO permissao VALUES (1, 'ROLE_CADASTRAR_CIDADE');
INSERT INTO permissao VALUES (2, 'ROLE_CADASTRAR_USUARIO');
INSERT INTO permissao VALUES (3, 'ROLE_CADASTRAR_ORCAMENTO');
INSERT INTO permissao VALUES (4, 'ROLE_CANCELAR_ORCAMENTO');


INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 1);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 2);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 3);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 4);

INSERT INTO usuario_grupo (codigo_usuario, codigo_grupo) VALUES (
	(SELECT codigo FROM usuario WHERE email = 'admin'), 1);




