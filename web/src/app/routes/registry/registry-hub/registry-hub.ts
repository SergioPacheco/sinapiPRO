import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatRippleModule } from '@angular/material/core';

interface RegistryGroup {
  title: string;
  icon: string;
  items: { label: string; route: string; icon: string; description: string }[];
}

@Component({
  selector: 'app-registry-hub',
  standalone: true,
  imports: [RouterLink, MatIconModule, MatRippleModule],
  template: `
    <div class="hub-header">
      <h1>Cadastros</h1>
      <p>Gerencie os cadastros auxiliares do sistema</p>
    </div>
    <div class="hub-grid">
      @for (group of groups; track group.title) {
        <div class="hub-group">
          <div class="group-title">
            <mat-icon>{{ group.icon }}</mat-icon>
            <h3>{{ group.title }}</h3>
          </div>
          <div class="group-items">
            @for (item of group.items; track item.route) {
              <a class="group-item" [routerLink]="item.route" matRipple>
                <mat-icon>{{ item.icon }}</mat-icon>
                <div>
                  <span class="item-label">{{ item.label }}</span>
                  <span class="item-desc">{{ item.description }}</span>
                </div>
              </a>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: `
    .hub-header { margin-bottom: 24px; }
    .hub-header h1 { margin: 0; font-size: 22px; font-weight: 600; color: var(--mat-sys-on-surface); }
    .hub-header p { margin: 4px 0 0; font-size: 14px; color: var(--mat-sys-on-surface-variant); }

    .hub-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }

    .hub-group {
      background: var(--mat-sys-surface-container);
      border: 1px solid var(--mat-sys-outline-variant);
      border-radius: 12px;
      padding: 20px;
    }

    .group-title {
      display: flex; align-items: center; gap: 8px; margin-bottom: 14px;
      mat-icon { font-size: 20px; width: 20px; height: 20px; color: var(--mat-sys-primary); }
      h3 { margin: 0; font-size: 14px; font-weight: 600; color: var(--mat-sys-on-surface); text-transform: uppercase; letter-spacing: 0.5px; }
    }

    .group-items { display: flex; flex-direction: column; gap: 2px; }

    .group-item {
      display: flex; align-items: center; gap: 12px; padding: 10px 12px;
      border-radius: 8px; text-decoration: none; color: inherit;
      transition: background 0.15s;
      &:hover { background: var(--mat-sys-surface-container-highest); }
      mat-icon { font-size: 18px; width: 18px; height: 18px; color: var(--mat-sys-on-surface-variant); }
    }

    .item-label { display: block; font-size: 13px; font-weight: 500; color: var(--mat-sys-on-surface); }
    .item-desc { display: block; font-size: 11px; color: var(--mat-sys-on-surface-variant); margin-top: 1px; }
  `,
})
export class RegistryHubComponent {
  groups: RegistryGroup[] = [
    {
      title: 'Pessoas e Empresas',
      icon: 'people',
      items: [
        { label: 'Clientes', route: 'clients', icon: 'person', description: 'Clientes e contratantes' },
        { label: 'Fornecedores', route: '/suppliers', icon: 'store', description: 'Fornecedores de materiais e serviços' },
        { label: 'Funcionários', route: 'employees', icon: 'badge', description: 'Colaboradores e terceirizados' },
        { label: 'Empreiteiros', route: 'contractors', icon: 'construction', description: 'Empreiteiros e subempreiteiros' },
        { label: 'Fiscais', route: 'inspectors', icon: 'verified_user', description: 'Fiscais de obra e aprovadores' },
        { label: 'Equipes', route: 'teams', icon: 'groups', description: 'Composição de equipes de obra' },
      ],
    },
    {
      title: 'Orçamento',
      icon: 'request_quote',
      items: [
        { label: 'BDI', route: 'bdi', icon: 'calculate', description: 'Cadastro de BDI reutilizável (TCU)' },
        { label: 'Encargos Sociais', route: 'social-charges', icon: 'percent', description: 'Horista, mensalista, Simples Nacional' },
        { label: 'Catálogo SINAPI', route: '/sinapi', icon: 'menu_book', description: 'Composições e insumos oficiais' },
        { label: 'Unidades de Medida', route: 'units', icon: 'straighten', description: 'm, m², kg, un, vb...' },
      ],
    },
    {
      title: 'Financeiro',
      icon: 'account_balance',
      items: [
        { label: 'Contas Bancárias', route: 'bank-accounts', icon: 'credit_card', description: 'Bancos e contas da empresa' },
        { label: 'Formas de Pagamento', route: 'payment-methods', icon: 'payments', description: 'Condições de pagamento' },
        { label: 'Centros de Custo', route: 'cost-centers', icon: 'account_tree', description: 'Estrutura de custos' },
        { label: 'Categorias Financeiras', route: 'finance-categories', icon: 'category', description: 'Material, MO, equipamento, serviço' },
      ],
    },
    {
      title: 'Obra e Operação',
      icon: 'engineering',
      items: [
        { label: 'Equipamentos', route: '/equipment', icon: 'precision_manufacturing', description: 'Máquinas e veículos' },
        { label: 'Tipos de Obra', route: 'project-types', icon: 'domain', description: 'Residencial, comercial, industrial' },
        { label: 'Etapas Padrão', route: 'default-stages', icon: 'list_alt', description: 'Templates de etapas para orçamento' },
      ],
    },
    {
      title: 'Segurança do Trabalho',
      icon: 'health_and_safety',
      items: [
        { label: 'Checklists', route: '/safety/templates', icon: 'checklist', description: 'Modelos de inspeção' },
        { label: 'Tipos de Incidente', route: 'incident-types', icon: 'warning', description: 'Classificação de ocorrências' },
        { label: 'EPIs', route: 'epis', icon: 'shield', description: 'Equipamentos de proteção individual' },
      ],
    },
    {
      title: 'Sistema',
      icon: 'settings',
      items: [
        { label: 'Perfis e Permissões', route: '/settings/roles', icon: 'admin_panel_settings', description: 'Controle de acesso' },
        { label: 'Relatórios', route: 'report-templates', icon: 'print', description: 'Personalização de relatórios (logo, cores)' },
      ],
    },
  ];
}
