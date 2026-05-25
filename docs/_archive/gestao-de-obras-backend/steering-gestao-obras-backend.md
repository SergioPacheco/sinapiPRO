# Steering - Backend Sistema Multi-Tenant de Gestão de Obras

## Objetivo deste steering

Este documento orienta o desenvolvimento backend de um sistema de gestão de obras inspirado em fluxos reais de operação: orçamento, planejamento, diário de obras, compras, medição, documentos, permissões e relatórios.

O desenvolvimento deve seguir **Spec Driven Development**, guiado por fluxos reais de negócio, e não por CRUDs isolados.

O sistema é obrigatoriamente **multi-tenant**. Nenhum usuário pode visualizar, consultar, alterar, excluir, exportar ou inferir dados de outro tenant, empresa, obra ou usuário fora do seu escopo de autorização.

---

# 1. Princípio central do produto

A entidade operacional central do sistema é:

```text
Obra
```

Todo módulo operacional deve nascer ou se conectar a uma obra:

```text
Empresa
  └── Obra
        ├── Orçamento
        ├── Base Própria / Composições
        ├── Planejamento
        ├── Diário de Obras
        ├── Compras
        ├── Medição
        ├── Documentos / CDE
        ├── BIM / Plugins
        └── BI / Relatórios
```

Não implemente módulos como cadastros soltos. Cada entidade deve existir porque sustenta um fluxo operacional real da obra.

---

# 2. Regra absoluta de multi-tenancy

## 2.1 Isolamento obrigatório

O sistema deve garantir isolamento de dados por tenant/empresa.

Todo dado operacional deve estar vinculado direta ou indiretamente a uma empresa:

```text
empresa_id
```

Quando aplicável, também deve estar vinculado a uma obra:

```text
obra_id
```

Nenhum endpoint pode retornar dados sem filtrar pelo tenant ativo do usuário autenticado.

---

## 2.2 Regra de ouro

Nunca buscar entidades apenas por `id`.

Errado:

```java
orcamentoRepository.findById(id);
```

Correto:

```java
orcamentoRepository.findByIdAndEmpresaId(id, empresaId);
```

Ou, quando a entidade pertence a uma obra:

```java
orcamentoRepository.findByIdAndEmpresaIdAndObraId(id, empresaId, obraId);
```

---

## 2.3 Obrigatório em todas as queries

Toda query de leitura, escrita, atualização, exclusão, relatório, exportação ou busca deve aplicar escopo de segurança.

Regras mínimas:

```text
- Filtrar por empresa_id sempre que a entidade for multi-tenant.
- Filtrar por obra_id quando a entidade for operacional de obra.
- Validar se o usuário pertence à empresa.
- Validar se o usuário possui papel/permissão na obra, quando aplicável.
- Validar se o módulo está licenciado para a empresa.
- Validar se o usuário possui permissão no módulo.
```

---

## 2.4 Proibição de vazamento indireto

O backend também deve impedir vazamento indireto de dados.

Exemplos proibidos:

```text
- Autocomplete retornar itens de outra empresa.
- Relatório consolidar dados de outra empresa.
- Count revelar quantidade de registros de outro tenant.
- Exportação PDF/Excel incluir dados fora do escopo.
- Endpoint por token externo acessar pedido/cotação de outra empresa.
- Erro informar que um ID existe em outro tenant.
```

Quando o usuário tentar acessar um registro fora do tenant, o sistema deve responder como se o recurso não existisse ou como acesso negado, conforme política definida.

---

## 2.5 Contexto obrigatório de segurança

Toda requisição autenticada deve possuir contexto de segurança com, no mínimo:

```text
user_id
empresa_id ativa
perfis globais
permissões por módulo
papéis por obra, quando aplicável
licenças/plugins habilitados
```

Criar componente central:

```text
TenantContext
SecurityContext
PermissionEvaluator
ObraAccessValidator
ModuloLicencaValidator
```

Nenhum service de aplicação deve depender de `empresa_id` informado livremente pelo frontend sem validar se o usuário realmente pertence à empresa.

---

# 3. Fluxo operacional macro do sistema

A jornada real do usuário deve seguir este ciclo:

```text
1. Administrador configura empresa, usuários, setores, permissões e licenças.
2. Usuário cria uma obra operacional.
3. Usuário prepara base própria de insumos e composições, se necessário.
4. Usuário cria ou importa orçamento da obra.
5. Usuário aprova uma versão do orçamento e marca como orçamento vigente.
6. Usuário cria planejamento a partir do orçamento vigente.
7. Usuário registra execução no diário de obras.
8. Diário de obras atualiza acompanhamento do planejamento.
9. Compras cria pedidos usando itens do orçamento ou curva ABC.
10. Medição usa itens do orçamento para medir execução.
11. Fiscal aprova ou rejeita medição.
12. Relatórios e BI consolidam orçamento, planejamento, diário, compras e medição.
```

O backend deve ser implementado para sustentar esse ciclo completo.

---

# 4. Diferença entre módulo e fluxo

