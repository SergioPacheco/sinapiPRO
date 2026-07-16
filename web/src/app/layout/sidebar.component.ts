import { Component, input, output, model } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, FormsModule, DropdownModule],
  template: `
    <aside class="layout-sidebar" role="navigation" aria-label="Menu principal">
      <div class="sidebar-brand">
        <span class="brand-icon">🏗️</span>
        @if (!collapsed()) { <span class="brand-text">SinapiPRO</span> }
        <button class="collapse-btn"
          [attr.aria-label]="collapsed() ? 'Expandir menu lateral' : 'Recolher menu lateral'"
          (click)="toggleCollapse.emit()">
          <i [class]="collapsed() ? 'pi pi-angle-right' : 'pi pi-angle-left'"></i>
        </button>
      </div>

      <div class="obra-selector">
        <label class="obra-label">OBRA</label>
        <p-dropdown
          [options]="obras()"
          [(ngModel)]="selectedObraId"
          optionLabel="name" optionValue="id"
          placeholder="Selecionar obra..."
          [filter]="true" filterPlaceholder="Buscar..."
          styleClass="w-full obra-dropdown"
          (onChange)="obraChange.emit(selectedObraId)"
          [showClear]="true" />
      </div>

      <nav class="sidebar-nav" aria-label="Navegação do sistema">
        <span class="nav-section" aria-hidden="true">GERAL</span>
        <a class="nav-item" routerLink="/dashboard" routerLinkActive="active" [attr.aria-label]="collapsed() ? 'Dashboard' : null"><i class="pi pi-th-large" aria-hidden="true"></i><span>Dashboard</span></a>
        <a class="nav-item" routerLink="/projects" routerLinkActive="active" [attr.aria-label]="collapsed() ? 'Obras' : null"><i class="pi pi-building" aria-hidden="true"></i><span>Obras</span></a>
        <a class="nav-item" routerLink="/budgets" routerLinkActive="active" [attr.aria-label]="collapsed() ? 'Orçamentos' : null"><i class="pi pi-calculator" aria-hidden="true"></i><span>Orçamentos</span></a>
        <a class="nav-item" routerLink="/sinapi" routerLinkActive="active" [attr.aria-label]="collapsed() ? 'SINAPI' : null"><i class="pi pi-database" aria-hidden="true"></i><span>SINAPI</span></a>

        @if (obraId()) {
          <span class="nav-section">OBRA</span>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'summary']" routerLinkActive="active"><i class="pi pi-info-circle"></i><span>Resumo</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'budgets']" routerLinkActive="active"><i class="pi pi-calculator"></i><span>Orçamentos</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'contracts']" routerLinkActive="active"><i class="pi pi-file"></i><span>Contratos</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'measurements']" routerLinkActive="active"><i class="pi pi-check-square"></i><span>Medições</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'schedule']" routerLinkActive="active"><i class="pi pi-chart-line"></i><span>Cronograma</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'daily-logs']" routerLinkActive="active"><i class="pi pi-calendar"></i><span>Diário de Obra</span></a>

          <span class="nav-section">SUPRIMENTOS</span>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'procurement']" routerLinkActive="active"><i class="pi pi-shopping-cart"></i><span>Compras</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'procurement', 'inventory']" routerLinkActive="active"><i class="pi pi-inbox"></i><span>Estoque</span></a>

          <span class="nav-section">FINANCEIRO</span>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'finance']" routerLinkActive="active"><i class="pi pi-dollar"></i><span>Contas</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'finance', 'cash-flow']" routerLinkActive="active"><i class="pi pi-chart-bar"></i><span>Fluxo de Caixa</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'job-costing']" routerLinkActive="active"><i class="pi pi-percentage"></i><span>Job Costing</span></a>

          <span class="nav-section">EXECUÇÃO</span>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'timesheets']" routerLinkActive="active"><i class="pi pi-clock"></i><span>Mão de Obra</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'safety']" routerLinkActive="active"><i class="pi pi-shield"></i><span>Segurança</span></a>
          <a class="nav-item" [routerLink]="['/projects', obraId(), 'documents']" routerLinkActive="active"><i class="pi pi-folder"></i><span>Documentos</span></a>
        }

        <span class="nav-section">EMPRESA</span>
        <a class="nav-item" routerLink="/finance" routerLinkActive="active"><i class="pi pi-wallet"></i><span>Financeiro</span></a>
        <a class="nav-item" routerLink="/commercial" routerLinkActive="active"><i class="pi pi-briefcase"></i><span>Comercial</span></a>
        <a class="nav-item" routerLink="/service-orders" routerLinkActive="active"><i class="pi pi-wrench"></i><span>Ordem Serviço</span></a>
        <a class="nav-item" routerLink="/equipment" routerLinkActive="active"><i class="pi pi-car"></i><span>Frota/Equip.</span></a>
        <a class="nav-item" routerLink="/reports" routerLinkActive="active"><i class="pi pi-print"></i><span>Relatórios</span></a>

        <span class="nav-section">CADASTROS</span>
        <a class="nav-item" routerLink="/registry" routerLinkActive="active"><i class="pi pi-users"></i><span>Pessoas</span></a>
        <a class="nav-item" routerLink="/registry/config" routerLinkActive="active"><i class="pi pi-sliders-h"></i><span>Tabelas</span></a>
        <a class="nav-item" routerLink="/settings" routerLinkActive="active"><i class="pi pi-cog"></i><span>Configurações</span></a>
      </nav>
      <div class="sidebar-footer"><span class="text-muted" style="font-size:10px">v0.1.0</span></div>
    </aside>
  `,
})
export class SidebarComponent {
  collapsed = input.required<boolean>();
  obras = input<any[]>([]);
  obraId = input<string | null>(null);

  toggleCollapse = output<void>();
  obraChange = output<string | null>();

  selectedObraId: string | null = null;

  ngOnInit() {
    this.selectedObraId = this.obraId() ?? null;
  }
}
