import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-finance-global',
  standalone: true,
  imports: [DecimalPipe, TabViewModule, TableModule, ButtonModule, StatusTagComponent],
  template: `
    <h2 style="margin:0 0 1rem;color:var(--sp-text)">Financeiro Global</h2>

    <p-tabView>
      <p-tabPanel header="Contas a Pagar">
        <p-table [value]="payables()" styleClass="p-datatable-sm" [paginator]="true" [rows]="15">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th>Obra</th><th class="text-right">Valor</th><th>Vencimento</th><th>Status</th></tr></ng-template>
          <ng-template pTemplate="body" let-p><tr>
            <td>{{p.description}}</td><td style="font-size:0.8rem">{{p.projectName}}</td>
            <td class="text-right font-mono">{{p.amount | number:'1.2-2'}}</td><td>{{p.dueDate}}</td>
            <td><sp-status [status]="p.status" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Contas a Receber">
        <p-table [value]="receivables()" styleClass="p-datatable-sm" [paginator]="true" [rows]="15">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th>Obra</th><th class="text-right">Valor</th><th>Vencimento</th><th>Status</th></tr></ng-template>
          <ng-template pTemplate="body" let-r><tr>
            <td>{{r.description}}</td><td style="font-size:0.8rem">{{r.projectName}}</td>
            <td class="text-right font-mono">{{r.amount | number:'1.2-2'}}</td><td>{{r.dueDate}}</td>
            <td><sp-status [status]="r.status" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Aging (Inadimplência)">
        <div class="flex gap-3 mb-3">
          <div class="metric-card"><span class="metric-label">1-30 dias</span><strong>{{aging().bucket1to30?.amount | number:'1.2-2'}}</strong></div>
          <div class="metric-card"><span class="metric-label">31-60 dias</span><strong>{{aging().bucket31to60?.amount | number:'1.2-2'}}</strong></div>
          <div class="metric-card"><span class="metric-label">61-90 dias</span><strong>{{aging().bucket61to90?.amount | number:'1.2-2'}}</strong></div>
          <div class="metric-card"><span class="metric-label">90+ dias</span><strong style="color:#ef4444">{{aging().bucket90plus?.amount | number:'1.2-2'}}</strong></div>
        </div>
      </p-tabPanel>

      <p-tabPanel header="Autorizações">
        <p-table [value]="authorizations()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th>Fornecedor</th><th class="text-right">Valor</th><th>Solicitante</th><th>Status</th><th></th></tr></ng-template>
          <ng-template pTemplate="body" let-a><tr>
            <td>{{a.description}}</td><td>{{a.supplierName}}</td>
            <td class="text-right font-mono">{{a.amount | number:'1.2-2'}}</td><td>{{a.requestedBy}}</td>
            <td><sp-status [status]="a.status" /></td>
            <td>@if(a.status==='PENDING'){<p-button icon="pi pi-check" [text]="true" size="small" severity="success" />}</td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Cheques">
        <p-table [value]="checks()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Nº Cheque</th><th>Emitente</th><th class="text-right">Valor</th><th>Vencimento</th><th>Status</th></tr></ng-template>
          <ng-template pTemplate="body" let-c><tr>
            <td>{{c.checkNumber}}</td><td>{{c.issuerName || c.payeeName}}</td>
            <td class="text-right font-mono">{{c.amount | number:'1.2-2'}}</td><td>{{c.dueDate}}</td>
            <td><sp-status [status]="c.status" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>
  `,
  styles: [`.metric-card{background:var(--sp-surface-card);border:1px solid var(--sp-border);border-radius:8px;padding:12px 16px;min-width:120px}
    .metric-label{display:block;font-size:10px;color:var(--sp-text-muted);margin-bottom:4px}`]
})
export class FinanceGlobalComponent {
  private http = inject(HttpClient);
  payables = signal<any[]>([]);
  receivables = signal<any[]>([]);
  aging = signal<any>({});
  authorizations = signal<any[]>([]);
  checks = signal<any[]>([]);

  ngOnInit() {
    this.http.get<any>('/finance/payables?size=50').subscribe(r => this.payables.set(r.content || []));
    this.http.get<any>('/finance/receivables?size=50').subscribe(r => this.receivables.set(r.content || []));
    this.http.get<any>('/finance/aging/payables').subscribe(r => this.aging.set(r || {}));
    this.http.get<any>('/finance/checks/received?status=RECEIVED').subscribe(r => this.checks.set(r || []));
  }
}
