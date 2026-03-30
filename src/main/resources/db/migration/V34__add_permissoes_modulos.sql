-- Adiciona permissões para os novos módulos
-- Módulos: FINANCEIRO, COMERCIAL, SUPRIMENTOS, OBRAS, RH, ATENDIMENTO, ADMIN

INSERT IGNORE INTO permissao (nome) VALUES
('ROLE_FINANCEIRO'),
('ROLE_COMERCIAL'),
('ROLE_SUPRIMENTOS'),
('ROLE_OBRAS'),
('ROLE_RH'),
('ROLE_ATENDIMENTO'),
('ROLE_ADMIN');

-- Cria grupo ADMIN com todas as permissões (se não existir)
INSERT IGNORE INTO grupo (nome) VALUES ('Administradores'), ('Financeiro'), ('Comercial'), ('Suprimentos'), ('Obras'), ('RH'), ('Atendimento');

-- Associa permissões ao grupo Administradores
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'Administradores'
  AND p.nome IN ('ROLE_ADMIN', 'ROLE_FINANCEIRO', 'ROLE_COMERCIAL',
                      'ROLE_SUPRIMENTOS', 'ROLE_OBRAS', 'ROLE_RH',
                      'ROLE_ATENDIMENTO', 'ROLE_CADASTRAR_ORCAMENTO',
                      'ROLE_CADASTRAR_USUARIO');

-- Associa permissão ao grupo Financeiro
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'Financeiro' AND p.nome = 'ROLE_FINANCEIRO';

-- Associa permissão ao grupo Comercial
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'Comercial' AND p.nome = 'ROLE_COMERCIAL';

-- Associa permissão ao grupo Suprimentos
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'Suprimentos' AND p.nome = 'ROLE_SUPRIMENTOS';

-- Associa permissão ao grupo Obras
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'Obras' AND p.nome = 'ROLE_OBRAS';

-- Associa permissão ao grupo RH
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'RH' AND p.nome = 'ROLE_RH';

-- Associa permissão ao grupo Atendimento
INSERT IGNORE INTO grupo_permissao (codigo_grupo, codigo_permissao)
SELECT g.codigo, p.codigo
FROM grupo g, permissao p
WHERE g.nome = 'Atendimento' AND p.nome = 'ROLE_ATENDIMENTO';
