import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { MessageService } from 'primeng/api';
import { StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent } from '../../shared/components';

@Component({
  selector: 'app-finance-dashboard',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, TabViewModule, ButtonModule, DialogModule, InputNumberModule, CalendarModule, StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent],
  template: `
    <h3 style="margin:0 0 1rem">Financeiro</h3>
    <div class="grid mb-3">
      <div class="col-12 md:col-4"><div class="kpi-card"><div class="kpi-label">A Pagar</div><div class="kpi-value"><sp-currency [value]="totals().payable" /></div></div></div>
      <div class="col-12 md:col-4"><div class="kpi-card"><div class="kpi-label">A Receber</div><div class="kpi-value"><sp-currency [value]="totals().receivable" /></div></div></div>
      <div class="col-12 md:col-4"><div class="kpi-card"><div class="kpi-label">Saldo</div><div class="kpi-value" [style.color]="totals().receivable - totals().payable >= 0 ? 'var(--sp-success)' : 'var(--sp-danger)'"><sp-currency [value]="totals().receivable - totals().payable" /></div></div></div>
    </div>

    <p-tabView>
      <p-tabPanel header="Contas a Pagar">
        <p-table [value]="payables()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:120px">Vencimento</th><th style="width:120px" class="text-right">Valor</th><th style="width:100px">Status</th><th style="width:100px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-p>
            <tr>
              <td>{{ p.description }}</td><td>{{ p.dueDate }}</td>
              <td class="text-right">{{ p.amount | number:'1.2-2' }}</td>
              <td><sp-status [status]="p.status" /></td>
              <td>@if (p.status === 'PENDING') { <p-button label="Pagar" size="small" [text]="true" (onClick)="openPayment(p)" /> }</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>
      <p-tabPanel header="Contas a Receber">
        <p-table [value]="receivables()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:120px">Vencimento</th><th style="width:120px" class="text-right">Valor</th><th style="width:100px">Status</th><th style="width:80px">Origem</th></tr></ng-template>
          <ng-template pTemplate="body" let-r>
            <tr>
              <td>{{ r.description }}</td><td>{{ r.dueDate }}</td>
              <td class="text-right">{{ r.amount | number:'1.2-2' }}</td>
              <td><sp-status [status]="r.status" /></td>
              <td>@if (r.measurementId) { <i class="pi pi-link text-muted" title="Vinculada à medição"></i> }</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>
      <p-tabPanel header="Faturas">
        <p-table [value]="invoices()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Número</th><th>Descrição</th><th style="width:120px" class="text-right">Valor</th><th style="width:100px">Status</th></tr></ng-template>
          <ng-template pTemplate="body" let-inv>
            <tr><td>{{ inv.number }}</td><td>{{ inv.description }}</td><td class="text-right">{{ inv.amount | number:'1.2-2' }}</td><td><sp-status [status]="inv.status" /></td></tr>
          </ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>

    <!-- Payment Dialog -->
    <p-dialog header="Registrar Pagamento" [(visible)]="paymentVisible" [style]="{width:'380px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Valor</label><p-inputNumber [(ngModel)]="payment.amount" mode="currency" currency="BRL" locale="pt-BR" styleClass="w-full" /></div>
        <div><label>Data</label><p-calendar [(ngModel)]="payment.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="paymentVisible = false" />
        <p-button label="Confirmar" icon="pi pi-check" (onClick)="confirmPayment()" [loading]="paying()" />
      </ng-template>
    </p-dialog>
  `,
  styles: [`.kpi-card { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: var(--sp-radius); padding: 1rem; }
    .kpi-label { font-size: 12px; color: var(--sp-text-muted); text-transform: uppercase; }
    .kpi-value { font-size: 1.5rem; font-weight: 700; margin-top: 4px; }`],
})
export class FinanceDashboardComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  payables = signal<any[]>([]);
  receivables = signal<any[]>([]);
  invoices = signal<any[]>([]);
  totals = signal<{ payable: number; receivable: number }>({ payable: 0, receivable: 0 });
  paymentVisible = false;
  payment: any = {};
  paying = signal(false);
  private selectedPayable: any = null;

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/finance/payables`).subscribe(res => {
      const list = res.content || res;
      this.payables.set(list);
      this.totals.update(t => ({ ...t, payable: list.reduce((s: number, p: any) => s + (p.amount || 0), 0) }));
    });
    this.http.get<any>(`/projects/${id}/finance/receivables`).subscribe(res => {
      const list = res.content || res;
      this.receivables.set(list);
      this.totals.update(t => ({ ...t, receivable: list.reduce((s: number, r: any) => s + (r.amount || 0), 0) }));
    });
    this.http.get<any>(`/projects/${id}/finance/invoices`).subscribe(res => this.invoices.set(res.content || res));
  }

  openPayment(p: any) {
    this.selectedPayable = p;
    this.payment = { amount: p.amount, date: new Date() };
    this.paymentVisible = true;
  }

  confirmPayment() {
    this.paying.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/finance/payables/${this.selectedPayable.id}/pay`, this.payment).subscribe({
      next: () => { this.paymentVisible = false; this.paying.set(false); this.messages.add({ severity: 'success', summary: 'Pagamento registrado' }); this.ngOnInit(); },
      error: () => this.paying.set(false),
    });
  }
}
