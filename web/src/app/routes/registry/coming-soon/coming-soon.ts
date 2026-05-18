import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-coming-soon',
  standalone: true,
  imports: [MatIconModule, PageHeader],
  template: `
    <page-header [title]="title" subtitle="Em desenvolvimento" />
    <div class="coming-soon">
      <mat-icon>construction</mat-icon>
      <h2>Em construção</h2>
      <p>Este cadastro estará disponível em breve.</p>
    </div>
  `,
  styles: `
    .coming-soon {
      display: flex; flex-direction: column; align-items: center; justify-content: center;
      padding: 80px 20px; text-align: center; color: var(--mat-sys-on-surface-variant);
      mat-icon { font-size: 64px; width: 64px; height: 64px; margin-bottom: 16px; opacity: 0.5; }
      h2 { margin: 0 0 8px; font-size: 20px; }
      p { margin: 0; font-size: 14px; }
    }
  `,
})
export class ComingSoonComponent {
  private readonly route = inject(ActivatedRoute);
  title = '';

  ngOnInit() {
    const labels: Record<string, string> = {
      'contractors': 'Empreiteiros',
      'inspectors': 'Fiscais',
      'bdi': 'BDI',
      'social-charges': 'Encargos Sociais',
      'units': 'Unidades de Medida',
      'payment-methods': 'Formas de Pagamento',
      'cost-centers': 'Centros de Custo',
      'finance-categories': 'Categorias Financeiras',
      'project-types': 'Tipos de Obra',
      'default-stages': 'Etapas Padrão',
      'incident-types': 'Tipos de Incidente',
      'epis': 'EPIs',
      'report-templates': 'Relatórios',
    };
    const path = this.route.snapshot.url[0]?.path || '';
    this.title = labels[path] || 'Cadastro';
  }
}
