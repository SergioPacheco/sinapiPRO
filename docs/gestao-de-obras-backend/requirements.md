# Bounded Contexts sugeridos 

1. Identity & Licensing
   - Empresa
   - Usuário
   - Setor
   - Papel
   - Permissão
   - Licença
   - Plugin habilitado

2. Obras
   - Obra
   - Cliente
   - Responsável técnico
   - Fiscal
   - Empreiteiro
   - Membros da obra
   - Anexos

3. Orçamentos
   - Orçamento
   - Etapa
   - Item de orçamento
   - Composição
   - Insumo
   - Base de preço
   - BDI
   - Encargos sociais
   - Curva ABC
   - Relatórios
   - Tags
   - Lixeira

4. Base Própria
   - Banco próprio
   - Insumos próprios
   - Composições próprias
   - Importação Excel
   - Atualização de preços
   - Sincronização com orçamento

5. Planejamento
   - Planejamento
   - Atividade
   - Predecessor
   - Calendário de trabalho
   - Feriado
   - Jornada
   - Recurso
   - Acompanhamento

6. Diário de Obras
   - Diário diário
   - Registro climático
   - Registro de equipe
   - Tarefa executada
   - Foto
   - Entrada/saída de material
   - Ocorrência

7. Medição
   - Contrato/obra medida
   - Medição
   - Item medido
   - Memória de cálculo
   - Aditivo
   - Serviço não orçado
   - Aprovação fiscal
   - Relatórios

8. Compras
   - Pedido de compra
   - Item do pedido
   - Aprovação técnica
   - Cotação
   - Fornecedor
   - Proposta do fornecedor
   - Aprovação financeira
   - Ordem de compra

9. BIM / Quantificação
   - Projeto BIM
   - Elemento modelado
   - Elemento não modelado
   - Critério de quantificação
   - Quantidade extraída
   - Memória de cálculo
   - Integração Revit/Civil3D/IFC

10. Relatórios & BI
   - Modelo de relatório
   - Exportação PDF/Excel
   - Dashboard
   - Indicadores

# Requirements - Backend Sistema de Gestão de Obras

## Introdução

Este backend deverá suportar uma plataforma integrada de gestão de obras, contemplando orçamento, bases próprias de insumos e composições, planejamento, diário de obras, medição, compras, BIM, licenças, permissões e relatórios.

O sistema deve ser multiempresa, multiusuário, com controle de permissões por módulo, obra, papel e licença.

---

## Glossário

- Empresa: organização cliente que usa o sistema.
- Obra: empreendimento ou projeto de construção vinculado a uma empresa.
- Orçamento: estrutura de custos de uma obra, composta por etapas, composições e insumos.
- Etapa: agrupador hierárquico de itens do orçamento.
- Composição: serviço composto por insumos e coeficientes.
- Insumo: material, mão de obra, equipamento ou recurso utilizado em composições.
- Base oficial: base externa de preço, como SINAPI, SICRO ou outra base importada.
- Base própria: banco privado da empresa com insumos e composições próprios.
- BDI: Benefícios e Despesas Indiretas aplicados ao orçamento.
- Planejamento: cronograma de execução da obra.
- Diário de obra: registro diário das atividades, ocorrências, materiais, fotos e status da obra.
- Medição: apuração de quantidades executadas para controle físico-financeiro.
- Pedido de compra: solicitação de aquisição de materiais ou serviços.
- Cotação: processo de envio de itens a fornecedores para obtenção de preços.
- Ordem de compra: autorização final de compra enviada ao fornecedor.
- Fiscal: usuário responsável por fiscalizar e aprovar medições.
- Empreiteiro: fornecedor/prestador vinculado à execução de serviços.
- Responsável técnico: usuário que aprova tecnicamente pedidos, medições ou registros.
- Plugin: módulo adicional, como BIM, elétrico, hidráulico, estrutural ou BI.

