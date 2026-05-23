import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';

interface RegistryItem {
  label: string;
  icon: string;
  route: string;
  description: string;
}

@Component({
  selector: 'app-registry',
  standalone: true,
  imports: [RouterLink, ButtonModule],
  template: `
    <h2 style="margin:0 0 1rem">Cadastros</h2>
    <div class="grid">
      @for (item of items; track item.route) {
        <div class="col-12 md:col-6 lg:col-4">
          <a [routerLink]="item.route" class="registry-item">
            <i [class]="'pi pi-' + item.icon" style="font-size:1.25rem; color:var(--sp-primary)"></i>
            <div>
              <div style="font-weight:600; font-size:13px">{{ item.label }}</div>
              <div class="text-muted" style="font-size:12px">{{ item.description }}</div>
            </div>
          </a>
        </div>
      }
    </div>
  `,
  styles: [`
    .registry-item {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.875rem 1rem;
      border-radius: var(--sp-radius);
      border: 1px solid var(--sp-border);
      background: var(--sp-surface-card);
      text-decoration: none;
      color: inherit;
      transition: border-color 0.15s;
    }
    .registry-item:hover { border-color: var(--sp-primary); }
  `],
})
export class RegistryComponent {
  items: RegistryItem[] = [
    { label: 'Clientes', icon: 'user', route: '/registry/clients', description: 'Clientes e contratantes' },
    { label: 'Fornecedores', icon: 'shop', route: '/registry/suppliers', description: 'Fornecedores de materiais' },
    { label: 'Funcionários', icon: 'id-card', route: '/registry/employees', description: 'Colaboradores e terceirizados' },
    { label: 'Equipes', icon: 'users', route: '/registry/teams', description: 'Composição de equipes' },
    { label: 'Equipamentos', icon: 'wrench', route: '/registry/equipment', description: 'Máquinas e veículos' },
    { label: 'Contas Bancárias', icon: 'credit-card', route: '/registry/bank-accounts', description: 'Bancos da empresa' },
  ];
}