## Módulo

Módulo é uma área funcional do sistema:

```text
- Orçamento
- Planejamento
- Diário de Obras
- Compras
- Medição
- CDE
- BI
```

## Fluxo

Fluxo é o trabalho real do usuário:

```text
- Criar obra
- Montar orçamento
- Transformar orçamento em planejamento
- Registrar execução diária
- Comprar material previsto
- Medir serviço executado
- Aprovar medição
```

O desenvolvimento deve priorizar fluxos. Módulos existem para servir fluxos.

---

# 5. Escopo dos módulos

## 5.1 Administrar Licença

Este módulo controla quem pode operar o sistema.

Escopo:

```text
- Empresa
- Usuários da empresa
- Setores
- Permissões
- Módulos habilitados
- Plugins habilitados
- Encargos sociais
- Configurações globais da empresa
```

Fluxo:

```text
Administrador entra
  → cadastra/configura empresa
  → cria setores
  → adiciona usuários
  → define permissões
  → libera módulos/plugins
  → configura encargos sociais
  → libera operação das obras
```

Regras:

```text
- Usuário pode existir globalmente e pertencer a várias empresas.
- Remover usuário da empresa não apaga o usuário global.
- Permissão deve ser por empresa, módulo e, quando aplicável, por obra.
- Plugin só aparece se a licença permitir.
- Alteração de permissões deve gerar auditoria.
```

---

## 5.2 Obras

Obra é o centro operacional do sistema.

Escopo:

```text
- Cadastro da obra
- Dados gerais
- Membros
- Responsável técnico
- Fiscais
- Empreiteiros
- Anexos
- Vínculo com orçamento
- Vínculo com planejamento
- Vínculo com diário
- Vínculo com compras
- Vínculo com medição
```

Fluxo:

```text
Usuário cria obra
  → adiciona membros
  → define responsável técnico
  → cadastra fiscais
  → cadastra empreiteiros
  → anexa documentos
  → cria ou vincula orçamento
```

Regras:

```text
- Uma obra pode ter mais de um orçamento.
- Apenas um orçamento deve ser marcado como orçamento vigente/base operacional.
- Planejamento, compras e medições devem preferencialmente usar o orçamento vigente.
- Uma obra pode ter vários fiscais.
- Uma obra pode ter vários empreiteiros.
- Um usuário pode possuir papéis diferentes em obras diferentes.
```

Estados sugeridos:

```text
RASCUNHO
ATIVA
PARALISADA
CONCLUIDA
CANCELADA
```

---

## 5.3 Base Própria / Base de Composições

Este módulo alimenta o orçamento.

Escopo:

```text
- Banco próprio de insumos
- Banco próprio de composições
- Busca de composições
- Busca de insumos
- Criação manual
- Importação por Excel
- Atualização de preços
- Cópia de composição existente
- Sincronização com orçamento
```

Fluxo:

```text
Usuário acessa Base de Composições
  → cria banco próprio
  → importa ou cadastra insumos
  → cria ou importa composições
  → usa essas composições no orçamento
  → altera preços futuramente
  → sistema sinaliza orçamentos desatualizados
  → usuário decide atualizar ou não
```

Regra crítica:

```text
Alterar preço na base própria NÃO deve alterar automaticamente um orçamento já aprovado ou fechado.
```

Fluxo correto de atualização:

```text
Preço alterado na base própria
  → sistema marca item do orçamento como desatualizado
  → usuário consulta diferenças
  → usuário escolhe atualizar item individualmente ou em massa
  → orçamento recalcula
  → auditoria registra alteração
```

---

## 5.4 Orçamento

Orçamento representa o custo previsto da obra.

Escopo:

```text
- Criar orçamento
- Importar orçamento
- Definir data base
- Definir bases de preço
- Definir arredondamento geral
- Definir arredondamento por base
- Criar etapas
- Adicionar composições
- Adicionar insumos
- Adicionar itens próprios
- Usar máscara de item
- Duplicar itens
- Recuperar itens excluídos
- Usar lixeira
- Usar tags
- Alterar data base
- Resolver divergência de data
- Ajustar valor do orçamento
- Gerar curva ABC
- Gerar relatórios sintéticos/analíticos
- Personalizar relatórios
- Aprovar versão
- Marcar orçamento vigente da obra
```

Fluxo:

```text
Usuário cria orçamento
  → escolhe obra
  → define data base
  → escolhe bases de preço
  → define BDI/encargos/arredondamento
  → cria/importa estrutura de etapas
  → adiciona composições/insumos
  → ajusta quantidades
  → revisa divergências
  → gera relatórios
  → gera curva ABC
  → aprova versão
  → marca orçamento como vigente da obra
```

Estados sugeridos:

```text
RASCUNHO
EM_REVISAO
APROVADO
VIGENTE_NA_OBRA
SUBSTITUIDO
CANCELADO
```

Regras:

