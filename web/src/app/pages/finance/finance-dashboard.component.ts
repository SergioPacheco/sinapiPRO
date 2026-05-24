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
      <p-tabPanel header="Banco">
        <div class="flex gap-2 mb-3">
          <p-calendar [(ngModel)]="bankFrom" dateFormat="dd/mm/yy" placeholder="De" styleClass="w-8rem" />
          <p-calendar [(ngModel)]="bankTo" dateFormat="dd/mm/yy" placeholder="Até" styleClass="w-8rem" />
          <p-button label="Filtrar" size="small" (onClick)="loadBankTransactions()" />
        </div>
        <p-table [value]="bankTransactions()" styleClass="p-datatable-sm" [paginator]="true" [rows]="15">
          <ng-template pTemplate="header"><tr><th style="width:100px">Data</th><th>Descrição</th><th style="width:80px">Tipo</th><th style="width:120px" class="text-right">Valor</th><th style="width:80px">Conc.</th></tr></ng-template>
          <ng-template pTemplate="body" let-t>
            <tr>
              <td>{{ t.transactionDate }}</td><td>{{ t.description }}</td>
              <td><span [class]="t.type === 'CREDIT' ? 'text-green-500' : 'text-red-500'">{{ t.type === 'CREDIT' ? '↑' : '↓' }}</span> {{ t.type }}</td>
              <td class="text-right">{{ t.amount | number:'1.2-2' }}</td>
              <td>@if (t.reconciled) { <i class="pi pi-check text-green-500"></i> } @else { <i class="pi pi-circle text-orange-400" title="Pendente"></i> }</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>
      <p-tabPanel header="Conciliação">
        <p class="text-muted mb-3">Transações não conciliadas — selecione e confirme o match com o extrato bancário.</p>
        <p-table [value]="unreconciled()" styleClass="p-datatable-sm" [(selection)]="selectedForReconcile" dataKey="id">
          <ng-template pTemplate="header"><tr><th style="width:40px"><p-tableHeaderCheckbox /></th><th style="width:100px">Data</th><th>Descrição</th><th style="width:80px">Tipo</th><th style="width:120px" class="text-right">Valor</th></tr></ng-template>
          <ng-template pTemplate="body" let-t>
            <tr><td><p-tableCheckbox [value]="t" /></td><td>{{ t.transactionDate }}</td><td>{{ t.description }}</td><td>{{ t.type }}</td><td class="text-right">{{ t.amount | number:'1.2-2' }}</td></tr>
          </ng-template>
        </p-table>
        <p-button label="Conciliar Selecionadas" icon="pi pi-check" size="small" class="mt-3" [disabled]="!selectedForReconcile?.length" (onClick)="reconcileBatch()" />
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
  bankTransactions = signal<any[]>([]);
  unreconciled = signal<any[]>([]);
  totals = signal<{ payable: number; receivable: number }>({ payable: 0, receivable: 0 });
  paymentVisible = false;
  payment: any = {};
  paying = signal(false);
  bankFrom: Date | null = null;
  bankTo: Date | null = null;
  selectedForReconcile: any[] = [];
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

  loadBankTransactions() {
    const from = this.bankFrom ? this.bankFrom.toISOString().split('T')[0] : '2020-01-01';
    const to = this.bankTo ? this.bankTo.toISOString().split('T')[0] : '2030-12-31';
    this.http.get<any>('/registry/bank-accounts').subscribe(res => {
      const accounts = res.content || res;
      if (accounts.length > 0) {
        const accountId = accounts[0].id;
        this.http.get<any>(`/bank-accounts/${accountId}/transactions?from=${from}&to=${to}`).subscribe({
          next: r => { const list = r.content || r; this.bankTransactions.set(list); this.unreconciled.set(list.filter((t: any) => !t.reconciled)); },
          error: () => {},
        });
      }
    });
  }

  reconcileBatch() {
    const ids = this.selectedForReconcile.map((t: any) => t.id);
    this.http.get<any>('/registry/bank-accounts').subscribe(res => {
      const accounts = res.content || res;
      if (accounts.length > 0) {
        this.http.post(`/bank-accounts/${accounts[0].id}/transactions/reconcile-batch`, ids).subscribe({
          next: () => { this.messages.add({ severity: 'success', summary: `${ids.length} transações conciliadas` }); this.selectedForReconcile = []; this.loadBankTransactions(); },
        });
      }
    });
  }
}
