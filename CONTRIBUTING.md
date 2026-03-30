# Guia de Contribuição — SinapiPRO

Obrigado por querer contribuir com o SinapiPRO! 🏗️

Este documento explica como participar do projeto de forma eficiente.

---

## Código de Conduta

Seja respeitoso. Contribuições de todos os níveis são bem-vindas — desde correções de typo até novos módulos.

---

## Como Contribuir

### 1. Reportar Bugs

Abra uma [issue](https://github.com/SergioPacheco/sinapiPRO/issues/new) com:
- **Título claro** descrevendo o problema
- **Passos para reproduzir**
- **Comportamento esperado** vs **comportamento atual**
- **Ambiente**: OS, Java version, banco de dados

### 2. Sugerir Funcionalidades

Abra uma issue com o label `enhancement` descrevendo:
- O problema que a funcionalidade resolve
- Como você imagina a solução
- Se possível, referência a como o Strato (sistema legado) implementa

### 3. Enviar Pull Requests

```bash
# Fork e clone
git clone https://github.com/SEU_USUARIO/sinapiPRO.git
cd sinapiPRO

# Crie uma branch descritiva
git checkout -b feat/analise-cotacao-melhorias

# Faça suas alterações
# ...

# Execute os testes
./mvnw test

# Compile para verificar
./mvnw compile

# Commit seguindo o padrão
git commit -m "feat(cotacao): adicionar filtro por fornecedor na análise"

# Push e abra o PR
git push origin feat/analise-cotacao-melhorias
```

---

## Padrões do Projeto

### Commits (Conventional Commits)

```
feat(modulo): nova funcionalidade
fix(modulo): correção de bug
refactor(modulo): refatoração sem mudança de comportamento
test(modulo): adição/correção de testes
docs: atualização de documentação
chore: tarefas de manutenção
migration: nova migration Flyway
```

**Módulos válidos:** `orcamento`, `cotacao`, `estoque`, `financeiro`, `comercial`, `obras`, `atendimento`, `frota`, `ged`, `seguranca`, `relatorios`

### Estrutura de Código

Siga o padrão existente:
- **Model**: `src/main/java/.../model/`
- **Repository**: `src/main/java/.../repository/` + `helper/`
- **Service**: `src/main/java/.../service/` — lógica de negócio aqui
- **Controller**: `src/main/java/.../controller/` — apenas HTTP + view binding
- **Templates**: `src/main/resources/templates/`

### Formatação Java (IntelliJ Standard)

- Cada método em sua própria linha
- Cada anotação em linha separada (`@Service`, `@Transactional`, etc.)
- Getters/setters expandidos
- try-catch em múltiplas linhas
- Sem imports inline (`java.util.Map` → import no topo)

### Testes

Todo novo service deve ter testes unitários com Mockito:

```java
@RunWith(MockitoJUnitRunner.class)
public class MeuServiceTeste {

    @Mock
    private MeuRepository repository;

    @InjectMocks
    private MeuService service;

    @Test
    public void should_resultado_when_condicao() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Migrations Flyway

- Arquivo: `V{N}__descricao_snake_case.sql`
- Use `INSERT IGNORE` para dados iniciais
- Sempre teste rollback manual antes de submeter
- Documente o propósito no comentário inicial

---

## Áreas que Precisam de Ajuda

| Área | Dificuldade | Descrição |
|---|---|---|
| Testes H2 | Média | Testes de integração com banco em memória |
| API REST | Média | Endpoints JSON para apps mobile |
| Exportação XLS | Fácil | Apache POI nos relatórios operacionais |
| Swagger/OpenAPI | Fácil | Documentação automática da API |
| i18n | Média | Internacionalização (pt-BR já é padrão) |
| Selenium/Playwright | Alta | Testes end-to-end |
| Templates Atendimento | Fácil | Melhorar UI do módulo CRM |
| Importação SINAPI | Média | Atualizar planilhas SINAPI mais recentes |

---

## Ambiente de Desenvolvimento

```bash
# Requisitos
java -version   # Java 11+
mysql --version # MariaDB/MySQL 8+

# Iniciar
./run.sh

# Compilar sem rodar
./mvnw compile -s /tmp/settings-local.xml -o

# Testes
./mvnw test

# Acesso
http://localhost:8090
admin@sinapipro.com / admin123
```

---

## Dúvidas?

Abra uma [issue](https://github.com/SergioPacheco/sinapiPRO/issues) com o label `question`.

Obrigado por contribuir! 🙏