```text
- Orçamento em rascunho pode ser editado.
- Orçamento aprovado não deve ser alterado diretamente.
- Alteração relevante em orçamento aprovado deve gerar nova versão.
- Planejamento deve nascer preferencialmente de orçamento aprovado/vigente.
- Compras deve usar orçamento vigente para saldo.
- Medição deve usar orçamento vigente ou contrato derivado dele.
```

---

## 5.5 Planejamento

Planejamento transforma orçamento em tempo, sequência e acompanhamento.

Escopo:

```text
- Criar planejamento
- Definir data de início
- Definir calendário de trabalho
- Definir feriados/dias não trabalhados
- Definir durações
- Definir predecessores
- Configurar colunas
- Iniciar planejamento com dados do orçamento
- Considerar itens agregados como distintos
- Atualizar planejamento após ajuste no orçamento
- Acompanhar progresso
- Emitir relatórios
```

Fluxo:

```text
Usuário seleciona obra
  → seleciona orçamento vigente
  → cria planejamento
  → define início e calendário
  → importa atividades do orçamento
  → ajusta durações
  → define predecessores
  → publica planejamento
  → diário de obras alimenta execução
  → acompanhamento compara previsto x realizado
```

Interrelação:

```text
Orçamento → Planejamento
Planejamento → Diário de Obras
Diário de Obras → Acompanhamento do Planejamento
Planejamento → Relatórios/BI
```

Regra crítica:

```text
Se o orçamento for alterado depois do planejamento criado, o sistema não deve sobrescrever automaticamente o planejamento.
```

Fluxo correto:

```text
Orçamento ajustado
  → planejamento fica com status de alterações pendentes
  → usuário abre comparação
  → sistema mostra itens novos/removidos/alterados
  → usuário decide sincronizar
```

---

## 5.6 Diário de Obras

Diário de Obras registra a execução real da obra.

Escopo:

```text
- Criar diário de obra
- Copiar dados do último relatório
- Registrar atividades executadas
- Adicionar tarefas conforme planejamento
- Registrar progresso
- Registrar entrada/saída de materiais
- Inserir fotos no relatório fotográfico
- Registrar ocorrências
- Emitir relatórios
- Validar período de relatório
```

Fluxo:

```text
Usuário entra na obra
  → cria diário para uma data
  → opcionalmente copia último diário
  → informa clima/equipe/ocorrências
  → seleciona tarefas do planejamento
  → informa status/progresso
  → registra materiais
  → anexa fotos
  → fecha diário
  → sistema atualiza acompanhamento do planejamento
```

Interrelação:

```text
Planejamento → lista tarefas disponíveis
Diário → informa execução real
Diário → atualiza planejamento
Diário → evidencia medição
Diário → alimenta BI
```

Regras:

```text
- Não deve existir dois diários para mesma obra/data, salvo configuração de turnos.
- Diário fechado não deve ser alterado sem reabertura justificada.
- Tarefa informada no diário deve atualizar progresso planejado.
- Foto deve ficar vinculada ao diário, obra, data e usuário.
- Movimento de material pode alimentar suprimentos/estoque, se existir.
```

---

## 5.7 Compras

Compras transforma necessidade da obra em pedido, cotação, aprovação e ordem de compra.

Escopo:

```text
- Criar pedido
- Inserir itens do orçamento
- Comprar a partir da curva ABC
- Cadastrar fornecedor
- Enviar cotação
- Controlar editabilidade da cotação
- Comparar preços
- Aprovação técnica
- Aprovação financeira
- Emissão de ordem de compra
- Relatórios de compras
```

Fluxo:

```text
Requisitante cria pedido
  → escolhe obra
  → adiciona itens do orçamento ou curva ABC
  → informa quantidades solicitadas
  → envia para responsável técnico
  → responsável técnico aprova/rejeita
  → setor de compras seleciona fornecedores
  → sistema envia cotação
  → fornecedores respondem
  → compras compara preços
  → escolhe vencedor
  → envia para financeiro
  → financeiro aprova
  → ordem de compra é emitida
```

Interrelação:

```text
Orçamento → itens disponíveis para compra
Curva ABC → priorização de compras
Compras → compromete saldo financeiro
Compras → alimenta BI
Diário → pode registrar chegada/saída de material comprado
```

Regras:

```text
- Item vindo do orçamento carrega unidade, quantidade orçada e saldo.
- Item não orçado pode ser comprado, mas deve ser marcado como extra.
- Pedido aprovado tecnicamente não deve ser livremente editado.
- Cotação enviada não deve ser editável em determinados status.
- Ordem de compra só pode sair após aprovação financeira.
```

Estados sugeridos:

```text
RASCUNHO
ENVIADO_RESPONSAVEL_TECNICO
APROVADO_TECNICAMENTE
REJEITADO_TECNICAMENTE
EM_COTACAO
COTADO
ENVIADO_FINANCEIRO
APROVADO_FINANCEIRO
ORDEM_COMPRA_EMITIDA
CANCELADO
```

---

## 5.8 Medição

Medição apura o que foi executado para fiscalização, pagamento e controle físico-financeiro.

