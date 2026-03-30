# Sprint 13 — Cadastros de Pessoas e Empresa — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `Empresa` (nome, cnpj, telefone, email, endereco)
- `Departamento`, `Cargo`, `Funcao` (simples)
- `Funcionario` (FK cargo/funcao/departamento, ativo, datas admissão/demissão)
- `ClienteEndereco` (tipo, logradouro, numero, bairro, cep, cidade, UF)
- `ClienteReferencia` (nome, telefone, tipo)

### Migration
- V16: empresa, departamento, cargo, funcao, funcionario, cliente_endereco, cliente_referencia

### Arquivos: 65 (7 CRUDs completos + menu)
