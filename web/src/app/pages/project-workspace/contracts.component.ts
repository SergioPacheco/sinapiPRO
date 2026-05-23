import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { StatusTagComponent, CurrencyDisplayComponent } from '../../shared/components';

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [DecimalPipe, TableModule, TagModule, ButtonModule, StatusTagComponent, CurrencyDisplayComponent],
  template: `
    <p-table [value]="contracts()" [loading]="loading()" styleClass="p-datatable-sm" [rows]="10" [paginator]="true" [expandedRowKeys]="expandedRows" dataKey="id">
      <ng-template pTemplate="header"><tr><th style="width:40px"></th><th>Número</th><th>Descrição</th><th>Fornecedor</th><th class="text-right">Valor</th><th style="width:110px">Status</th></tr></ng-template>
      <ng-template pTemplate="body" let-c let-expanded="expanded">
        <tr>
          <td><p-button [icon]="expanded ? 'pi pi-chevron-down' : 'pi pi-chevron-right'" [text]="true" [pRowToggler]="c" /></td>
          <td class="font-mono">{{ c.number }}</td><td>{{ c.description }}</td><td>{{ c.supplierName }}</td>
          <td class="text-right"><sp-currency [value]="c.originalValue" /></td><td><sp-status [status]="c.status" /></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="rowexpansion" let-c>
        <tr><td colspan="6" class="p-3">
          <h5 style="margin:0 0 0.5rem">Aditivos (Change Orders)</h5>
          <p-table [value]="changeOrders()[c.id] || []" styleClass="p-datatable-sm">
            <ng-template pTemplate="header"><tr><th style="width:70px">#</th><th>Descrição</th><th class="text-right" style="width:130px">Valor</th><th style="width:110px">Status</th></tr></ng-template>
            <ng-template pTemplate="body" let-co>
              <tr><td>{{ co.number }}</td><td>{{ co.description }}</td><td class="text-right">{{ co.amount | number:'1.2-2' }}</td><td><sp-status [status]="co.status" /></td></tr>
            </ng-template>
            <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhum aditivo</td></tr></ng-template>
          </p-table>
        </td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhum contrato</td></tr></ng-template>
    </p-table>
  `,
})
export class ContractsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  contracts = signal<any[]>([]);
  changeOrders = signal<Record<string, any[]>>({});
  loading = signal(true);
  expandedRows: any = {};

  private get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.projectId}/contracts`).subscribe({
      next: res => {
        const list = res.content || res;
        this.contracts.set(list);
        this.loading.set(false);
        list.forEach((c: any) => this.loadChangeOrders(c.id));
      },
      error: () => this.loading.set(false),
    });
  }

  loadChangeOrders(cid: string) {
    this.http.get<any>(`/projects/${this.projectId}/contracts/${cid}/change-orders`).subscribe(res => {
      this.changeOrders.update(m => ({ ...m, [cid]: res.content || res }));
    });
  }
}