Escopo:

```text
- Criar medição de obra
- Importar itens por planilha
- Selecionar itens do orçamento
- Criar relatório fotográfico
- Adicionar serviço não orçado
- Adicionar aditivo
- Inserir empreiteiro
- Controlar editabilidade da medição
- Solicitar aprovação
- Aprovar/rejeitar por fiscal
- Emitir relatórios
```

Fluxo:

```text
Usuário cria medição
  → escolhe obra
  → escolhe orçamento/contrato base
  → importa ou seleciona itens
  → informa quantidade executada
  → informa memória de cálculo
  → adiciona fotos/anexos
  → adiciona serviço não orçado ou aditivo, se necessário
  → vincula empreiteiro
  → solicita aprovação
  → fiscal aprova ou rejeita
  → sistema gera relatório
```

Interrelação:

```text
Orçamento → base dos itens medíveis
Diário → evidência da execução
Medição → atualiza executado físico/financeiro
Fiscal → aprova/rejeita
BI → compara previsto x medido
```

Regras:

```text
- Medição em rascunho pode ser editada.
- Medição enviada para aprovação pode ter edição bloqueada.
- Medição aprovada não deve ser editável.
- Serviço não orçado deve ficar destacado.
- Aditivo deve ficar separado do orçamento original.
- Memória de cálculo deve ser versionada/auditada.
```

Estados sugeridos:

```text
RASCUNHO
EM_APROVACAO
APROVADA
REJEITADA
CANCELADA
FECHADA
```

---

## 5.9 OF Medição

OF Medição deve ser tratado como interface/produto avançado para operar o mesmo domínio de medição, e não como um domínio separado.

Escopo:

```text
- Área de trabalho
- Obras
- Membros da obra
- Fiscais
- Empreiteiros
- Supervisores
- Medições da obra
- Solicitar aprovação
- Aprovar medição por fiscal
- Alterar memória de cálculo
- Importar itens
- Lixeira
- Relatórios
- Anexos
```

Regra de arquitetura:

```text
Não duplicar domínio. Usar o domínio medicao com APIs adequadas para a interface OF Medição.
```

---

## 5.10 CDE / Documentos

CDE deve ser tratado como ambiente comum de dados da obra.

Escopo mínimo:

```text
- Projeto
- Documentos
- Pastas
- Versões
- Revisões
- Permissões
- Anexos
- Relatórios
```

Interrelação:

```text
Obra → Projeto CDE
CDE → documentos da obra
CDE → anexos de orçamento/medição/diário
```

---

## 5.11 BI / Relatórios

BI consolida os dados dos módulos operacionais.

Escopo proposto:

```text
- Indicadores de orçamento
- Indicadores de planejamento
- Indicadores de diário
- Indicadores de compras
- Indicadores de medição
- Previsto x realizado
- Orçado x comprado
- Orçado x medido
- Avanço físico
- Avanço financeiro
```

Regra:

```text
Todo relatório e indicador deve respeitar empresa_id, obra_id e permissões do usuário.
```

---

# 6. Mapa de interrelação dos módulos

```text
ADMINISTRAR LICENÇA
  ↓ libera usuários, permissões e plugins

OBRAS
  ↓ centraliza tudo

BASE DE COMPOSIÇÕES
  ↓ fornece insumos/composições próprios

ORÇAMENTO
  ↓ gera custo previsto
  ↓ gera curva ABC
  ↓ fornece itens para planejamento, compras e medição

PLANEJAMENTO
  ↓ transforma orçamento em cronograma
  ↓ fornece tarefas para diário

DIÁRIO DE OBRAS
  ↓ registra execução real
  ↓ atualiza acompanhamento do planejamento
  ↓ evidencia medição

COMPRAS
  ↓ usa itens do orçamento/curva ABC
  ↓ gera pedidos, cotações e ordens de compra

MEDIÇÃO
  ↓ usa orçamento/contrato como base
  ↓ pode usar evidências do diário
  ↓ fiscal aprova executado

BI / RELATÓRIOS
  ↓ consolida tudo
```

---

# 7. Backlog orientado por fluxos

As tasks devem ser organizadas por fluxos reais, não por CRUD isolado.

Ordem funcional recomendada:

```text
Fluxo 01 - Preparar empresa
Fluxo 02 - Criar obra operacional
Fluxo 03 - Preparar base própria
Fluxo 04 - Montar orçamento
Fluxo 05 - Aprovar orçamento vigente
Fluxo 06 - Gerar planejamento
Fluxo 07 - Registrar execução no diário
Fluxo 08 - Comprar com base no orçamento
Fluxo 09 - Medir execução
Fluxo 10 - Aprovar medição
Fluxo 11 - Consolidar BI
Fluxo 12 - Gerenciar documentos/CDE
Fluxo 13 - Integrar BIM/plugins
```

---

# 8. Tasks macro por fluxo

## Fluxo 01 - Preparar empresa

Objetivo: permitir que uma empresa esteja pronta para operar o sistema.

