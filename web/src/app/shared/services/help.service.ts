import { Injectable } from '@angular/core';

export interface HelpSection {
  id: string;
  title: string;
  icon: string;
  summary: string;
  content: HelpBlock[];
}

export interface HelpBlock {
  type: 'paragraph' | 'steps' | 'tip' | 'warning' | 'shortcut';
  text?: string;
  items?: string[];
}

@Injectable({ providedIn: 'root' })
export class HelpService {

  private readonly sections: HelpSection[] = [
    {
      id: 'getting-started',
      title: 'Primeiros Passos',
      icon: 'rocket_launch',
      summary: 'Como começar a usar o SinapiPRO',
      content: [
        { type: 'paragraph', text: 'O SinapiPRO é um ERP completo para gestão de obras da construção civil. O fluxo principal segue as fases: Captação → Planejamento → Execução → Encerramento → Pós-Obra.' },
        { type: 'steps', items: [
          'Crie uma obra (menu Obras → Nova Obra)',
          'Preencha o wizard: dados, cliente, equipe, datas',
          'Na aba Resumo, siga o checklist de planejamento',
          'Crie orçamento, cronograma e contratos',
          'Quando tudo estiver pronto, clique "Iniciar Execução"',
        ]},
        { type: 'tip', text: 'Use Ctrl+K (ou ⌘K no Mac) para navegar rapidamente a qualquer página ou obra.' },
      ],
    },
    {
      id: 'projects',
      title: 'Obras',
      icon: 'business',
      summary: 'Criar, gerenciar e acompanhar obras',
      content: [
        { type: 'paragraph', text: 'Cada obra é o centro do sistema. Dentro dela você gerencia orçamentos, contratos, medições, suprimentos e financeiro.' },
        { type: 'steps', items: [
          'Lista de obras: visualize em tabela ou Kanban (arraste cards para mudar status)',
          'Nova Obra: wizard guiado em 5 passos com preview',
          'Aba Resumo: dashboard com fases, checklist, KPIs e próximas ações',
          'Abas: cada módulo da obra é uma aba no workspace',
        ]},
        { type: 'tip', text: 'Badges nas abas indicam pendências (⚠️ medições para aprovar, orçamento faltando, etc.).' },
        { type: 'shortcut', text: 'Ctrl+K → "Nova Obra" para criar rapidamente.' },
      ],
    },
    {
      id: 'budgets',
      title: 'Orçamentos',
      icon: 'request_quote',
      summary: 'Composições SINAPI, BDI e Curva ABC',
      content: [
        { type: 'paragraph', text: 'O orçamento é a base financeira da obra. Use composições do catálogo SINAPI ou crie composições próprias.' },
        { type: 'steps', items: [
          'Crie um orçamento na aba Orçamentos da obra',
          'Abra a Planilha para adicionar etapas e itens',
          'Configure o BDI (Benefícios e Despesas Indiretas)',
          'Visualize a Curva ABC para priorizar compras',
          'Efetive o orçamento para torná-lo vigente',
        ]},
        { type: 'tip', text: 'Após criar o orçamento, o sistema sugere "Criar Cronograma" como próximo passo.' },
        { type: 'warning', text: 'Apenas um orçamento pode estar vigente por vez. Efetivar um novo substitui o anterior.' },
      ],
    },
    {
      id: 'contracts',
      title: 'Contratos',
      icon: 'description',
      summary: 'Gestão de contratos com fornecedores',
      content: [
        { type: 'paragraph', text: 'Contratos vinculam fornecedores à obra. O fluxo é: Rascunho → Ativo → Concluído.' },
        { type: 'steps', items: [
          'Crie um contrato na aba Contratos',
          'Use o lookup (🔍) para selecionar ou cadastrar fornecedor',
          'Defina valor, retenção e datas',
          'Ative o contrato quando estiver assinado',
          'Marque como concluído ao final',
        ]},
        { type: 'tip', text: 'Ao ativar um contrato, o sistema sugere ir ao Financeiro para gerar parcelas.' },
      ],
    },
    {
      id: 'schedule',
      title: 'Cronograma',
      icon: 'event_note',
      summary: 'Planejamento de atividades, CPM e Curva S',
      content: [
        { type: 'paragraph', text: 'O cronograma define as atividades da obra com datas, pesos e dependências. Acompanhe o progresso com a Curva S.' },
        { type: 'steps', items: [
          'Crie atividades com data início, fim e peso (%)',
          'Defina dependências entre atividades',
          'Atualize o progresso real periodicamente',
          'Visualize a Curva S (planejado vs realizado)',
          'Crie baselines para comparar versões do cronograma',
        ]},
      ],
    },
    {
      id: 'measurements',
      title: 'Medições',
      icon: 'straighten',
      summary: 'Workflow de medições: rascunho → aprovação → pagamento',
      content: [
        { type: 'paragraph', text: 'Medições registram o avanço físico-financeiro da obra. Seguem um workflow de aprovação.' },
        { type: 'steps', items: [
          'Crie uma medição com período e itens medidos',
          'Submeta para aprovação (botão Enviar)',
          'O aprovador pode aprovar ou rejeitar com motivo',
          'Medição aprovada gera automaticamente fatura e conta a receber',
          'Acompanhe no Kanban: Rascunho → Enviada → Aprovada → Paga',
        ]},
        { type: 'warning', text: 'Medições rejeitadas voltam para Rascunho com o motivo da rejeição.' },
      ],
    },
    {
      id: 'procurement',
      title: 'Suprimentos',
      icon: 'shopping_cart',
      summary: 'Requisição → Cotação → Pedido → Recebimento',
      content: [
        { type: 'paragraph', text: 'O módulo de suprimentos gerencia todo o ciclo de compras, desde a requisição até o recebimento no almoxarifado.' },
        { type: 'steps', items: [
          'Gere requisições a partir da Curva ABC do orçamento',
          'Crie cotações e envie para fornecedores',
          'Compare preços no mapa comparativo',
          'Gere pedido de compra com o melhor preço',
          'Registre o recebimento (estoque atualiza automaticamente)',
        ]},
        { type: 'tip', text: 'O stepper no topo mostra quantos itens estão em cada etapa do pipeline.' },
      ],
    },
    {
      id: 'daily-log',
      title: 'Diário de Obra',
      icon: 'edit_note',
      summary: 'Registro diário de atividades, clima e ocorrências',
      content: [
        { type: 'paragraph', text: 'O diário de obra registra o dia-a-dia: condições climáticas, mão de obra, equipamentos e ocorrências.' },
        { type: 'steps', items: [
          'Crie um registro para cada dia de trabalho',
          'Informe clima da manhã e tarde',
          'Se houve perda por chuva, registre horas perdidas',
          'O sistema calcula atrasos climáticos automaticamente',
        ]},
        { type: 'tip', text: 'Após registrar o diário, o sistema sugere "Apontar Horas" como próximo passo.' },
      ],
    },
    {
      id: 'finance',
      title: 'Financeiro',
      icon: 'account_balance',
      summary: 'Contas a pagar, receber, fluxo de caixa e notas fiscais',
      content: [
        { type: 'paragraph', text: 'O financeiro é alimentado automaticamente por medições (contas a receber) e contratos (contas a pagar).' },
        { type: 'steps', items: [
          'Contas a Pagar: geradas por contratos e pedidos',
          'Contas a Receber: geradas automaticamente por medições aprovadas',
          'Fluxo de Caixa: visão consolidada de entradas e saídas',
          'Notas Fiscais: vincule NFs a pagamentos/recebimentos',
        ]},
        { type: 'warning', text: 'Contas a receber são criadas automaticamente ao aprovar medições — não precisa cadastrar manualmente.' },
      ],
    },
    {
      id: 'safety',
      title: 'Segurança do Trabalho',
      icon: 'health_and_safety',
      summary: 'Inspeções, incidentes e checklists de segurança',
      content: [
        { type: 'paragraph', text: 'Gerencie a segurança da obra com inspeções periódicas, registro de incidentes e checklists.' },
        { type: 'steps', items: [
          'Crie templates de checklist de segurança',
          'Realize inspeções periódicas com score',
          'Registre incidentes com classificação de gravidade',
          'Acompanhe indicadores de segurança',
        ]},
      ],
    },
    {
      id: 'delivery',
      title: 'Entrega da Obra',
      icon: 'verified',
      summary: 'Wizard de encerramento: Punch List → Docs → Vistoria → Entrega',
      content: [
        { type: 'paragraph', text: 'O encerramento segue um wizard de 4 fases. Cada fase tem um checklist que deve ser 100% concluído antes de avançar.' },
        { type: 'steps', items: [
          'Fase 1 — Punch List: resolva todas as pendências',
          'Fase 2 — Documentação: entregue as-built, manuais, laudos',
          'Fase 3 — Vistoria: realize inspeção final com cliente',
          'Fase 4 — Entrega: emita recebimento provisório → definitivo',
        ]},
        { type: 'tip', text: 'Após a entrega definitiva, a obra muda automaticamente para status "Concluída".' },
      ],
    },
    {
      id: 'commercial',
      title: 'Comercial',
      icon: 'storefront',
      summary: 'Propostas comerciais e conversão em obras',
      content: [
        { type: 'paragraph', text: 'O módulo comercial gerencia propostas para clientes. Propostas aceitas podem ser convertidas em obras automaticamente.' },
        { type: 'steps', items: [
          'Crie uma proposta com título, cliente e valor',
          'Envie ao cliente (status: Enviada)',
          'Quando aceita, use o botão "Converter em Obra"',
          'O sistema cria a obra com os dados da proposta',
        ]},
      ],
    },
    {
      id: 'shortcuts',
      title: 'Atalhos de Teclado',
      icon: 'keyboard',
      summary: 'Navegação rápida pelo sistema',
      content: [
        { type: 'shortcut', text: 'Ctrl+K / ⌘K — Abrir Command Palette (busca rápida)' },
        { type: 'shortcut', text: 'F1 — Abrir ajuda contextual' },
        { type: 'shortcut', text: 'ESC — Fechar dialogs e painéis' },
        { type: 'paragraph', text: 'No Command Palette, digite o nome de uma obra, ação ou página para navegar instantaneamente.' },
      ],
    },
    {
      id: 'lookups',
      title: 'Cadastros Rápidos',
      icon: 'person_add',
      summary: 'Como cadastrar clientes, fornecedores e funcionários sem sair do fluxo',
      content: [
        { type: 'paragraph', text: 'Em qualquer formulário com campo de seleção (Cliente, Fornecedor, Engenheiro), você pode pesquisar e cadastrar sem sair da tela.' },
        { type: 'steps', items: [
          '🔍 Clique na lupa para pesquisar registros existentes',
          '➕ Clique no + para cadastrar um novo na hora',
          'O registro criado é automaticamente selecionado no campo',
          'Você não precisa ir ao menu Cadastros para isso',
        ]},
      ],
    },
  ];

  getAll(): HelpSection[] {
    return this.sections;
  }

  getById(id: string): HelpSection | undefined {
    return this.sections.find(s => s.id === id);
  }

  getByRoute(route: string): HelpSection | undefined {
    const map: Record<string, string> = {
      '/projects': 'projects',
      'summary': 'projects',
      'budgets': 'budgets',
      'contracts': 'contracts',
      'schedule': 'schedule',
      'measurements': 'measurements',
      'procurement': 'procurement',
      'daily-logs': 'daily-log',
      'finance': 'finance',
      'safety': 'safety',
      'delivery': 'delivery',
      'commercial': 'commercial',
    };
    for (const [key, sectionId] of Object.entries(map)) {
      if (route.includes(key)) return this.getById(sectionId);
    }
    return this.getById('getting-started');
  }

  search(query: string): HelpSection[] {
    const q = query.toLowerCase();
    return this.sections.filter(s =>
      s.title.toLowerCase().includes(q) ||
      s.summary.toLowerCase().includes(q) ||
      s.content.some(b => b.text?.toLowerCase().includes(q) || b.items?.some(i => i.toLowerCase().includes(q)))
    );
  }
}