---

## Requisitos Globais

### REQ-GLO-001 - Multiempresa

O sistema deve permitir que uma empresa possua usuários, obras, orçamentos, bases próprias, planejamentos, diários, medições, pedidos de compra, fornecedores e licenças.

#### Critérios de aceite

- Dado um usuário autenticado
- Quando ele consultar dados do sistema
- Então deve visualizar apenas dados das empresas às quais possui acesso

---

### REQ-GLO-002 - Permissões por módulo

O sistema deve controlar acesso por módulo, papel e licença.

#### Critérios de aceite

- Dado um usuário sem permissão para determinado módulo
- Quando tentar acessar uma API desse módulo
- Então o sistema deve retornar 403 Forbidden

---

### REQ-GLO-003 - Auditoria

Toda operação crítica deve gerar auditoria.

#### Operações críticas

- Criação/edição/exclusão de orçamento
- Ajuste de valores
- Aprovação de medição
- Aprovação de compra
- Envio de ordem de compra
- Alteração de permissões
- Exclusão definitiva
- Importação de planilhas

#### Critérios de aceite

- Dado que uma operação crítica foi executada
- Quando consultar o histórico
- Então devem existir usuário, data/hora, operação, entidade afetada e valores relevantes

---

### REQ-GLO-004 - Soft delete e lixeira

Entidades principais devem ser excluídas logicamente antes de exclusão definitiva.

#### Critérios de aceite

- Dado um orçamento excluído
- Quando o usuário acessar a lixeira
- Então o orçamento deve estar disponível para restauração, se ainda não removido definitivamente

---

### REQ-GLO-005 - Anexos

O sistema deve permitir anexar arquivos a obras, diários, medições, compras e projetos BIM.

#### Critérios de aceite

- Dado um arquivo válido
- Quando anexado a uma entidade
- Então deve ser armazenado com metadados de nome, tipo, tamanho, usuário e data

---

## Módulo Administrar Licença

### REQ-LIC-001 - Cadastro de usuários na empresa

O administrador deve conseguir inserir usuários novos ou existentes em uma empresa.

#### Critérios de aceite

- Dado um administrador da empresa
- Quando informar e-mail de usuário existente
- Então o usuário deve ser vinculado à empresa
- E deve receber permissões conforme configuração inicial

---

### REQ-LIC-002 - Remoção de usuário da empresa

O administrador deve remover um usuário da empresa sem apagar seus dados globais.

#### Critérios de aceite

- Dado um usuário vinculado à empresa
- Quando o administrador removê-lo
- Então o vínculo deve ser encerrado
- E o histórico/auditoria deve permanecer preservado

---

### REQ-LIC-003 - Setores

O administrador deve criar, editar e inativar setores.

#### Critérios de aceite

- Dado um setor ativo
- Quando associado a usuários
- Então pode ser usado para organização e permissão

---

### REQ-LIC-004 - Permissões de usuário

O administrador deve configurar permissões de acesso a módulos, plugins e conteúdos.

#### Critérios de aceite

- Dado um usuário sem acesso ao plugin OF Elétrico
- Quando tentar autenticar no plugin
- Então o sistema deve negar acesso

---

### REQ-LIC-005 - Encargos sociais

O sistema deve permitir cadastro de encargos sociais por empresa.

#### Critérios de aceite

- Dado um encargo social cadastrado
- Quando utilizado em orçamento
- Então deve impactar os cálculos conforme fórmula definida

---

## Módulo Obras

### REQ-OBR-001 - Cadastro de obra

O sistema deve permitir cadastrar obras para vinculação com orçamento, planejamento, diário, medição e compras.

#### Campos mínimos

- Nome
- Código interno
- Cliente
- Endereço
- Cidade/UF
- Data de início prevista
- Data de fim prevista
- Responsável técnico
- Status

#### Critérios de aceite

