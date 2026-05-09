# Guia de Contribuição — SinapiPRO

Obrigado por querer contribuir com o SinapiPRO! 🏗️

---

## Código de Conduta

Seja respeitoso. Contribuições de todos os níveis são bem-vindas.

---

## Como Contribuir

### 1. Reportar Bugs

Abra uma [issue](https://github.com/SergioPacheco/sinapiPRO/issues/new) com:
- Título claro descrevendo o problema
- Passos para reproduzir
- Comportamento esperado vs atual
- Ambiente: OS, Java version

### 2. Sugerir Funcionalidades

Abra uma issue com label `enhancement` descrevendo o problema que resolve.

### 3. Enviar Código

```bash
# Fork + clone
git clone https://github.com/SEU_USER/sinapiPRO.git
cd sinapiPRO/api

# Crie uma branch
git checkout -b feat/minha-funcionalidade

# Desenvolva e teste
mvn test -s .mvn/settings.xml

# Commit e push
git commit -m "feat(modulo): descrição da mudança"
git push origin feat/minha-funcionalidade
```

Abra um Pull Request para `main`.

---

## Pré-requisitos

```bash
java -version   # Java 25 (Temurin)
mvn -version    # Maven 3.9+
docker -v       # Docker (para PostgreSQL via Testcontainers)
```

Instale via [SDKMAN](https://sdkman.io/):
```bash
sdk install java 25-tem
sdk install maven
```

---

## Estrutura do Projeto

```
sinapiPRO/
├── api/                    ← MÓDULO ATIVO (Java 25 + Spring Boot 4)
│   ├── pom.xml
│   ├── compose.yaml        ← PostgreSQL para dev
│   └── src/
├── docs/                   ← Documentação (Mermaid, arquitetura, domínio)
└── src/                    ← Módulo legado (somente referência, não alterar)
```

**Todo desenvolvimento acontece em `api/`.**

---

## Comandos

```bash
cd api
mvn compile -s .mvn/settings.xml          # compilar
mvn test -s .mvn/settings.xml             # testes (requer Docker)
mvn spring-boot:run -s .mvn/settings.xml  # executar
```

---

## Padrões de Código

### Arquitetura (Vertical Slicing)
```
{module}/
├── api/            ← Controllers + DTOs (records)
├── application/    ← Services + business logic
└── domain/         ← Entities + Repositories
```

### Java 25 Features (usar sempre que possível)
- `import module java.base;` — substitui imports de java.util.*, java.time.*, etc
- `var` — inferência de tipo em variáveis locais
- `_` — unnamed variables em lambdas
- Sealed classes/interfaces para hierarquias
- Pattern matching em switch
- Records para DTOs e value objects
- Structured Concurrency para operações paralelas
- Gatherers para agregações em stream

### Convenções
- Entities: singular em inglês (`Budget`, `Supplier`)
- DTOs: `Create{Entity}Request`, `{Entity}Response`
- Exceptions: `{Domain}NotFoundException`
- Valores monetários: `BigDecimal` + `numeric(18,2)` no banco
- PKs: UUID (nunca sequencial)

---

## Padrões de Commit

```
feat(modulo): nova funcionalidade
fix(modulo): correção de bug
refactor(modulo): refatoração sem mudança de comportamento
test(modulo): adição/correção de testes
docs: atualização de documentação
```

---

## Banco de Dados

- **PostgreSQL 17** (sobe automaticamente via Docker Compose)
- Migrations via **Flyway** em `api/src/main/resources/db/migration/`
- Nunca alterar migrations existentes — criar nova migration

---

## Testes

- **Testcontainers** — PostgreSQL real nos testes de integração
- **Spring Security Test** — JWT mockado
- Todo novo service deve ter testes unitários
- Bug fixes devem incluir teste de regressão

---

## Licença

Ao contribuir, você concorda que suas contribuições serão licenciadas sob a [MIT License](LICENSE).
