import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [TableModule, ButtonModule, TagModule, RouterLink,DecimalPipe],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Orçamentos</h3>
      <p-button label="Novo Orçamento" icon="pi pi-plus" size="small" />
    </div>
    <p-table [value]="budgets()" [loading]="loading()" styleClass="p-datatable-sm" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr><th style="width:100px">Código</th><th>Título</th><th style="width:130px">Valor Total</th><th style="width:120px">Status</th><th style="width:80px"></th></tr>
      </ng-template>
      <ng-template pTemplate="body" let-b>
        <tr>
          <td class="font-mono">{{ b.code }}</td>
          <td>{{ b.title }}</td>
          <td class="currency">{{ b.totalAmount | number:'1.2-2' }}</td>
          <td><p-tag [value]="b.status" [severity]="b.active ? 'success' : 'secondary'" /></td>
          <td><a [routerLink]="[b.id]" class="pi pi-arrow-right" style="color:var(--sp-primary)"></a></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhum orçamento</td></tr></ng-template>
    </p-table>
  `,
})
export class BudgetListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  budgets = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/budgets`).subscribe({
      next: res => { this.budgets.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