- Dado uma obra cadastrada
- Quando criar orçamento, planejamento, medição, diário ou pedido
- Então a obra deve estar disponível para vínculo

---

### REQ-OBR-002 - Membros da obra

A obra deve permitir membros com papéis diferentes.

#### Papéis sugeridos

- Administrador da obra
- Responsável técnico
- Fiscal
- Empreiteiro
- Comprador
- Financeiro
- Leitor

---

### REQ-OBR-003 - Fiscais

O sistema deve permitir cadastrar fiscais internos e externos.

#### Critérios de aceite

- Dado um fiscal cadastrado
- Quando uma medição exigir aprovação
- Então o fiscal poderá aprovar ou rejeitar conforme permissão

---

### REQ-OBR-004 - Empreiteiros

A obra deve permitir cadastro e vínculo de empreiteiros.

#### Critérios de aceite

- Dado um empreiteiro vinculado
- Quando criar medição ou diário
- Então ele pode ser associado a serviços executados

---

## Módulo Orçamentos

### REQ-ORC-001 - Criar orçamento

O usuário deve criar orçamento vinculado a empresa e opcionalmente a uma obra.

#### Campos mínimos

- Descrição
- Obra
- Data base
- Método de arredondamento geral
- Bases de preço selecionadas
- BDI
- Encargos sociais

---

### REQ-ORC-002 - Etapas

O orçamento deve possuir etapas hierárquicas.

#### Critérios de aceite

- Dado um orçamento
- Quando o usuário criar uma etapa
- Então ela deve aceitar nome, ordem, código/máscara e etapa pai opcional

---

### REQ-ORC-003 - Máscara de item

O orçamento deve permitir código customizado por máscara de item.

#### Critérios de aceite

- Dado um orçamento com máscara ativa
- Quando criar etapa ou item
- Então o código deve respeitar a máscara configurada

---

### REQ-ORC-004 - Adicionar composição oficial

O usuário deve adicionar composições de bases oficiais ao orçamento.

#### Critérios de aceite

- Dado uma base de preço selecionada
- Quando pesquisar composição por código ou descrição
- Então o sistema deve retornar composições compatíveis com a base/data

---

### REQ-ORC-005 - Adicionar composição própria

O usuário deve adicionar composições da base própria ao orçamento.

#### Critérios de aceite

- Dado uma composição própria cadastrada
- Quando o usuário selecionar banco "Próprio"
- Então poderá informar quantidade e adicionar ao orçamento

---

### REQ-ORC-006 - Adicionar insumo próprio

O usuário deve adicionar insumos próprios diretamente ao orçamento.

#### Critérios de aceite

- Dado um insumo próprio cadastrado
- Quando o usuário selecionar insumo próprio
- Então poderá informar quantidade, unidade e preço

---

### REQ-ORC-007 - Quantidade do item

Todo item de orçamento deve possuir quantidade válida.

#### Critérios de aceite

- Dado um item de orçamento
- Quando a quantidade for menor ou igual a zero
- Então o sistema deve rejeitar a operação

---

### REQ-ORC-008 - Cálculo de item

O sistema deve calcular total do item a partir de quantidade, custo unitário, BDI e regras de arredondamento.

#### Fórmula base

totalItem = quantidade * precoUnitarioComBDI

---

### REQ-ORC-009 - Arredondamento geral

O orçamento deve possuir método de arredondamento geral.

---

### REQ-ORC-010 - Arredondamento por base

O sistema deve permitir sobrescrever o arredondamento geral por base de preço.

#### Critérios de aceite

- Dado um orçamento com múltiplas bases
- Quando uma base possuir método próprio de arredondamento
- Então os itens dessa base devem usar esse método em seus cálculos

---

### REQ-ORC-011 - Ajuste linear do orçamento

O sistema deve permitir desconto ou acréscimo linear no orçamento.

#### Modos

1. Preços e coeficientes
2. Apenas coeficientes

