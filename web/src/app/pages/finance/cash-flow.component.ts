import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-cash-flow',
  standalone: true,
  imports: [DecimalPipe, TableModule],
  template: `
    <h3 style="margin:0 0 1rem">Fluxo de Caixa — Projeção Mensal</h3>
    <p-table [value]="rows()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-striped">
      <ng-template pTemplate="header"><tr><th>Mês</th><th style="width:140px">Receitas</th><th style="width:140px">Despesas</th><th style="width:140px">Saldo</th></tr></ng-template>
      <ng-template pTemplate="body" let-r>
        <tr><td>{{ r.month }}</td><td class="text-green-500">{{ r.income | number:'1.2-2' }}</td><td class="text-red-500">{{ r.expense | number:'1.2-2' }}</td><td [class.text-red-500]="r.balance < 0">{{ r.balance | number:'1.2-2' }}</td></tr>
      </ng-template>
    </p-table>
  `,
})
export class CashFlowComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  rows = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any[]>(`/projects/${id}/finance/cash-flow/projection`).subscribe({
      next: res => { this.rows.set(res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
