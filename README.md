# SinapiPRO

> Sistema de Gestão de Obras e Orçamentos baseado na tabela SINAPI

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://openjdk.org/projects/jdk/11/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

SinapiPRO é um sistema ERP open source para gestão de obras e orçamentos da construção civil, utilizando a tabela SINAPI (Sistema Nacional de Pesquisa de Custos e Índices da Construção Civil) como base de referência de preços.

---

## ✨ Funcionalidades

### Orçamento
- Criação de orçamentos com insumos e composições SINAPI
- Tipos: Estimativa → Venda → Execução (fluxo completo)
- BDI detalhado (insumo, serviço, terceiro, ferramenta)
- Leis Sociais, Taxa de Administração
- Reajuste de preços em lote (percentual, valor, SINAPI)
- Baseline do orçamento (snapshot + comparativo)
- Planejamento Físico-Financeiro com Curva S
- **Job Costing (EVM)**: PV, EV, AC, CPI, SPI, EAC, VAC

### Suprimentos
- Cotações com análise comparativa de preços
- Geração automática de pedidos pelo menor preço
- Pedidos de Compra com recebimento parcial/total
- Estoque com **Custo Médio Ponderado** (NBC TG 16)
- Requisições de Insumos

### Financeiro
- Plano de Contas hierárquico
- Contas a Pagar e Receber com situação automática
- Movimento Bancário com atualização de saldo
- Conciliação Bancária
- Boletos e Cheques
- Relatórios: Fluxo de Caixa, Balancete, DRE, Inadimplência

### Comercial (Incorporação)
- Espelho de Vendas com situação colorida
- Propostas e Vendas de Unidades
- Geração automática de parcelas (entrada + mensais + chaves)
- Reajuste por índice (INCC, IPCA, CUB)
- Tabela de Preços e Comissões
- Relatórios: Mapa de Vendas, Resumo por Corretor

### Operacional
- Diário de Obra (MO, Equipamentos, Ocorrências, Serviços)
- Relatório de Avanço Físico com Curva de Avanço
- Contratos e Medições com retenção configurável
- Aprovação de medição gera Despesa automaticamente

### Outros Módulos
- **Atendimento/CRM** com SLA por prioridade e escalação automática
- **Banco de Horas** com encerramento de competência (CLT)
- **GED** — upload real de arquivos com validação OWASP
- **Frota** — alertas de manutenção por KM e data
- **Faturamento** — Nota Fiscal de Serviço com cálculo de ISS
- **Relatórios** em PDF (FreeMarker + Flying Saucer)

---

## 🚀 Como Executar

### Pré-requisitos
- Java 11+
- MariaDB / MySQL 8+
- Maven 3.x (ou use o `./mvnw` incluído)

### Início Rápido

```bash
# 1. Clone o repositório
git clone https://github.com/SergioPacheco/sinapiPRO.git
cd sinapiPRO

# 2. Inicia o sistema (cria banco automaticamente)
./run.sh
```

O script `run.sh` faz tudo automaticamente:
- Cria o banco de dados `sinapipro` se não existir
- Compila o projeto se o JAR não existir
- Executa as migrations Flyway (V1→V36)
- Aguarda o startup e confirma disponibilidade

### Acesso

```
URL:   http://localhost:8090
Email: admin@sinapipro.com
Senha: admin123
```

### Comandos do run.sh

```bash
./run.sh          # inicia em background
./run.sh stop     # para o servidor
./run.sh restart  # reinicia
./run.sh logs     # acompanha logs em tempo real
./run.sh build    # só compila
./run.sh status   # verifica se está rodando
```

### Com Docker

```bash
./mvnw package -DskipTests
docker-compose up -d
```

---

## 🗄️ Banco de Dados

O sistema usa **Flyway** para gerenciar o schema. As migrations são executadas automaticamente no startup.

| Migration | Descrição |
|---|---|
| V1–V13 | Schema core (orçamento, insumos, composições, segurança) |
| V14–V19 | Baseline, cadastros, diário de obra, contratos, requisições |
| V20–V26 | Cotações, pedidos, estoque, financeiro completo |
| V27–V32 | Comercial, mão de obra, boletos, atendimento, GED, frota |
| V33–V34 | Custo médio estoque, permissões por módulo |
| V35 | Dados iniciais (admin, grupos, permissões) |
| V36 | Dados de demonstração (seed) |

**Credenciais padrão do banco:**
```
Host:     localhost
Database: sinapipro
User:     sinapipro
Password: sinapipro123
```

---

## 🏗️ Arquitetura