#### Regras

- No modo preços e coeficientes:
  - Alterar preços unitários dos insumos
  - Não alterar preço unitário de insumos de mão de obra
  - Alterar coeficientes de mão de obra
- No modo apenas coeficientes:
  - Alterar apenas coeficientes de mão de obra

#### Critérios de aceite

- Dado um orçamento
- Quando aplicar ajuste linear confirmado
- Então o sistema deve gravar novo cenário de preços
- E deve registrar auditoria
- E não deve permitir restauração automática sem snapshot prévio

---

### REQ-ORC-012 - Prévia do ajuste

Antes de confirmar o ajuste linear, o sistema deve exibir prévia.

#### Critérios de aceite

- Dado um ajuste configurado
- Quando solicitar prévia
- Então o sistema deve retornar valor antes, valor depois, percentual aplicado e itens críticos

---

### REQ-ORC-013 - Itens críticos do ajuste

O sistema deve identificar composições que não sofreram alteração ou que exigem ajuste fino.

#### Situações

- Composição com mão de obra fora do primeiro nível
- Composição em que valor de mão de obra é menor que o desconto

---

### REQ-ORC-014 - Tags

O usuário deve marcar itens do orçamento com tags.

---

### REQ-ORC-015 - Duplicação de itens

O usuário deve duplicar etapas, composições ou insumos dentro do orçamento.

---

### REQ-ORC-016 - Recuperação de itens excluídos

Itens excluídos devem poder ser recuperados antes da exclusão definitiva.

---

### REQ-ORC-017 - Alteração de data base

O sistema deve permitir atualizar data base de orçamento já criado.

#### Critérios de aceite

- Dado um orçamento com itens de base oficial
- Quando alterar data base
- Então os preços devem ser recalculados ou marcados como divergentes

---

### REQ-ORC-018 - Divergência de data

O sistema deve sinalizar itens cuja data da base diverge da data base do orçamento.

---

### REQ-ORC-019 - Curva ABC

O sistema deve gerar curva ABC de insumos e serviços.

#### Critérios de aceite

- Dado um orçamento calculado
- Quando gerar curva ABC
- Então os itens devem ser ordenados por representatividade financeira

---

### REQ-ORC-020 - Relatórios

O sistema deve gerar relatórios sintéticos, analíticos, curva ABC e relatórios personalizados.

---

## Módulo Base Própria

### REQ-BAS-001 - Criar banco próprio

A empresa deve possuir banco próprio de insumos e composições.

---

### REQ-BAS-002 - Criar insumo manual

O usuário deve criar insumo próprio manualmente.

#### Campos mínimos

- Código
- Descrição
- Unidade
- Tipo
- Valor
- Data base

---

### REQ-BAS-003 - Importar insumos via Excel

O sistema deve importar insumos próprios a partir de planilha.

#### Critérios de aceite

- Dado arquivo XLSX válido
- Quando importado
- Então os insumos devem ser criados ou rejeitados com relatório de erros

---

### REQ-BAS-004 - Atualizar preços de insumos existentes

O sistema deve atualizar preços de insumos próprios existentes via planilha.

---

### REQ-BAS-005 - Criar composição própria

O usuário deve criar composição própria.

#### Campos mínimos

- Código
- Descrição
- Unidade
- Itens da composição
- Coeficientes
- Preço total calculado

---

### REQ-BAS-006 - Copiar composição existente

O usuário deve copiar uma composição existente para a base própria e editá-la.

---

### REQ-BAS-007 - Importar composições via Excel

O sistema deve importar composições próprias via planilha.

---

### REQ-BAS-008 - Sincronizar orçamento com base própria

Quando preço de item próprio mudar na base, o sistema deve permitir atualizar itens já usados no orçamento.

#### Critérios de aceite

- Dado um orçamento com itens próprios
- Quando o usuário solicitar atualização
- Então o sistema deve recalcular os itens selecionados ou todos os itens próprios

