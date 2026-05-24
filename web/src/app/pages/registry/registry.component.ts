import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-registry',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="registry-layout">
      <aside class="registry-sidebar">
        <div class="sidebar-title">Cadastros</div>
        @for (item of items; track item.route) {
          <a [routerLink]="item.route" routerLinkActive="active" class="sidebar-item">
            <i [class]="'pi pi-' + item.icon"></i>
            <span>{{ item.label }}</span>
          </a>
        }
      </aside>
      <main class="registry-content">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [`
    .registry-layout { display: flex; gap: 0; min-height: 500px; margin: -1.25rem 0; }
    .registry-sidebar { width: 200px; border-right: 1px solid var(--sp-border); padding: 1rem 0; flex-shrink: 0; }
    .sidebar-title { font-weight: 700; font-size: 13px; padding: 0 1rem 0.75rem; color: var(--sp-text-muted); text-transform: uppercase; }
    .sidebar-item { display: flex; align-items: center; gap: 0.5rem; padding: 0.5rem 1rem; font-size: 13px; color: var(--sp-text-muted); text-decoration: none; transition: all 0.15s; }
    .sidebar-item:hover { background: var(--sp-surface-hover); color: var(--sp-text); }
    .sidebar-item.active { color: var(--sp-primary); background: color-mix(in srgb, var(--sp-primary) 10%, transparent); border-right: 2px solid var(--sp-primary); }
    .sidebar-item i { font-size: 0.9rem; width: 18px; }
    .registry-content { flex: 1; padding: 1rem 1.5rem; overflow-y: auto; }
  `],
})
export class RegistryComponent {
  items = [
    { label: 'Clientes', icon: 'user', route: '/registry/clients' },
    { label: 'Fornecedores', icon: 'shop', route: '/registry/suppliers' },
    { label: 'Funcionários', icon: 'id-card', route: '/registry/employees' },
    { label: 'Equipamentos', icon: 'wrench', route: '/equipment' },
  ];
}
