import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { StatusTagComponent, EmptyStateComponent } from '../../shared/components';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [TableModule, ButtonModule, RouterLink, DecimalPipe, StatusTagComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Orçamentos</h3>
      <p-button label="Novo Orçamento" icon="pi pi-plus" size="small" routerLink="new" />
    </div>

    @if (!loading() && budgets().length === 0) {
      <sp-empty title="Nenhum orçamento" message="Crie o primeiro orçamento para esta obra." icon="file" actionLabel="Novo Orçamento" (action)="goNew()" />
    } @else {
      <p-table [value]="budgets()" [loading]="loading()" styleClass="p-datatable-sm" [rowHover]="true">
        <ng-template pTemplate="header">
          <tr>
            <th style="width:100px">Código</th>
            <th>Título</th>
            <th style="width:130px" class="text-right">Valor Total</th>
            <th style="width:120px">Status</th>
            <th style="width:80px"></th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-b>
          <tr>
            <td class="font-mono">{{ b.code }}</td>
            <td>{{ b.title }}</td>
            <td class="text-right currency">{{ b.totalAmount | number:'1.2-2' }}</td>
            <td><sp-status [status]="b.status" /></td>
            <td><a [routerLink]="[b.id]" class="pi pi-arrow-right" style="color:var(--sp-primary)"></a></td>
          </tr>
        </ng-template>
      </p-table>
    }
  `,
})
export class BudgetListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
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

  goNew() { this.router.navigate(['new'], { relativeTo: this.route }); }
}