---

## Módulo Planejamento

### REQ-PLA-001 - Criar planejamento

O usuário deve criar planejamento vinculado a uma obra.

#### Campos mínimos

- Descrição
- Obra
- Data de início
- Jornada diária
- Jornada extra
- Considerar sábado
- Considerar domingo
- Considerar feriado
- Tipos de recursos considerados
- Inicializar com cronograma do orçamento
- Considerar itens agregados como distintos
- Considerar produção de equipe
- Habilitar sugestão de duração

---

### REQ-PLA-002 - Calendário de trabalho

O planejamento deve possuir calendário de trabalho com dias úteis, sábados, domingos e feriados.

---

### REQ-PLA-003 - Feriados

O usuário deve cadastrar feriados ou dias não trabalhados no planejamento.

---

### REQ-PLA-004 - Atividades

O planejamento deve possuir atividades derivadas do orçamento ou criadas manualmente.

---

### REQ-PLA-005 - Duração de atividades

O sistema deve permitir definir duração das atividades.

---

### REQ-PLA-006 - Sugestão de duração

Quando habilitado, o sistema deve sugerir duração baseada em coeficientes, produção de equipe e jornada.

---

### REQ-PLA-007 - Predecessores

O sistema deve permitir definir relação de dependência entre atividades.

#### Tipos sugeridos

- Término-Início
- Início-Início
- Término-Término
- Início-Término

---

### REQ-PLA-008 - Atualizar planejamento após alteração no orçamento

O sistema deve permitir sincronizar o planejamento quando o orçamento for alterado.

---

### REQ-PLA-009 - Acompanhamento

O sistema deve mostrar status e progresso das atividades.

#### Fonte de atualização

- Diário de obras

#### Critérios de aceite

- Dado uma tarefa vinculada ao diário
- Quando o responsável técnico alterar status ou progresso
- Então o acompanhamento do planejamento deve refletir a alteração

---

## Módulo Diário de Obras

### REQ-DIA-001 - Criar diário

O usuário deve criar diário vinculado a uma obra e data.

#### Campos mínimos

- Data
- Obra
- Copiar informações do último relatório

---

### REQ-DIA-002 - Um diário por obra/data

O sistema deve impedir duplicidade de diário para a mesma obra e data, salvo se a regra da empresa permitir múltiplos turnos.

---

### REQ-DIA-003 - Copiar último relatório

Quando selecionado, o diário deve iniciar com informações do último relatório da mesma obra.

---

### REQ-DIA-004 - Registrar atividades

O diário deve registrar atividades executadas no dia.

---

### REQ-DIA-005 - Vincular tarefas ao planejamento

O diário deve permitir adicionar tarefas de acordo com o planejamento.

#### Critérios de aceite

- Dado uma atividade planejada
- Quando registrada no diário
- Então deve atualizar o acompanhamento do planejamento

---

### REQ-DIA-006 - Status de tarefa

O diário deve permitir informar status da tarefa.

#### Status sugeridos

- Não iniciada
- Em andamento
- Parada
- Cancelada
- Concluída

---

### REQ-DIA-007 - Progresso

O diário deve permitir informar progresso em unidade e percentual.

---

### REQ-DIA-008 - Entrada e saída de materiais

O diário deve registrar movimentação de materiais.

#### Campos mínimos

- Material
- Tipo de movimento
- Quantidade
- Unidade
- Origem/destino
- Observação

---

### REQ-DIA-009 - Relatório fotográfico

O diário deve permitir inserir fotos no relatório fotográfico.

---

### REQ-DIA-010 - Relatórios do diário

O sistema deve emitir relatório do diário de obra por período, obra e responsável.

---

### REQ-DIA-011 - Validação de período

Relatórios por período devem rejeitar datas inválidas.

---

## Módulo Medição

### REQ-MED-001 - Criar medição de obra

