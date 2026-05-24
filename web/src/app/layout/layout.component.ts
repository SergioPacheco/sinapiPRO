import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../core/services/auth.service';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { DropdownModule } from 'primeng/dropdown';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, FormsModule, ButtonModule, MenuModule, DropdownModule],
  template: `
    <div class="layout-wrapper">
      <aside class="layout-sidebar">
        <div class="sidebar-brand"><span class="brand-icon">🏗️</span><span class="brand-text">SinapiPRO</span></div>

        <!-- Seletor de Obra (como no Strato) -->
        <div class="obra-selector">
          <label class="obra-label">OBRA</label>
          <p-dropdown [options]="obras()" [(ngModel)]="obraId" optionLabel="name" optionValue="id" placeholder="Selecionar obra..." [filter]="true" filterPlaceholder="Buscar..." styleClass="w-full obra-dropdown" (onChange)="onObraChange()" [showClear]="true" />
        </div>

        <nav class="sidebar-nav">
          <!-- Menu global (sem obra) -->
          <span class="nav-section">GERAL</span>
          <a class="nav-item" routerLink="/dashboard" routerLinkActive="active"><i class="pi pi-th-large"></i><span>Dashboard</span></a>
          <a class="nav-item" routerLink="/projects" routerLinkActive="active"><i class="pi pi-building"></i><span>Obras</span></a>
          <a class="nav-item" routerLink="/budgets" routerLinkActive="active"><i class="pi pi-calculator"></i><span>Orçamentos</span></a>
          <a class="nav-item" routerLink="/sinapi" routerLinkActive="active"><i class="pi pi-database"></i><span>SINAPI</span></a>
          <a class="nav-item" routerLink="/sinapi/materials" routerLinkActive="active"><i class="pi pi-box"></i><span>Insumos</span></a>

          <!-- Menu da obra selecionada -->
          @if (obraId) {
            <span class="nav-section">OBRA</span>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'summary']" routerLinkActive="active"><i class="pi pi-info-circle"></i><span>Resumo</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'contracts']" routerLinkActive="active"><i class="pi pi-file"></i><span>Contratos</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'measurements']" routerLinkActive="active"><i class="pi pi-check-square"></i><span>Medições</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'schedule']" routerLinkActive="active"><i class="pi pi-chart-line"></i><span>Cronograma</span></a>

            <span class="nav-section">SUPRIMENTOS</span>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'procurement']" routerLinkActive="active"><i class="pi pi-shopping-cart"></i><span>Requisições</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'procurement', 'quotations']" routerLinkActive="active"><i class="pi pi-comments"></i><span>Cotações</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'procurement', 'inventory']" routerLinkActive="active"><i class="pi pi-inbox"></i><span>Estoque</span></a>

            <span class="nav-section">FINANCEIRO</span>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'finance']" routerLinkActive="active"><i class="pi pi-dollar"></i><span>Contas a Pagar</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'finance', 'invoices']" routerLinkActive="active"><i class="pi pi-receipt"></i><span>Contas a Receber</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'finance', 'cash-flow']" routerLinkActive="active"><i class="pi pi-chart-bar"></i><span>Fluxo de Caixa</span></a>

            <span class="nav-section">EXECUÇÃO</span>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'daily-logs']" routerLinkActive="active"><i class="pi pi-calendar"></i><span>Diário de Obra</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'timesheets']" routerLinkActive="active"><i class="pi pi-clock"></i><span>Mão de Obra</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'safety']" routerLinkActive="active"><i class="pi pi-shield"></i><span>Segurança</span></a>
            <a class="nav-item" [routerLink]="['/projects', obraId, 'documents']" routerLinkActive="active"><i class="pi pi-folder"></i><span>Documentos</span></a>
          }

          <!-- Cadastros e Config -->
          <span class="nav-section">SISTEMA</span>
          <a class="nav-item" routerLink="/registry" routerLinkActive="active"><i class="pi pi-users"></i><span>Cadastros</span></a>
          <a class="nav-item" routerLink="/commercial" routerLinkActive="active"><i class="pi pi-briefcase"></i><span>Comercial</span></a>
          <a class="nav-item" routerLink="/settings" routerLinkActive="active"><i class="pi pi-cog"></i><span>Configurações</span></a>
        </nav>
        <div class="sidebar-footer"><span class="text-muted" style="font-size:10px">v0.1.0</span></div>
      </aside>
      <div class="layout-main">
        <header class="layout-topbar">
          @if (obraId) {
            <span class="topbar-obra">{{ obraNome() }}</span>
          }
          <span style="flex:1"></span>
          <i class="pi pi-bell" style="cursor:pointer;color:var(--sp-text-muted)"></i>
          <div class="topbar-user" (click)="userMenu.toggle($event)">
            <span class="user-avatar">{{ initials() }}</span>
            <span class="user-name">{{ auth.user()?.name || 'Usuário' }}</span>
          </div>
          <p-menu #userMenu [model]="userMenuItems" [popup]="true" />
        </header>
        <main class="layout-content"><router-outlet /></main>
      </div>
    </div>
  `,
  styles: [`
    .sidebar-brand { padding:1rem; display:flex; align-items:center; gap:0.5rem; border-bottom:1px solid var(--sp-border); }
    .brand-icon { font-size:1.3rem; } .brand-text { font-size:0.9rem; font-weight:700; }
    .obra-selector { padding:0.75rem 0.75rem 0; }
    .obra-label { font-size:9px; font-weight:700; color:var(--sp-text-muted); letter-spacing:0.5px; display:block; margin-bottom:4px; }
    :host ::ng-deep .obra-dropdown .p-dropdown { background:var(--sp-surface-hover); border-color:var(--sp-border); font-size:12px; }
    .sidebar-nav { flex:1; padding:0.5rem; display:flex; flex-direction:column; gap:1px; overflow-y:auto; }
    .nav-section { font-size:9px; font-weight:700; color:var(--sp-text-muted); letter-spacing:0.5px; padding:12px 8px 4px; }
    .nav-item { display:flex; align-items:center; gap:0.6rem; padding:0.45rem 0.6rem; border-radius:5px; color:var(--sp-text-muted); text-decoration:none; font-size:12px; font-weight:500; transition:all 0.12s; }
    .nav-item:hover { background:var(--sp-surface-hover); color:var(--sp-text); }
    .nav-item.active { background:color-mix(in srgb, var(--sp-primary) 15%, transparent); color:var(--sp-primary); }
    .nav-item i { font-size:0.85rem; width:18px; text-align:center; }
    .sidebar-footer { padding:0.5rem 0.75rem; border-top:1px solid var(--sp-border); }
    .topbar-obra { font-size:12px; font-weight:600; color:var(--sp-text); background:var(--sp-surface-hover); padding:4px 10px; border-radius:4px; }
    .topbar-user { display:flex; align-items:center; gap:0.4rem; cursor:pointer; padding:0.2rem 0.4rem; border-radius:5px; }
    .topbar-user:hover { background:var(--sp-surface-hover); }
    .user-avatar { width:26px; height:26px; border-radius:50%; background:var(--sp-primary); color:white; display:flex; align-items:center; justify-content:center; font-size:10px; font-weight:700; }
    .user-name { font-size:12px; font-weight:500; }
  `],
})
export class LayoutComponent implements OnInit {
  auth = inject(AuthService);
  private http = inject(HttpClient);
  private router = inject(Router);

  obras = signal<any[]>([]);
  obraId: string | null = null;

  userMenuItems = [
    { label: 'Perfil', icon: 'pi pi-user', routerLink: '/profile' },
    { separator: true },
    { label: 'Sair', icon: 'pi pi-sign-out', command: () => this.auth.logout() },
  ];

  ngOnInit() {
    this.http.get<any>('/projects?page=0&size=50').subscribe({
      next: res => this.obras.set(res.content || res),
    });
    // Restaurar obra selecionada
    this.obraId = localStorage.getItem('selectedObraId');
  }

  onObraChange() {
    if (this.obraId) {
      localStorage.setItem('selectedObraId', this.obraId);
    } else {
      localStorage.removeItem('selectedObraId');
    }
  }

  obraNome(): string {
    const obra = this.obras().find(o => o.id === this.obraId);
    return obra?.name || '';
  }

  initials() {
    const name = this.auth.user()?.name || '';
    return name.split(' ').map((w: string) => w[0]).join('').substring(0, 2).toUpperCase();
  }
}
