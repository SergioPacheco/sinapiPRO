import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-project-summary',
  standalone: true,
  imports: [DecimalPipe, RouterLink, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <div>
        <h2 style="margin:0;color:var(--sp-text)">{{ project().name }}</h2>
        <span style="font-size:12px;color:var(--sp-text-muted)">{{ project().code }} | {{ project().clientName }}</span>
      </div>
      <sp-status [status]="project().status || 'PLANNING'" />
    </div>

    <!-- KPIs da Obra -->
    <div class="kpi-grid">
      <div class="kpi-card"><span class="kpi-label">Valor Orçado</span><strong class="kpi-value">{{ project().budgetValue | number:'1.0-0' }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Área (m²)</span><strong class="kpi-value">{{ project().totalArea | number:'1.0-0' }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Início</span><strong class="kpi-value" style="font-size:14px">{{ project().startDate }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Término</span><strong class="kpi-value" style="font-size:14px">{{ project().endDate }}</strong></div>
    </div>

    <!-- Orçamentos -->
    <div class="section">
      <div class="flex align-items-center justify-content-between mb-2">
        <h3>Orçamentos</h3>
        <a [routerLink]="['../budgets']" style="font-size:12px;color:var(--sp-primary)">Ver todos →</a>
      </div>
      @for (b of budgets(); track b.id) {
        <div class="item-card">
          <div class="flex align-items-center justify-content-between">
            <div>
              <strong style="font-size:12px">{{ b.code }}</strong>
              <span style="font-size:12px;color:var(--sp-text-muted);margin-left:8px">{{ b.title }}</span>
            </div>
            <div class="flex align-items-center gap-2">
              <span class="font-mono" style="font-size:12px">{{ b.totalAmount | number:'1.2-2' }}</span>
              <sp-status [status]="b.status" />
              <a [routerLink]="['/budgets', b.id]" style="color:var(--sp-primary)"><i class="pi pi-arrow-right" style="font-size:12px"></i></a>
            </div>
          </div>
        </div>
      }
    </div>

    <!-- Últimas Medições -->
    @if (measurements().length > 0) {
      <div class="section">
        <h3>Últimas Medições</h3>
        @for (m of measurements(); track m.id) {
          <div class="item-card">
            <div class="flex align-items-center justify-content-between">
              <span style="font-size:12px">Medição #{{ m.number }} ({{ m.periodStart }} — {{ m.periodEnd }})</span>
              <div class="flex align-items-center gap-2">
                <span class="font-mono" style="font-size:12px">{{ m.measuredValue | number:'1.2-2' }}</span>
                <sp-status [status]="m.status" />
              </div>
            </div>
          </div>
        }
      </div>
    }
  `,
  styles: [`
    .kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 24px; }
    .kpi-card { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 8px; padding: 14px; }
    .kpi-label { display: block; font-size: 10px; color: var(--sp-text-muted); text-transform: uppercase; margin-bottom: 4px; }
    .kpi-value { font-size: 20px; color: var(--sp-text); }
    .section { margin-bottom: 20px; }
    .section h3 { font-size: 13px; color: var(--sp-text-muted); margin: 0 0 8px; }
    .item-card { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 6px; padding: 10px 14px; margin-bottom: 6px; }
  `],
})
export class ProjectSummaryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  project = signal<any>({});
  budgets = signal<any[]>([]);
  measurements = signal<any[]>([]);

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}`).subscribe({ next: r => this.project.set(r) });
    this.http.get<any>(`/projects/${this.pid}/budgets`).subscribe({ next: r => this.budgets.set((r.content || r || []).slice(0, 3)) });
    this.http.get<any>(`/projects/${this.pid}/measurements`).subscribe({ next: r => this.measurements.set((r.content || r || []).slice(0, 5)) });
  }
}