Tasks:

```markdown
- [ ] Criar modelo Empresa
- [ ] Criar modelo Usuario
- [ ] Criar vínculo EmpresaUsuario
- [ ] Criar modelo Setor
- [ ] Criar modelo Permissao
- [ ] Criar modelo ModuloSistema
- [ ] Criar modelo Licenca
- [ ] Criar controle de módulos habilitados por licença
- [ ] Criar controle de plugins habilitados por licença
- [ ] Criar cadastro de encargos sociais
- [ ] Implementar API para adicionar usuário existente à empresa
- [ ] Implementar API para convidar novo usuário
- [ ] Implementar API para remover usuário da empresa sem apagar usuário global
- [ ] Implementar API para configurar permissões
- [ ] Implementar validação de permissão por módulo
- [ ] Implementar auditoria de alteração de permissão
- [ ] Criar testes de isolamento multiempresa
- [ ] Criar testes de acesso negado por módulo não licenciado
```

Critério de aceite:

```text
Dado um administrador de empresa
Quando ele cadastrar usuários, setores e permissões
Então cada usuário deve acessar apenas os módulos permitidos
E apenas os dados da empresa ativa
```

---

## Fluxo 02 - Criar obra operacional

Objetivo: criar a entidade central que receberá orçamento, planejamento, diário, compras, medições e documentos.

Tasks:

```markdown
- [ ] Criar modelo Obra
- [ ] Criar status da obra
- [ ] Criar modelo ObraMembro
- [ ] Criar papel na obra
- [ ] Criar modelo Fiscal
- [ ] Criar modelo Empreiteiro
- [ ] Criar modelo AnexoObra
- [ ] Implementar criação de obra
- [ ] Implementar edição de dados gerais da obra
- [ ] Implementar vínculo de membros
- [ ] Implementar vínculo de fiscais
- [ ] Implementar vínculo de empreiteiros
- [ ] Implementar upload de anexos
- [ ] Implementar bloqueio de exclusão de obra com orçamento/medição ativa
- [ ] Criar testes de criação de obra
- [ ] Criar testes de permissão por papel na obra
- [ ] Criar testes de anexos
```

Critério de aceite:

```text
Dado uma obra ativa
Quando o usuário acessar qualquer módulo operacional
Então essa obra deve poder ser usada como contexto principal
```

---

## Fluxo 03 - Preparar base própria

Objetivo: permitir que a empresa tenha insumos e composições próprios para usar no orçamento.

Tasks:

```markdown
- [ ] Criar modelo BasePropria
- [ ] Criar modelo InsumoProprio
- [ ] Criar modelo HistoricoPrecoInsumo
- [ ] Criar modelo ComposicaoPropria
- [ ] Criar modelo ComposicaoPropriaItem
- [ ] Implementar cadastro manual de insumo
- [ ] Implementar importação XLSX de insumos
- [ ] Implementar relatório de erros da importação
- [ ] Implementar atualização de preço de insumos existentes
- [ ] Implementar cadastro manual de composição
- [ ] Implementar cálculo do preço total da composição
- [ ] Implementar cópia de composição existente
- [ ] Implementar importação XLSX de composições
- [ ] Implementar busca por código/descrição
- [ ] Criar testes de importação de insumos
- [ ] Criar testes de atualização de preços
- [ ] Criar testes de composição com insumos
- [ ] Criar testes de cálculo de composição
```

Critério de aceite:

```text
Dado uma composição própria cadastrada
Quando o usuário montar um orçamento
Então essa composição deve poder ser inserida como item orçamentário
```

---

## Fluxo 04 - Montar orçamento

Objetivo: permitir que o usuário crie o orçamento previsto da obra.

Tasks:

```markdown
- [ ] Criar modelo Orcamento
- [ ] Criar modelo OrcamentoVersao
- [ ] Criar modelo OrcamentoEtapa
- [ ] Criar modelo OrcamentoItem
- [ ] Criar modelo OrcamentoBasePreco
- [ ] Criar modelo OrcamentoBdi
- [ ] Criar modelo OrcamentoEncargoSocial
- [ ] Criar status do orçamento
- [ ] Implementar criação de orçamento vinculado à obra
- [ ] Implementar definição de data base
- [ ] Implementar seleção de bases de preço
- [ ] Implementar arredondamento geral
- [ ] Implementar arredondamento por base
- [ ] Implementar criação de etapas
- [ ] Implementar adição de composição oficial
- [ ] Implementar adição de composição própria
- [ ] Implementar adição de insumo próprio
- [ ] Implementar cálculo de item
- [ ] Implementar cálculo de etapa
- [ ] Implementar cálculo total do orçamento
- [ ] Implementar aprovação do orçamento
- [ ] Implementar marcação como orçamento vigente da obra
- [ ] Criar testes de cálculo
- [ ] Criar testes de arredondamento
- [ ] Criar testes de aprovação
- [ ] Criar testes de orçamento vigente
```

Critério de aceite:

