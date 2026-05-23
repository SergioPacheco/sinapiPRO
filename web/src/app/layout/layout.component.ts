import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ButtonModule, MenuModule],
  template: `
    <div class="layout-wrapper">
      <aside class="layout-sidebar">
        <div class="sidebar-brand"><span class="brand-icon">🏗️</span><span class="brand-text">SinapiPRO</span></div>
        <nav class="sidebar-nav">
          @for (item of menu; track item.route) {
            <a class="nav-item" [routerLink]="item.route" routerLinkActive="active">
              <i [class]="'pi pi-' + item.icon"></i><span>{{ item.label }}</span>
            </a>
          }
        </nav>
        <div class="sidebar-footer"><span class="text-muted" style="font-size:11px">v0.1.0</span></div>
      </aside>
      <div class="layout-main">
        <header class="layout-topbar">
          <span style="flex:1"></span>
          <i class="pi pi-search" style="cursor:pointer;color:var(--sp-text-muted)" title="Buscar (Ctrl+K)"></i>
          <i class="pi pi-bell" style="cursor:pointer;color:var(--sp-text-muted)" title="Notificações"></i>
          <div class="topbar-user" (click)="userMenu.toggle($event)">
            <span class="user-avatar">{{ initials() }}</span>
            <span class="user-name">{{ auth.user()?.name || 'Usuário' }}</span>
            <i class="pi pi-angle-down"></i>
          </div>
          <p-menu #userMenu [model]="userMenuItems" [popup]="true" />
        </header>
        <main class="layout-content"><router-outlet /></main>
      </div>
    </div>
  `,
  styles: [`
    .sidebar-brand { padding:1.25rem 1rem; display:flex; align-items:center; gap:0.5rem; border-bottom:1px solid var(--sp-border); }
    .brand-icon { font-size:1.4rem; } .brand-text { font-size:0.95rem; font-weight:700; }
    .sidebar-nav { flex:1; padding:0.75rem 0.5rem; display:flex; flex-direction:column; gap:2px; }
    .nav-item { display:flex; align-items:center; gap:0.75rem; padding:0.6rem 0.75rem; border-radius:6px; color:var(--sp-text-muted); text-decoration:none; font-size:13px; font-weight:500; transition:all 0.15s; }
    .nav-item:hover { background:var(--sp-surface-hover); color:var(--sp-text); }
    .nav-item.active { background:color-mix(in srgb, var(--sp-primary) 15%, transparent); color:var(--sp-primary); }
    .nav-item i { font-size:1rem; width:20px; text-align:center; }
    .sidebar-footer { padding:0.75rem 1rem; border-top:1px solid var(--sp-border); }
    .topbar-user { display:flex; align-items:center; gap:0.5rem; cursor:pointer; padding:0.25rem 0.5rem; border-radius:6px; }
    .topbar-user:hover { background:var(--sp-surface-hover); }
    .user-avatar { width:28px; height:28px; border-radius:50%; background:var(--sp-primary); color:white; display:flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; }
    .user-name { font-size:13px; font-weight:500; }
  `],
})
export class LayoutComponent {
  auth = inject(AuthService);

  menu = [
    { label: 'Dashboard', icon: 'th-large', route: '/dashboard' },
    { label: 'Obras', icon: 'building', route: '/projects' },
    { label: 'Composições', icon: 'book', route: '/sinapi' },
    { label: 'Relatórios', icon: 'chart-bar', route: '/reports' },
    { label: 'Configurações', icon: 'cog', route: '/settings' },
  ];

  userMenuItems = [
    { label: 'Perfil', icon: 'pi pi-user', routerLink: '/profile' },
    { separator: true },
    { label: 'Sair', icon: 'pi pi-sign-out', command: () => this.auth.logout() },
  ];

  initials() {
    const name = this.auth.user()?.name || '';
    return name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
  }
}