```
src/main/java/br/edu/ifrn/sinapiPRO/
├── config/          ← Spring Security, formatters
├── controller/      ← Spring MVC (Thymeleaf views)
│   ├── handler/     ← @ControllerAdvice exception handler
│   └── page/        ← Pagination wrapper
├── dto/             ← Data transfer objects
├── model/           ← JPA entities (514 classes)
├── repository/      ← Spring Data JPA + Criteria API
├── security/        ← Spring Security config
├── service/         ← Business logic (lógica de negócio real)
│   ├── AnaliseCotacaoService    ← análise comparativa, geração de pedidos
│   ├── MedicaoContratoService   ← cálculo, retenção, aprovação
│   ├── EstoqueService           ← custo médio ponderado (NBC TG 16)
│   ├── VendaParcelasService     ← parcelas automáticas, reajuste por índice
│   ├── AtendimentoSlaService    ← SLA, escalação automática
│   ├── JobCostingService        ← EVM (PMBOK/NBR ISO 21500)
│   └── ...
└── thymeleaf/       ← Custom Thymeleaf processors
```

**Stack:**
- **Backend:** Java 11, Spring Boot 2.7.18, Spring MVC, Spring Security 5
- **Persistência:** Spring Data JPA, Hibernate, Flyway
- **Frontend:** Thymeleaf 3, Bootstrap 3
- **Relatórios:** FreeMarker + Flying Saucer (PDF)
- **Banco:** MariaDB / MySQL

---

## 🔐 Segurança

O sistema usa **Spring Security** com autenticação por formulário e controle de acesso por roles:

| Role | Módulos |
|---|---|
| `ROLE_ADMIN` | Acesso total |
| `ROLE_FINANCEIRO` | Despesas, receitas, movimento bancário, boletos |
| `ROLE_COMERCIAL` | Vendas, propostas, unidades, tabelas de preços |
| `ROLE_SUPRIMENTOS` | Cotações, pedidos, estoque, requisições |
| `ROLE_OBRAS` | Contratos, medições, diário de obra, banco de horas |
| `ROLE_RH` | Funcionários, departamentos, cargos |
| `ROLE_ATENDIMENTO` | Atendimentos e ordens de serviço |

---

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest="DespesaServiceTeste,EstoqueServiceTeste"
```

**39 testes unitários** cobrindo os services críticos:
- `OrcamentoService`, `DespesaService`, `ReajusteService`, `VendaService`
- `AnaliseCotacaoService`, `MedicaoContratoService`, `EstoqueService`
- `BaixaPedidoService`, `AtendimentoSlaService`

---

## 🤝 Como Contribuir

Contribuições são muito bem-vindas! Este é um projeto open source voltado para a comunidade da construção civil brasileira.

### Passos para contribuir

1. **Fork** o repositório
2. Crie uma branch: `git checkout -b feat/minha-funcionalidade`
3. Faça suas alterações seguindo os padrões do projeto
4. Execute os testes: `./mvnw test`
5. Commit: `git commit -m "feat(modulo): descrição da mudança"`
6. Push: `git push origin feat/minha-funcionalidade`
7. Abra um **Pull Request**

### Padrões de commit

```
feat(modulo): nova funcionalidade
fix(modulo): correção de bug
refactor(modulo): refatoração sem mudança de comportamento
test(modulo): adição/correção de testes
docs: atualização de documentação
chore: tarefas de manutenção
```

### O que precisa de ajuda

- [ ] Testes de integração com banco H2
- [ ] Melhorar templates das telas de Atendimento e GED
- [ ] API REST para integração com apps mobile
- [ ] Exportação XLS dos relatórios operacionais
- [ ] Importação de planilhas SINAPI mais recentes
- [ ] Documentação da API (OpenAPI/Swagger)
- [ ] Internacionalização (i18n)
- [ ] Testes end-to-end (Selenium/Playwright)

### Reportar bugs

Abra uma [issue](https://github.com/SergioPacheco/sinapiPRO/issues) com:
- Descrição do problema
- Passos para reproduzir
- Comportamento esperado vs atual
- Versão do sistema e ambiente

---

## 📊 Estatísticas do Projeto

| Métrica | Valor |
|---|---|
| Arquivos Java | 514 |
| Templates Thymeleaf/FTL | 214 |
| Migrations Flyway | 36 |
| Testes unitários | 39 |
| Módulos | 11 fases (Sprints 1–37) |
| Tabelas no banco | ~100 |

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** — veja o arquivo [LICENSE](LICENSE) para detalhes.

```
MIT License

Copyright (c) 2026 Sergio Pacheco

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👥 Autores

- **Sergio Pacheco** — *Desenvolvimento inicial* — [@SergioPacheco](https://github.com/SergioPacheco)

---

## 🙏 Agradecimentos

- [SINAPI/CEF](https://www.caixa.gov.br/poder-publico/modernizacao-gestao/sinapi) — Base de dados de custos da construção civil
- [Spring Boot](https://spring.io/projects/spring-boot) — Framework principal
- [Thymeleaf](https://www.thymeleaf.org/) — Template engine
- [Flyway](https://flywaydb.org/) — Migrations de banco de dados
- Comunidade da construção civil brasileira

---

*SinapiPRO — Gestão inteligente de obras para a construção civil brasileira* 🏗️