```text
Dado uma obra ativa
Quando o usuário aprovar um orçamento
Então esse orçamento poderá ser usado para planejamento, compras e medição
```

---

## Fluxo 05 - Revisar, versionar e relatar orçamento

Objetivo: permitir manutenção controlada do orçamento sem perder rastreabilidade.

Tasks:

```markdown
- [ ] Implementar máscara de item
- [ ] Implementar tags de itens
- [ ] Implementar duplicação de etapa
- [ ] Implementar duplicação de item
- [ ] Implementar lixeira de orçamento
- [ ] Implementar recuperação de itens excluídos
- [ ] Implementar alteração de data base
- [ ] Implementar identificação de divergência de data
- [ ] Implementar preview de ajuste linear
- [ ] Implementar confirmação de ajuste linear
- [ ] Implementar snapshot antes do ajuste
- [ ] Implementar nova versão ao alterar orçamento aprovado
- [ ] Criar auditoria detalhada das alterações
- [ ] Implementar relatório sintético
- [ ] Implementar relatório analítico
- [ ] Implementar curva ABC de insumos
- [ ] Implementar curva ABC de serviços
- [ ] Implementar exportação Excel
- [ ] Implementar exportação PDF
- [ ] Criar testes de versionamento
- [ ] Criar testes de lixeira
- [ ] Criar testes de divergência de data
- [ ] Criar testes de ajuste linear
- [ ] Criar testes de curva ABC
```

---

## Fluxo 06 - Gerar planejamento

Objetivo: transformar orçamento aprovado em cronograma executável.

Tasks:

```markdown
- [ ] Criar modelo Planejamento
- [ ] Criar modelo PlanejamentoAtividade
- [ ] Criar modelo PlanejamentoCalendario
- [ ] Criar modelo PlanejamentoFeriado
- [ ] Criar modelo PlanejamentoPredecessor
- [ ] Implementar criação vinculada à obra
- [ ] Implementar vínculo com orçamento base
- [ ] Implementar inicialização por itens do orçamento
- [ ] Implementar opção de itens agregados como distintos
- [ ] Implementar data de início
- [ ] Implementar jornada diária
- [ ] Implementar jornada extra
- [ ] Implementar sábados/domingos/feriados
- [ ] Implementar definição de duração
- [ ] Implementar cálculo de datas planejadas
- [ ] Implementar predecessores
- [ ] Implementar validação de ciclo
- [ ] Implementar publicação do planejamento
- [ ] Criar testes de geração a partir do orçamento
- [ ] Criar testes de predecessores
- [ ] Criar testes de calendário
```

Critério de aceite:

```text
Dado um orçamento vigente
Quando o usuário criar um planejamento
Então o sistema deve gerar atividades planejáveis a partir da estrutura orçamentária
```

---

## Fluxo 07 - Sincronizar planejamento com orçamento alterado

Objetivo: não quebrar planejamento já criado quando o orçamento for alterado.

Tasks:

```markdown
- [ ] Criar mecanismo de comparação orçamento x planejamento
- [ ] Detectar item novo no orçamento
- [ ] Detectar item removido do orçamento
- [ ] Detectar quantidade alterada
- [ ] Detectar valor alterado
- [ ] Criar endpoint de preview de sincronização
- [ ] Criar endpoint de aplicar sincronização
- [ ] Preservar progresso já executado
- [ ] Criar auditoria da sincronização
- [ ] Criar testes de item novo
- [ ] Criar testes de item removido
- [ ] Criar testes preservando avanço realizado
```

---

## Fluxo 08 - Registrar execução no diário

Objetivo: registrar o que aconteceu na obra e atualizar o acompanhamento do planejamento.

Tasks:

```markdown
- [ ] Criar modelo DiarioObra
- [ ] Criar modelo DiarioAtividade
- [ ] Criar modelo DiarioMaterialMovimento
- [ ] Criar modelo DiarioFoto
- [ ] Criar modelo DiarioOcorrencia
- [ ] Implementar criação de diário por obra/data
- [ ] Implementar validação de duplicidade obra/data
- [ ] Implementar cópia do último diário
- [ ] Implementar seleção de tarefas do planejamento
- [ ] Implementar atualização de status da tarefa
- [ ] Implementar atualização de progresso
- [ ] Implementar entrada de material
- [ ] Implementar saída de material
- [ ] Implementar upload de fotos
- [ ] Implementar fechamento do diário
- [ ] Publicar evento DiarioFechado
- [ ] Atualizar acompanhamento do planejamento
- [ ] Criar testes de diário único por data
- [ ] Criar testes de atualização do planejamento
- [ ] Criar testes de movimento de material
```

Critério de aceite:

```text
Dado uma atividade planejada
Quando ela for registrada no diário com progresso
Então o acompanhamento do planejamento deve refletir o progresso informado
```

---

## Fluxo 09 - Comprar com base no orçamento

Objetivo: transformar demanda da obra em pedido, cotação, aprovação e ordem de compra.

Tasks:

```markdown
- [ ] Criar modelo PedidoCompra
- [ ] Criar modelo PedidoCompraItem
- [ ] Criar modelo Fornecedor
- [ ] Criar modelo Cotacao
- [ ] Criar modelo CotacaoFornecedor
- [ ] Criar modelo CotacaoItem
- [ ] Criar modelo OrdemCompra
- [ ] Implementar criação de pedido vinculado à obra
- [ ] Implementar busca de itens do orçamento
- [ ] Implementar compra a partir da curva ABC
- [ ] Preencher unidade/quantidade/saldo do item orçado
- [ ] Permitir item não orçado
- [ ] Implementar envio para aprovação técnica
- [ ] Implementar aprovação/rejeição técnica
- [ ] Implementar seleção de fornecedores
- [ ] Implementar envio de cotação
- [ ] Implementar token externo para fornecedor
- [ ] Implementar resposta da cotação
- [ ] Implementar comparação de preços
- [ ] Implementar escolha de menor preço por item
- [ ] Implementar escolha de fornecedor vencedor
- [ ] Implementar envio ao financeiro
- [ ] Implementar aprovação financeira
- [ ] Implementar emissão de ordem de compra
- [ ] Bloquear edição conforme status
- [ ] Criar testes do fluxo completo
```

---

## Fluxo 10 - Medir execução

Objetivo: apurar serviços executados para fiscalização, pagamento e controle.

Tasks:

```markdown
- [ ] Criar modelo Medicao
- [ ] Criar modelo MedicaoItem
- [ ] Criar modelo MedicaoMemoriaCalculo
- [ ] Criar modelo MedicaoAditivo
- [ ] Criar modelo MedicaoServicoNaoOrcado
- [ ] Criar modelo MedicaoAprovacao
- [ ] Criar modelo RelatorioFotograficoMedicao
- [ ] Implementar criação de medição por obra
- [ ] Implementar seleção de orçamento/contrato base
- [ ] Implementar importação de itens
- [ ] Implementar seleção de itens do orçamento
- [ ] Implementar quantidade medida
- [ ] Implementar quantidade acumulada
- [ ] Implementar saldo a medir
- [ ] Implementar memória de cálculo
- [ ] Versionar memória de cálculo
- [ ] Implementar serviço não orçado
- [ ] Implementar aditivo
- [ ] Implementar vínculo com empreiteiro
- [ ] Implementar anexos/fotos
- [ ] Implementar solicitação de aprovação
- [ ] Implementar aprovação por fiscal
- [ ] Implementar rejeição com justificativa
- [ ] Bloquear edição conforme status
- [ ] Implementar relatório de medição
- [ ] Criar testes de medição acumulada
- [ ] Criar testes de aprovação fiscal
- [ ] Criar testes de bloqueio de edição
```

---

## Fluxo 11 - Consolidar BI e relatórios

Objetivo: consolidar orçamento, planejamento, diário, compras e medição.

Tasks:

```markdown
- [ ] Criar indicadores de valor orçado
- [ ] Criar indicadores de valor planejado
- [ ] Criar indicadores de progresso físico planejado
- [ ] Criar indicadores de progresso físico realizado
- [ ] Criar indicadores de valor comprado
- [ ] Criar indicadores de valor medido
- [ ] Criar indicador orçado x comprado
- [ ] Criar indicador orçado x medido
- [ ] Criar indicador planejado x realizado
- [ ] Criar relatório executivo da obra
- [ ] Criar exportação PDF
- [ ] Criar exportação Excel
- [ ] Garantir isolamento por empresa/obra em todos os indicadores
- [ ] Criar testes de segurança para relatórios
```

---

# 9. Regras de arquitetura backend

## 9.1 Não criar CRUD solto

Antes de criar uma entidade, endpoint ou tabela, responder:

```text
Qual fluxo de negócio esta entidade sustenta?
Qual usuário usa isso?
Em qual etapa da obra isso acontece?
Qual módulo depende disso?
Qual regra de permissão protege isso?
```

Se essas perguntas não tiverem resposta clara, não implementar ainda.

---

## 9.2 Todo endpoint deve ter caso de uso

Endpoints devem refletir ações reais do usuário.

Preferir:

```http
POST /api/obras/{obraId}/orcamentos/{orcamentoId}/aprovar
POST /api/obras/{obraId}/planejamentos/{planejamentoId}/publicar
POST /api/obras/{obraId}/diarios/{diarioId}/fechar
POST /api/obras/{obraId}/medicoes/{medicaoId}/solicitar-aprovacao
POST /api/obras/{obraId}/medicoes/{medicaoId}/aprovar
```

Evitar apenas:

```http
PUT /api/orcamentos/{id}
PUT /api/medicoes/{id}
```

Ações de mudança de estado devem ser explícitas.

---

## 9.3 Estados devem controlar edição

Entidades operacionais devem ter status.

Exemplos:

