-- Dados iniciais: grupos, permissões e usuário admin
-- Senha padrão: admin123 (BCrypt)

INSERT IGNORE INTO grupo (nome) VALUES
('Administradores'), ('Financeiro'), ('Comercial'),
('Suprimentos'), ('Obras'), ('RH'), ('Atendimento');

INSERT IGNORE INTO permissao (nome) VALUES
('ROLE_ADMIN'), ('ROLE_FINANCEIRO'), ('ROLE_COMERCIAL'),
('ROLE_SUPRIMENTOS'), ('ROLE_OBRAS'), ('ROLE_RH'),
('ROLE_ATENDIMENTO'), ('ROLE_CADASTRAR_ORCAMENTO'),
('ROLE_CADASTRAR_USUARIO');

-- Todas as permissões para Administradores
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo FROM grupo g, permissao p
WHERE g.nome = 'Administradores';

-- Permissão específica por grupo
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo FROM grupo g, permissao p
WHERE (g.nome = 'Financeiro'   AND p.nome = 'ROLE_FINANCEIRO')
   OR (g.nome = 'Comercial'    AND p.nome = 'ROLE_COMERCIAL')
   OR (g.nome = 'Suprimentos'  AND p.nome = 'ROLE_SUPRIMENTOS')
   OR (g.nome = 'Obras'        AND p.nome = 'ROLE_OBRAS')
   OR (g.nome = 'RH'           AND p.nome = 'ROLE_RH')
   OR (g.nome = 'Atendimento'  AND p.nome = 'ROLE_ATENDIMENTO');

-- Usuário admin padrão (senha: admin123)
INSERT IGNORE INTO usuario (nome, email, senha, ativo, primeiro_acesso)
SELECT 'Administrador', 'admin@sinapipro.com',
       '$2a$10$0xo4DjqxNgqvvRZzMl.E2eE.Q6rkkHVc20tww2noPr7TFNFDKMB3G',
       1, 0
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@sinapipro.com');

-- Vincula admin ao grupo Administradores
INSERT IGNORE INTO usuario_grupo (codigo_usuario, codigo_grupo)
SELECT u.codigo, g.codigo
FROM usuario u, grupo g
WHERE u.email = 'admin@sinapipro.com' AND g.nome = 'Administradores'
  AND NOT EXISTS (
    SELECT 1 FROM usuario_grupo ug2
    WHERE ug2.codigo_usuario = u.codigo AND ug2.codigo_grupo = g.codigo
  );