O sistema deve permitir criar medição vinculada a uma obra.

#### Campos mínimos

- Obra
- Período inicial
- Período final
- Descrição
- Responsável
- Status

---

### REQ-MED-002 - Itens medidos

A medição deve conter itens orçados, serviços não orçados e aditivos.

---

### REQ-MED-003 - Importar planilha de medição

O sistema deve importar itens de medição via planilha.

---

### REQ-MED-004 - Serviço não orçado

O usuário deve adicionar serviço não previsto no orçamento.

#### Critérios de aceite

- Dado uma medição aberta
- Quando adicionar serviço não orçado
- Então o item deve ser marcado como extra e exigir aprovação

---

### REQ-MED-005 - Aditivo

O sistema deve permitir adicionar aditivo à medição.

---

### REQ-MED-006 - Memória de cálculo

Cada item medido pode possuir memória de cálculo.

#### Critérios de aceite

- Dado um item medido
- Quando editar memória de cálculo
- Então quantidade medida deve ser recalculada se a fórmula for alterada

---

### REQ-MED-007 - Empreiteiro na medição

Itens ou medições podem ser associados a empreiteiro.

---

### REQ-MED-008 - Medição editável

A medição só deve ser editável em status permitidos.

#### Status sugeridos

- Rascunho
- Enviada para aprovação
- Aprovada
- Rejeitada
- Cancelada
- Fechada

#### Critérios de aceite

- Dado uma medição aprovada
- Quando tentar editar item medido
- Então o sistema deve bloquear alteração

---

### REQ-MED-009 - Solicitar aprovação

O usuário deve solicitar aprovação de medição.

---

### REQ-MED-010 - Aprovar medição

Fiscal autorizado deve aprovar medição.

#### Critérios de aceite

- Dado uma medição enviada
- Quando fiscal aprovar
- Então status deve mudar para Aprovada
- E deve gerar auditoria

---

### REQ-MED-011 - Rejeitar medição

Fiscal autorizado deve rejeitar medição com justificativa.

---

### REQ-MED-012 - Relatórios de medição

O sistema deve emitir relatórios de medição.

---

## Módulo Compras

### REQ-COM-001 - Criar pedido

O usuário deve criar pedido de compra vinculado a obra.

#### Campos mínimos

- Data do pedido
- Descrição
- Obra
- Requerente

---

### REQ-COM-002 - Adicionar itens do orçamento

Ao digitar descrição, o sistema deve sugerir itens do orçamento da obra.

#### Critérios de aceite

- Dado item pertencente ao orçamento
- Quando selecionado
- Então unidade, quantidade orçada e saldo devem ser preenchidos automaticamente

---

### REQ-COM-003 - Adicionar item não orçado

O usuário pode adicionar item não pertencente ao orçamento.

#### Critérios de aceite

- Dado item não orçado
- Quando adicionado ao pedido
- Então unidade pode ser preenchida
- Mas quantidade orçada e saldo não devem ser preenchidos automaticamente

---

### REQ-COM-004 - Salvar e aprovar pelo requerente

O requerente deve salvar e enviar pedido para aprovação técnica.

---

### REQ-COM-005 - Aprovação do responsável técnico

O responsável técnico deve aprovar ou rejeitar o pedido.

---

### REQ-COM-006 - Envio ao setor de compras

Após aprovação técnica, o pedido deve ir para o setor de compras.

---

### REQ-COM-007 - Fornecedores

O setor de compras deve selecionar fornecedores existentes ou cadastrar novos.

---

### REQ-COM-008 - Frete

O setor de compras deve adicionar frete por fornecedor.

---

### REQ-COM-009 - Envio de cotação

O setor de compras deve enviar pedido de cotação para fornecedores.

#### Campos

- Itens selecionados
- Forma de pagamento
- Data de entrega desejada
- Observação até 600 caracteres

---

