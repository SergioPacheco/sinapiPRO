import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-finance-dashboard',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, TabViewModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Financeiro</h2>
      <p-button label="Nova Despesa" icon="pi pi-plus" size="small" (onClick)="showNewPay = true" />
    </div>

    <!-- Resumo Geral -->
    <div class="flex gap-3 mb-3" style="font-size:12px">
      <div class="metric-card"><span class="metric-label">A Pagar</span><strong style="color:#ef4444">{{ totals().pagar | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">A Receber</span><strong style="color:#10b981">{{ totals().receber | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Saldo</span><strong [style.color]="totals().saldo >= 0 ? '#10b981' : '#ef4444'">{{ totals().saldo | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Vencidas</span><strong style="color:#ef4444">{{ totals().vencidas | number:'1.2-2' }}</strong></div>
    </div>

    <p-tabView>
      <!-- Contas a Pagar -->
      <p-tabPanel header="Contas a Pagar">
        <p-table [value]="payables()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:100px">Fornecedor</th><th class="text-right" style="width:90px">Valor</th><th style="width:80px">Vencimento</th><th style="width:70px">Status</th><th style="width:60px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-p>
            <tr>
              <td style="font-size:0.85rem">{{ p.description }}</td>
              <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ p.supplierName }}</td>
              <td class="text-right font-mono">{{ p.amount | number:'1.2-2' }}</td>
              <td style="font-size:0.8rem">{{ p.dueDate }}</td>
              <td><sp-status [status]="p.status || 'OPEN'" /></td>
              <td>@if (p.status !== 'PAID') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" (onClick)="payItem(p)" pTooltip="Pagar" /> }</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Contas a Receber / Faturamento -->
      <p-tabPanel header="Contas a Receber">
        <div class="flex justify-content-end mb-2">
          <p-button label="Faturar Medição" icon="pi pi-receipt" size="small" severity="success" (onClick)="showFaturar = true" />
        </div>
        <p-table [value]="receivables()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th class="text-right" style="width:90px">Valor</th><th style="width:80px">Vencimento</th><th style="width:70px">Tipo</th><th style="width:70px">Status</th><th style="width:60px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-r>
            <tr>
              <td style="font-size:0.85rem">{{ r.description }}</td>
              <td class="text-right font-mono">{{ r.amount | number:'1.2-2' }}</td>
              <td style="font-size:0.8rem">{{ r.dueDate }}</td>
              <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ r.type }}</td>
              <td><sp-status [status]="r.status || 'OPEN'" /></td>
              <td>@if (r.status !== 'RECEIVED') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" (onClick)="receiveItem(r)" pTooltip="Receber" /> }</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Fluxo de Caixa -->
      <p-tabPanel header="Fluxo de Caixa">
        <p-table [value]="cashFlow()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
          <ng-template pTemplate="header"><tr><th>Período</th><th class="text-right">Receitas</th><th class="text-right">Despesas</th><th class="text-right">Saldo</th><th class="text-right">Acumulado</th></tr></ng-template>
          <ng-template pTemplate="body" let-row>
            <tr>
              <td>{{ row.period }}</td>
              <td class="text-right font-mono" style="color:#10b981">{{ row.income | number:'1.2-2' }}</td>
              <td class="text-right font-mono" style="color:#ef4444">{{ row.expense | number:'1.2-2' }}</td>
              <td class="text-right font-mono" [style.color]="row.balance >= 0 ? '#10b981' : '#ef4444'">{{ row.balance | number:'1.2-2' }}</td>
              <td class="text-right font-mono" style="font-weight:600">{{ row.accumulated | number:'1.2-2' }}</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Movimento Bancário / Conciliação -->
      <p-tabPanel header="Banco">
        <div class="flex align-items-center justify-content-between mb-2">
          <div style="font-size:12px;color:var(--sp-text-muted)">
            Conta: <strong style="color:var(--sp-text)">{{ bankAccount().bankName }} - Ag {{ bankAccount().agency }} / CC {{ bankAccount().accountNumber }}</strong>
            &nbsp;|&nbsp; Saldo: <strong [style.color]="bankBalance() >= 0 ? '#10b981' : '#ef4444'">{{ bankBalance() | number:'1.2-2' }}</strong>
          </div>
          <div class="flex gap-2">
            <p-button label="Lançamento" icon="pi pi-plus" size="small" (onClick)="showNewTx = true" />
            <p-button label="Conciliar" icon="pi pi-check-square" size="small" severity="secondary" (onClick)="reconcileBatch()" />
          </div>
        </div>
        <p-table [value]="transactions()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15">
          <ng-template pTemplate="header"><tr><th style="width:80px">Data</th><th>Descrição</th><th class="text-right" style="width:90px">Valor</th><th style="width:60px">Tipo</th><th style="width:70px">Conciliado</th></tr></ng-template>
          <ng-template pTemplate="body" let-t>
            <tr>
              <td style="font-size:0.8rem">{{ t.transactionDate }}</td>
              <td style="font-size:0.85rem">{{ t.description }}</td>
              <td class="text-right font-mono" [style.color]="t.type === 'CREDIT' ? '#10b981' : '#ef4444'">{{ t.type === 'CREDIT' ? '+' : '-' }}{{ t.amount | number:'1.2-2' }}</td>
              <td style="font-size:0.8rem">{{ t.type === 'CREDIT' ? 'Crédito' : 'Débito' }}</td>
              <td class="text-center">{{ t.reconciled ? '✓' : '—' }}</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>

    <!-- Nova Despesa -->
    <p-dialog header="Nova Despesa" [(visible)]="showNewPay" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Descrição</label><input pInputText [(ngModel)]="payForm.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Valor</label><p-inputNumber [(ngModel)]="payForm.amount" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-6"><label>Vencimento</label><p-calendar [(ngModel)]="payForm.dueDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
        <div><label>Categoria</label><input pInputText [(ngModel)]="payForm.category" class="w-full" placeholder="MATERIAL, SERVICO..." /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Criar" icon="pi pi-check" (onClick)="createPayable()" />
      </ng-template>
    </p-dialog>

    <!-- Faturar Medição -->
    <p-dialog header="Faturar Medição Aprovada" [(visible)]="showFaturar" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <p style="color:var(--sp-text-muted)">Gera uma conta a receber a partir de uma medição aprovada.</p>
        <div><label>Descrição (NF)</label><input pInputText [(ngModel)]="fatForm.description" class="w-full" placeholder="Medição #X - Mês/Ano" /></div>
        <div class="grid">
          <div class="col-6"><label>Valor</label><p-inputNumber [(ngModel)]="fatForm.amount" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-6"><label>Vencimento</label><p-calendar [(ngModel)]="fatForm.dueDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Faturar" icon="pi pi-receipt" severity="success" (onClick)="faturar()" />
      </ng-template>
    </p-dialog>
  `,
  styles: [`.metric-card { background:var(--sp-surface-card); border:1px solid var(--sp-border); border-radius:6px; padding:10px 14px; } .metric-label { display:block; font-size:10px; color:var(--sp-text-muted); text-transform:uppercase; margin-bottom:2px; }`],
})
export class FinanceDashboardComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  payables = signal<any[]>([]);
  receivables = signal<any[]>([]);
  cashFlow = signal<any[]>([]);
  transactions = signal<any[]>([]);
  bankAccount = signal<any>({});
  bankBalance = signal(0);
  totals = signal<any>({ pagar: 0, receber: 0, saldo: 0, vencidas: 0 });
  showNewPay = false;
  showFaturar = false;
  showNewTx = false;
  payForm: any = {};
  fatForm: any = {};
  txForm: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    const today = new Date().toISOString().slice(0, 10);
    this.http.get<any>(`/projects/${this.pid}/finance/payables?size=100`).subscribe({
      next: r => {
        const items = r.content || r || [];
        this.payables.set(items);
        const pagar = items.filter((i: any) => i.status !== 'PAID').reduce((s: number, i: any) => s + (i.amount || 0), 0);
        const vencidas = items.filter((i: any) => i.dueDate < today && i.status !== 'PAID').reduce((s: number, i: any) => s + (i.amount || 0), 0);
        this.http.get<any>(`/projects/${this.pid}/finance/receivables?size=100`).subscribe({
          next: rr => {
            const rItems = rr.content || rr || [];
            this.receivables.set(rItems);
            const receber = rItems.filter((i: any) => i.status !== 'RECEIVED').reduce((s: number, i: any) => s + (i.amount || 0), 0);
            this.totals.set({ pagar, receber, saldo: receber - pagar, vencidas });
          },
        });
      },
    });
    if (this.pid) {
      this.http.get<any>(`/analytics/projects/${this.pid}/cash-flow`).subscribe({ next: r => this.cashFlow.set(r?.periods || r || []), error: () => {} });
    }
    // Banco
    this.http.get<any>('/registry/bank-accounts').subscribe({
      next: r => {
        const accounts = r.content || r || [];
        if (accounts.length > 0) {
          const acc = accounts[0];
          this.bankAccount.set(acc);
          this.http.get<any>(`/bank-accounts/${acc.id}/transactions`).subscribe({ next: t => this.transactions.set(t.content || t || []), error: () => {} });
          this.http.get<any>(`/bank-accounts/${acc.id}/transactions/balance`).subscribe({ next: b => this.bankBalance.set(b?.balance || 0), error: () => {} });
        }
      }, error: () => {},
    });
  }

  reconcileBatch() {
    const acc = this.bankAccount();
    if (!acc.id) return;
    this.http.post(`/bank-accounts/${acc.id}/transactions/reconcile-batch`, {}).subscribe({
      next: () => this.messages.add({ severity: 'success', summary: 'Conciliação realizada' }),
      error: () => this.messages.add({ severity: 'info', summary: 'Nenhuma transação para conciliar' }),
    });
  }

  createPayable() {
    const body = { ...this.payForm, dueDate: this.payForm.dueDate?.toISOString?.()?.slice(0, 10) };
    this.http.post(`/projects/${this.pid}/finance/payables`, body).subscribe({ next: () => { this.showNewPay = false; this.payForm = {}; this.messages.add({ severity: 'success', summary: 'Despesa criada' }); this.ngOnInit(); } });
  }

  faturar() {
    const body = { description: this.fatForm.description, amount: this.fatForm.amount, dueDate: this.fatForm.dueDate?.toISOString?.()?.slice(0, 10), category: 'FATURAMENTO' };
    this.http.post(`/projects/${this.pid}/finance/receivables`, body).subscribe({ next: () => { this.showFaturar = false; this.fatForm = {}; this.messages.add({ severity: 'success', summary: 'Faturamento gerado' }); this.ngOnInit(); } });
  }

  payItem(item: any) {
    const amount = item.amount - (item.paidAmount || 0);
    this.http.post(`/projects/${this.pid}/finance/payables/${item.id}/pay`, { amount, date: new Date().toISOString().slice(0, 10) })
      .subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Pago' }); this.ngOnInit(); } });
  }

  receiveItem(item: any) {
    const amount = item.amount - (item.receivedAmount || 0);
    this.http.post(`/projects/${this.pid}/finance/receivables/${item.id}/receive`, { amount, date: new Date().toISOString().slice(0, 10) })
      .subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Recebido' }); this.ngOnInit(); } });
  }
}
