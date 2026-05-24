import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-budget-global',
  standalone: true,
  imports: [DecimalPipe, RouterLink, FormsModule, TableModule, ButtonModule, InputTextModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Orçamentos</h2>
      <input pInputText [(ngModel)]="search" placeholder="Buscar..." style="width:250px" (input)="loadBudgets()" />
    </div>
    <p-table [value]="budgets()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="20">
      <ng-template pTemplate="header">
        <tr>
          <th style="width:100px">Código</th>
          <th>Título</th>
          <th style="width:150px">Obra</th>
          <th style="width:120px" class="text-right">Valor</th>
          <th style="width:100px">Status</th>
          <th style="width:60px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-b>
        <tr>
          <td class="font-mono" style="font-size:0.8rem">{{ b.code }}</td>
          <td>{{ b.title }}</td>
          <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ b.projectName }}</td>
          <td class="text-right font-mono">{{ b.totalAmount | number:'1.2-2' }}</td>
          <td><sp-status [status]="b.status" /></td>
          <td><a [routerLink]="['/budgets', b.id]" class="pi pi-arrow-right" style="color:var(--sp-primary)"></a></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum orçamento encontrado</td></tr></ng-template>
    </p-table>
  `,
})
export class BudgetGlobalComponent implements OnInit {
  private http = inject(HttpClient);
  budgets = signal<any[]>([]);
  loading = signal(true);
  search = '';

  ngOnInit() { this.loadBudgets(); }

  loadBudgets() {
    this.loading.set(true);
    let url = '/budgets?size=50';
    if (this.search) url += `&search=${encodeURIComponent(this.search)}`;
    this.http.get<any>(url).subscribe({
      next: res => { this.budgets.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
