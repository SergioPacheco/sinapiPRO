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
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Contas a Receber</h2>
      <p-button label="Nova Receita" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <!-- Resumo -->
    <div class="flex gap-3 mb-3" style="font-size:12px">
      <div class="metric-card"><span class="metric-label">Total a Receber</span><strong style="color:#10b981">{{ totalReceber() | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Recebido</span><strong>{{ totalRecebido() | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Em Atraso</span><strong style="color:#ef4444">{{ totalAtraso() | number:'1.2-2' }}</strong></div>
    </div>

    <p-table [value]="receivables()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15">
      <ng-template pTemplate="header">
        <tr>
          <th>Descrição</th>
          <th class="text-right" style="width:110px">Valor</th>
          <th style="width:90px">Vencimento</th>
          <th style="width:80px">Tipo</th>
          <th style="width:80px">Status</th>
          <th style="width:80px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-r>
        <tr>
          <td style="font-size:0.85rem">{{ r.description }}</td>
          <td class="text-right font-mono">{{ r.amount | number:'1.2-2' }}</td>
          <td style="font-size:0.8rem">{{ r.dueDate }}</td>
          <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ r.type }}</td>
          <td><sp-status [status]="r.status" /></td>
          <td>
            @if (r.status !== 'RECEIVED') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" (onClick)="receive(r.id)" pTooltip="Receber" /> }
          </td>
        </tr>
      </ng-template>
    </p-table>

    <!-- Nova Receita -->
    <p-dialog header="Nova Receita" [(visible)]="showNew" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Descrição</label><input pInputText [(ngModel)]="form.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Valor</label><p-inputNumber [(ngModel)]="form.amount" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-6"><label>Vencimento</label><p-calendar [(ngModel)]="form.dueDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
        <div><label>Tipo</label><input pInputText [(ngModel)]="form.type" class="w-full" placeholder="MEDICAO, FATURAMENTO, etc." /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
  styles: [`.metric-card { background:var(--sp-surface-card); border:1px solid var(--sp-border); border-radius:6px; padding:10px 14px; } .metric-label { display:block; font-size:10px; color:var(--sp-text-muted); text-transform:uppercase; margin-bottom:2px; }`],
})
export class InvoiceListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  receivables = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};
  totalReceber = signal(0);
  totalRecebido = signal(0);
  totalAtraso = signal(0);

  ngOnInit() {
    this.http.get<any>('/receivables?size=100').subscribe({
      next: r => {
        const items = r.content || r || [];
        this.receivables.set(items);
        const today = new Date().toISOString().slice(0, 10);
        this.totalReceber.set(items.filter((i: any) => i.status !== 'RECEIVED').reduce((s: number, i: any) => s + (i.amount || 0), 0));
        this.totalRecebido.set(items.filter((i: any) => i.status === 'RECEIVED').reduce((s: number, i: any) => s + (i.amount || 0), 0));
        this.totalAtraso.set(items.filter((i: any) => i.dueDate < today && i.status !== 'RECEIVED').reduce((s: number, i: any) => s + (i.amount || 0), 0));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  create() {
    const body = { ...this.form, dueDate: this.form.dueDate?.toISOString?.()?.slice(0, 10) };
    this.http.post('/receivables', body).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Receita criada' }); this.ngOnInit(); } });
  }

  receive(id: string) {
    this.http.post(`/receivables/${id}/receive`, { amount: 0, receivedDate: new Date().toISOString().slice(0, 10) }).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Recebido' }); this.ngOnInit(); } });
  }
}
