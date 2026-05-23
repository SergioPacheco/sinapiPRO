import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

interface RegistryItem {
  label: string;
  route: string;
  icon: string;
  description: string;
  group: string;
  comingSoon?: boolean;
}

@Component({
  selector: 'app-registry-hub',
  standalone: true,
  imports: [FormsModule, RouterLink, MatIconModule, MatListModule, MatFormFieldModule, MatInputModule],
  template: `
    <div class="hub-header">
      <h1>Cadastros</h1>
      <mat-form-field appearance="outline" class="search-field">
        <mat-label>Buscar cadastro</mat-label>
        <input matInput [ngModel]="searchTerm()" (ngModelChange)="searchTerm.set($event)" placeholder="Digite para filtrar..." />
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>
    </div>
    <mat-nav-list>
      @for (item of filteredItems(); track item.route) {
        @if (item.comingSoon) {
          <mat-list-item disabled>
            <mat-icon matListItemIcon>{{ item.icon }}</mat-icon>
            <span matListItemTitle>{{ item.label }} <span class="badge-coming-soon">Em breve</span></span>
            <span matListItemLine>{{ item.description }}</span>
          </mat-list-item>
        } @else {
          <a mat-list-item [routerLink]="item.route">
            <mat-icon matListItemIcon>{{ item.icon }}</mat-icon>
            <span matListItemTitle>{{ item.label }}</span>
            <span matListItemLine>{{ item.description }}</span>
          </a>
        }
      }
    </mat-nav-list>
  `,
  styles: `
    .hub-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
    .hub-header h1 { margin: 0; font-size: 22px; font-weight: 600; }
    .search-field { width: 280px; }
    .badge-coming-soon {
      font-size: 10px; font-weight: 600; padding: 2px 6px; border-radius: 4px;
      background: var(--mat-sys-tertiary-container); color: var(--mat-sys-on-tertiary-container); margin-left: 8px;
    }
  `,
})
export class RegistryHubComponent {
  searchTerm = signal('');

  private readonly items: RegistryItem[] = [
    { label: 'Clientes', route: 'clients', icon: 'person', description: 'Clientes e contratantes', group: 'Pessoas' },
    { label: 'Fornecedores', route: '/suppliers', icon: 'store', description: 'Fornecedores de materiais e serviços', group: 'Pessoas' },
    { label: 'Funcionários', route: 'employees', icon: 'badge', description: 'Colaboradores e terceirizados', group: 'Pessoas' },
    { label: 'Equipes', route: 'teams', icon: 'groups', description: 'Composição de equipes de obra', group: 'Pessoas' },
    { label: 'Empreiteiros', route: 'contractors', icon: 'construction', description: 'Empreiteiros e subempreiteiros', group: 'Pessoas', comingSoon: true },
    { label: 'Fiscais', route: 'inspectors', icon: 'verified_user', description: 'Fiscais de obra e aprovadores', group: 'Pessoas', comingSoon: true },
    { label: 'Catálogo SINAPI', route: '/sinapi', icon: 'menu_book', description: 'Composições e insumos oficiais', group: 'Orçamento' },
    { label: 'Contas Bancárias', route: 'bank-accounts', icon: 'credit_card', description: 'Bancos e contas da empresa', group: 'Financeiro' },
    { label: 'Equipamentos', route: '/equipment', icon: 'precision_manufacturing', description: 'Máquinas e veículos', group: 'Obra' },
    { label: 'Checklists', route: '/safety/templates', icon: 'checklist', description: 'Modelos de inspeção', group: 'Segurança' },
    { label: 'Perfis e Permissões', route: '/settings/roles', icon: 'admin_panel_settings', description: 'Controle de acesso', group: 'Sistema' },
    { label: 'BDI', route: 'bdi', icon: 'calculate', description: 'Cadastro de BDI reutilizável (TCU)', group: 'Orçamento', comingSoon: true },
    { label: 'Encargos Sociais', route: 'social-charges', icon: 'percent', description: 'Horista, mensalista, Simples Nacional', group: 'Orçamento', comingSoon: true },
    { label: 'Unidades de Medida', route: 'units', icon: 'straighten', description: 'm, m², kg, un, vb...', group: 'Orçamento', comingSoon: true },
    { label: 'Centros de Custo', route: 'cost-centers', icon: 'account_tree', description: 'Estrutura de custos', group: 'Financeiro', comingSoon: true },
    { label: 'Relatórios', route: 'report-templates', icon: 'print', description: 'Personalização de relatórios', group: 'Sistema', comingSoon: true },
  ];

  filteredItems = computed(() => {
    const term = this.searchTerm().toLowerCase();
    if (!term) return this.items;
    return this.items.filter(i => i.label.toLowerCase().includes(term) || i.description.toLowerCase().includes(term));
  });
}