### REQ-COM-010 - Cotação por link externo

O fornecedor deve poder preencher cotação por link seguro.

---

### REQ-COM-011 - Recebimento da cotação

Quando fornecedor enviar cotação, os valores e descontos devem ser preenchidos automaticamente no sistema.

---

### REQ-COM-012 - Escolha do fornecedor

O usuário deve selecionar itens mais baratos ou fornecedor mais barato.

---

### REQ-COM-013 - Envio ao financeiro

Após seleção de cotação, o pedido deve ir para análise financeira.

---

### REQ-COM-014 - Aprovação financeira

Financeiro deve aprovar e gerar ordem de compra.

---

### REQ-COM-015 - Ordem de compra

O sistema deve enviar ordem de compra ao fornecedor.

---

### REQ-COM-016 - Cotação não editável

Cotação não deve ser editável após envio, aprovação ou fechamento, conforme status.

---

## Módulo BIM / Quantificação

### REQ-BIM-001 - Criar projeto BIM

O sistema deve permitir criar projeto BIM vinculado a obra e orçamento.

---

### REQ-BIM-002 - Quantificar elementos modelados

O sistema deve receber quantidades de elementos modelados vindos de plugin BIM.

---

### REQ-BIM-003 - Quantificar elementos não modelados

O sistema deve permitir quantificação de elementos não modelados.

---

### REQ-BIM-004 - Critérios de quantificação

O sistema deve permitir criar e reaproveitar critérios de quantificação.

---

### REQ-BIM-005 - Memória de cálculo BIM

Cada quantidade extraída deve possuir memória de cálculo.

---

### REQ-BIM-006 - Elementos não orçados

O sistema deve identificar elementos BIM sem vínculo com item orçamentário.

---

### REQ-BIM-007 - Importação/exportação IFC

O sistema deve suportar registro de importações/exportações IFC.

---

### REQ-BIM-008 - Civil 3D

O sistema deve suportar quantificação de:
- Pipe Networks
- Superfície
- Corte e aterro
- Corredores

---

## Módulo OF Elétrico

### REQ-ELE-001 - Permissão de plugin

Usuário só pode acessar OF Elétrico se possuir permissão.

---

### REQ-ELE-002 - Avisos de roteamento

O sistema deve receber e armazenar avisos de roteamento do plugin.

#### Tipos

- Dispositivo sem classificação de carga
- Circuito com várias classificações
- Sistema de interruptor inconsistente
- Sistema órfão
- Dispositivo fora de circuito
- Circuito inconsistente
- Infraestrutura desconectada
- Infraestrutura corrompida

---

## Módulo OF Estrutural

### REQ-EST-001 - Configurações do plugin

O sistema deve armazenar configurações avançadas do plugin estrutural.

---

### REQ-EST-002 - Ajuste de projeto

O sistema deve registrar comandos de ajuste automático de armaduras e elementos estruturais.

---

### REQ-EST-003 - Vistas e pranchas

O sistema deve registrar criação/verificação de vistas e pranchas estruturais.

---

### REQ-EST-004 - Repetições estruturais

O sistema deve permitir agrupar elementos estruturais equivalentes.

---

## Módulo Relatórios / BI

### REQ-REL-001 - Modelos de relatório

O sistema deve permitir selecionar modelos de relatório.

---

### REQ-REL-002 - Personalização

Relatórios devem permitir personalização de logo, cabeçalho, rodapé e cores.

---

### REQ-REL-003 - Exportações

Relatórios devem ser exportáveis em PDF e Excel quando aplicável.

---

### REQ-REL-004 - Indicadores

O backend deve disponibilizar indicadores para BI.

#### Indicadores mínimos

- Valor total orçado
- Valor medido
- Valor comprado
- Saldo financeiro da obra
- Progresso planejado
- Progresso realizado
- Curva ABC
- Pedidos por status
- Medições por status
- Diários por período