```text
Orçamento aprovado não deve ser editado diretamente.
Planejamento publicado não deve ser sobrescrito sem sincronização controlada.
Diário fechado não deve ser alterado sem reabertura justificada.
Pedido aprovado tecnicamente não deve ser livremente editado.
Cotação enviada não deve ser editada livremente.
Medição aprovada não deve ser editada.
```

---

## 9.4 Auditoria obrigatória

Gerar auditoria para:

```text
- Alteração de permissões
- Criação/edição/exclusão de obra
- Aprovação de orçamento
- Marcação de orçamento vigente
- Ajuste linear de orçamento
- Alteração de data base
- Sincronização de planejamento
- Fechamento/reabertura de diário
- Aprovação/rejeição técnica de compra
- Aprovação/rejeição financeira de compra
- Emissão de ordem de compra
- Solicitação/aprovação/rejeição de medição
- Alteração de memória de cálculo
- Exclusão/restauração de itens
- Geração/exportação de relatório sensível
```

Auditoria mínima:

```text
usuario_id
empresa_id
obra_id, quando aplicável
entidade
entidade_id
operação
valores anteriores, quando aplicável
valores novos, quando aplicável
data/hora
ip/user-agent, se disponível
```

---

## 9.5 Testes obrigatórios de segurança

Toda feature multi-tenant deve ter testes provando que um usuário não acessa dados de outro tenant.

Cenários mínimos:

```text
- Usuário da Empresa A não lista dados da Empresa B.
- Usuário da Empresa A não acessa entidade da Empresa B por ID direto.
- Usuário sem papel na obra não acessa dados da obra.
- Usuário sem módulo licenciado não acessa endpoint do módulo.
- Relatório não exporta dados fora do escopo.
- Autocomplete não retorna dados de outro tenant.
- Token externo de fornecedor só acessa a cotação vinculada.
```

---

# 10. Prompt para IA de desenvolvimento

Use este prompt para orientar agentes de desenvolvimento:

```text
Você é um Tech Lead Java especialista em sistemas multi-tenant de gestão de obras.

Implemente o backend guiado por fluxos reais de gestão de obras, não por CRUD isolado.

A entidade central do sistema é Obra.

Todo módulo operacional deve nascer a partir de uma Obra:
- Orçamento
- Planejamento
- Diário de Obras
- Compras
- Medição
- Documentos/CDE
- Relatórios

O sistema é multi-tenant.
Nenhum usuário pode visualizar, consultar, alterar, excluir, exportar ou inferir dados de outro usuário, empresa, tenant ou obra fora do seu escopo.

Regras obrigatórias de segurança:
1. Toda entidade operacional deve possuir empresa_id.
2. Toda entidade operacional de obra deve possuir obra_id.
3. Nunca buscar entidade apenas por id.
4. Toda query deve filtrar por empresa_id.
5. Toda query operacional deve validar obra_id quando aplicável.
6. Toda operação deve validar se o usuário pertence à empresa ativa.
7. Toda operação de obra deve validar papel/permissão na obra.
8. Todo módulo deve validar licença/permissão antes de executar.
9. Todo relatório/exportação deve respeitar empresa_id, obra_id e permissões.
10. Autocomplete, count, busca e exportação também devem respeitar multi-tenancy.

A ordem funcional obrigatória é:
1. Administrador prepara empresa, usuários, setores, permissões e licenças.
2. Usuário cria uma obra operacional.
3. Usuário cria base própria de insumos e composições, se necessário.
4. Usuário cria/importa orçamento da obra.
5. Usuário aprova uma versão do orçamento e marca como orçamento vigente da obra.
6. Usuário cria planejamento a partir do orçamento vigente.
7. Usuário registra execução no diário de obras.
8. Diário de obras atualiza acompanhamento do planejamento.
9. Compras cria pedidos usando itens do orçamento ou curva ABC.
10. Medição usa itens do orçamento para medir execução.
11. Fiscal aprova ou rejeita medição.
12. Relatórios e BI consolidam orçamento, planejamento, diário, compras e medição.

Não implemente cadastros soltos sem fluxo.
Não crie endpoint sem caso de uso associado.
Não implemente telas administrativas antes de definir o papel delas no fluxo.
Toda alteração crítica deve gerar auditoria.
Toda entidade aprovada/fechada deve ter bloqueio de edição ou versionamento.
Toda feature deve ter testes de isolamento multi-tenant.
```

---

# 11. Resumo executivo

Este sistema deve ser tratado como um **ciclo de vida da obra**, não como coleção de cadastros.

Ciclo principal:

```text
Configurar empresa
→ Criar obra
→ Orçar
→ Planejar
→ Executar
→ Registrar diário
→ Comprar
→ Medir
→ Aprovar
→ Relatar
```

Esse ciclo deve guiar:

```text
- Menu
- Backend
- APIs
- Banco de dados
- Permissões
- Licenças
- Tasks
- Testes
- Documentação
```

O backend bom não é aquele que possui muitas entidades.

O backend bom é aquele em que cada entidade, endpoint e tabela sustenta um fluxo operacional real, com isolamento multi-tenant forte e rastreabilidade completa.
