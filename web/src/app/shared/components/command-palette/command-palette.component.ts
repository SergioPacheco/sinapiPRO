import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogModule, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';

interface PaletteAction {
  id: string;
  label: string;
  icon: string;
  route: string;
  keywords: string;
}

const ACTIONS: PaletteAction[] = [
  { id: 'new_project', label: 'Nova Obra', icon: 'add_business', route: '/projects/new', keywords: 'criar obra projeto new' },
  { id: 'projects', label: 'Listar Obras', icon: 'business', route: '/projects', keywords: 'obras projetos listar' },
  { id: 'new_budget', label: 'Novo Orçamento', icon: 'request_quote', route: '/projects', keywords: 'criar orçamento budget' },
  { id: 'suppliers', label: 'Fornecedores', icon: 'local_shipping', route: '/suppliers', keywords: 'fornecedor supplier' },
  { id: 'clients', label: 'Clientes', icon: 'people', route: '/registry/clients', keywords: 'cliente customer' },
  { id: 'employees', label: 'Funcionários', icon: 'badge', route: '/registry/employees', keywords: 'funcionário employee equipe' },
  { id: 'teams', label: 'Equipes', icon: 'groups', route: '/registry/teams', keywords: 'equipe team' },
  { id: 'sinapi', label: 'Catálogo SINAPI', icon: 'menu_book', route: '/sinapi/compositions', keywords: 'sinapi composição insumo' },
  { id: 'equipment', label: 'Equipamentos', icon: 'construction', route: '/equipment', keywords: 'equipamento máquina' },
  { id: 'safety', label: 'Segurança do Trabalho', icon: 'health_and_safety', route: '/safety', keywords: 'segurança safety inspeção' },
  { id: 'commercial', label: 'Comercial', icon: 'storefront', route: '/commercial', keywords: 'comercial proposta lead' },
  { id: 'aftersales', label: 'Pós-Venda', icon: 'support_agent', route: '/aftersales', keywords: 'pós-venda ticket garantia' },
  { id: 'analytics', label: 'Relatórios', icon: 'analytics', route: '/analytics', keywords: 'relatório analytics dashboard' },
  { id: 'settings', label: 'Configurações', icon: 'settings', route: '/settings', keywords: 'configuração settings perfil' },
  { id: 'finance', label: 'Financeiro Global', icon: 'account_balance', route: '/finance-global', keywords: 'financeiro pagar receber fluxo' },
  { id: 'procurement', label: 'Suprimentos Global', icon: 'shopping_cart', route: '/procurement-global', keywords: 'suprimentos compras pedido' },
];

@Component({
  selector: 'app-command-palette-dialog',
  standalone: true,
  imports: [FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatIconModule, MatListModule],
  template: `
    <div class="palette-container">
      <div class="palette-search">
        <mat-icon>search</mat-icon>
        <input type="text" [(ngModel)]="query" (ngModelChange)="filter()" placeholder="Buscar ação ou página..." autofocus
               (keydown.arrowdown)="moveDown()" (keydown.arrowup)="moveUp()" (keydown.enter)="execute()" />
        <span class="shortcut">ESC</span>
      </div>
      <mat-nav-list class="palette-results">
        @for (action of filtered; track action.id; let i = $index) {
          <a mat-list-item (click)="go(action)" [class.active]="i === selectedIndex">
            <mat-icon matListItemIcon>{{ action.icon }}</mat-icon>
            <span matListItemTitle>{{ action.label }}</span>
          </a>
        }
        @if (filtered.length === 0 && query) {
          <div class="no-results">Nenhum resultado para "{{ query }}"</div>
        }
      </mat-nav-list>
    </div>
  `,
  styles: `
    .palette-container { width: 520px; max-height: 420px; overflow: hidden; }
    .palette-search { display: flex; align-items: center; gap: 12px; padding: 16px 20px; border-bottom: 1px solid var(--mat-sys-outline-variant); }
    .palette-search input { flex: 1; border: none; outline: none; font-size: 16px; background: transparent; color: var(--mat-sys-on-surface); }
    .palette-search mat-icon { color: var(--mat-sys-outline); }
    .shortcut { font-size: 11px; padding: 2px 6px; border-radius: 4px; background: var(--mat-sys-surface-container-highest); color: var(--mat-sys-on-surface-variant); }
    .palette-results { max-height: 340px; overflow-y: auto; padding: 8px; }
    .palette-results a.active { background: var(--mat-sys-surface-container-highest); border-radius: 8px; }
    .no-results { text-align: center; padding: 24px; color: var(--mat-sys-on-surface-variant); }
  `,
})
export class CommandPaletteDialogComponent {
  private readonly router = inject(Router);
  private readonly dialogRef = inject(MatDialogRef<CommandPaletteDialogComponent>);

  query = '';
  filtered: PaletteAction[] = ACTIONS.slice(0, 8);
  selectedIndex = 0;

  filter() {
    const q = this.query.toLowerCase();
    this.filtered = q
      ? ACTIONS.filter(a => a.label.toLowerCase().includes(q) || a.keywords.includes(q))
      : ACTIONS.slice(0, 8);
    this.selectedIndex = 0;
  }

  moveDown() { this.selectedIndex = Math.min(this.selectedIndex + 1, this.filtered.length - 1); }
  moveUp() { this.selectedIndex = Math.max(this.selectedIndex - 1, 0); }

  execute() {
    if (this.filtered[this.selectedIndex]) this.go(this.filtered[this.selectedIndex]);
  }

  go(action: PaletteAction) {
    this.dialogRef.close();
    this.router.navigateByUrl(action.route);
  }
}

/**
 * Service that listens for Ctrl+K / ⌘K and opens the command palette.
 * Inject in the root layout component.
 */
@Component({
  selector: 'app-command-palette',
  standalone: true,
  imports: [],
  template: '',
})
export class CommandPaletteComponent implements OnInit, OnDestroy {
  private readonly dialog = inject(MatDialog);
  private listener!: (e: KeyboardEvent) => void;

  ngOnInit() {
    this.listener = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        this.open();
      }
    };
    document.addEventListener('keydown', this.listener);
  }

  ngOnDestroy() {
    document.removeEventListener('keydown', this.listener);
  }

  open() {
    this.dialog.open(CommandPaletteDialogComponent, {
      panelClass: 'command-palette-panel',
      position: { top: '15vh' },
      width: '560px',
      hasBackdrop: true,
    });
  }
}
