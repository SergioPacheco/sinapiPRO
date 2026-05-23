import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { CurrencyDisplayComponent } from '../../shared/components';

@Component({
  selector: 'app-job-costing',
  standalone: true,
  imports: [DecimalPipe, TableModule, CardModule, CurrencyDisplayComponent],
  template: `
    <h3 style="margin:0 0 1rem">Job Costing</h3>
    <div class="grid mb-3">
      @for (card of cards(); track card.label) {
        <div class="col-3">
          <p-card>
            <div class="text-muted text-sm">{{ card.label }}</div>
            <div class="text-xl font-bold mt-1"><sp-currency [value]="card.value" /></div>
          </p-card>
        </div>
      }
    </div>
    <p-table [value]="codes()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="15">
      <ng-template pTemplate="header"><tr><th>Código</th><th>Descrição</th><th class="text-right">Orçado</th><th class="text-right">Comprometido</th><th class="text-right">Realizado</th><th class="text-right">Variação</th></tr></ng-template>
      <ng-template pTemplate="body" let-r>
        <tr>
          <td class="font-mono">{{ r.code }}</td><td>{{ r.description }}</td>
          <td class="text-right">{{ r.budgeted | number:'1.2-2' }}</td>
          <td class="text-right">{{ r.committed | number:'1.2-2' }}</td>
          <td class="text-right">{{ r.actual | number:'1.2-2' }}</td>
          <td class="text-right" [style.color]="r.variance >= 0 ? 'var(--green-400)' : 'var(--red-400)'">{{ r.variance | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhum código de custo</td></tr></ng-template>
    </p-table>
  `,
})
export class JobCostingComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  codes = signal<any[]>([]);
  cards = signal<{ label: string; value: number }[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/job-costing/summary`).subscribe(s => {
      this.cards.set([
        { label: 'Orçado', value: s.budgeted || 0 },
        { label: 'Comprometido', value: s.committed || 0 },
        { label: 'Realizado', value: s.actual || 0 },
        { label: 'Variação', value: s.variance || 0 },
      ]);
    });
    this.http.get<any>(`/projects/${id}/job-costing/codes`).subscribe({
      next: res => { this.codes.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
