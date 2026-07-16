import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { LangSwitcherComponent } from '../shared/components/lang-switcher.component';
import { MenuModule } from 'primeng/menu';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [MenuModule, LangSwitcherComponent],
  template: `
    <header class="layout-topbar">
      <nav class="breadcrumb" aria-label="Localização">
        @for (seg of getBreadcrumb(); track $index) {
          @if ($index > 0) { <i class="pi pi-angle-right" style="font-size:10px;color:var(--sp-text-muted);margin:0 4px" aria-hidden="true"></i> }
          <span [style.color]="$last ? 'var(--sp-text)' : 'var(--sp-text-muted)'" style="font-size:12px">{{ seg }}</span>
        }
      </nav>
      @if (obraNome()) {
        <span class="topbar-obra">{{ obraNome() }}</span>
      }
      <span style="flex:1"></span>
      <app-lang-switcher />
      <button class="btn-icon-topbar" aria-label="Notificações" (click)="showNotifications = !showNotifications" [attr.aria-expanded]="showNotifications">
        <i class="pi pi-bell"></i>
      </button>
      @if (showNotifications) {
        <div class="notif-panel" role="region" aria-label="Painel de notificações" aria-live="polite">
          <strong style="font-size:11px;color:var(--sp-text-muted)">Notificações</strong>
          <div style="margin-top:8px;font-size:12px;color:var(--sp-text-muted)">Nenhuma notificação pendente</div>
        </div>
      }
      <div class="topbar-user" (click)="userMenu.toggle($event)">
        <span class="user-avatar">{{ initials() }}</span>
        <span class="user-name">{{ auth.user()?.name || 'Usuário' }}</span>
      </div>
      <p-menu #userMenu [model]="userMenuItems" [popup]="true" />
    </header>
  `,
})
export class TopbarComponent {
  auth = inject(AuthService);
  private router = inject(Router);

  obraNome = input<string>('');

  showNotifications = false;

  userMenuItems = [
    { label: 'Perfil', icon: 'pi pi-user', routerLink: '/profile' },
    { separator: true },
    { label: 'Sair', icon: 'pi pi-sign-out', command: () => this.auth.logout() },
  ];

  initials(): string {
    const name = this.auth.user()?.name || '';
    return name.split(' ').map((w: string) => w[0]).join('').substring(0, 2).toUpperCase();
  }

  getBreadcrumb(): string[] {
    const url = this.router.url;
    const map: Record<string, string> = {
      dashboard: 'Dashboard', projects: 'Obras', budgets: 'Orçamentos',
      sinapi: 'SINAPI', registry: 'Cadastros', settings: 'Configurações',
      contracts: 'Contratos', measurements: 'Medições', procurement: 'Suprimentos',
      finance: 'Financeiro', schedule: 'Cronograma', 'daily-logs': 'Diário',
      timesheets: 'Mão de Obra', safety: 'Segurança', documents: 'Documentos',
      summary: 'Resumo',
    };
    return url.split('/').filter(s => s && !s.match(/^[0-9a-f-]{36}$/)).map(s => map[s] || s).slice(0, 3);
  }
}
