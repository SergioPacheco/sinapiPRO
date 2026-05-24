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
  selector: 'app-finance-dashboard',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Financeiro — Contas a Pagar</h2>
      <p-button label="Nova Despesa" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <!-- Resumo -->
    <div class="flex gap-3 mb-3" style="font-size:12px">
      <div class="metric-card"><span class="metric-label">Total a Pagar</span><strong>{{ totalPagar() | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Vencidas</span><strong style="color:#ef4444">{{ vencidas() | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">A Vencer</span><strong style="color:#10b981">{{ aVencer() | number:'1.2-2' }}</strong></div>
    </div>

    <p-table [value]="payables()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15">
      <ng-template pTemplate="header">
        <tr>
          <th>Descrição</th>
          <th style="width:120px">Fornecedor</th>
          <th class="text-right" style="width:100px">Valor</th>
          <th style="width:90px">Vencimento</th>
          <th style="width:80px">Status</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-p>
        <tr>
          <td style="font-size:0.85rem">{{ p.description }}</td>
          <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ p.supplierName }}</td>
          <td class="text-right font-mono">{{ p.amount | number:'1.2-2' }}</td>
          <td style="font-size:0.8rem">{{ p.dueDate }}</td>
          <td><sp-status [status]="p.status" /></td>
        </tr>
      </ng-template>
    </p-table>

    <!-- Nova Despesa -->
    <p-dialog header="Nova Despesa" [(visible)]="showNew" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Descrição</label><input pInputText [(ngModel)]="form.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Valor</label><p-inputNumber [(ngModel)]="form.amount" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-6"><label>Vencimento</label><p-calendar [(ngModel)]="form.dueDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
        <div><label>Categoria</label><input pInputText [(ngModel)]="form.category" class="w-full" placeholder="MATERIAL, SERVICO, etc." /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
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
  loading = signal(true);
  showNew = false;
  form: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  totalPagar = signal(0);
  vencidas = signal(0);
  aVencer = signal(0);

  ngOnInit() {
    this.http.get<any>(`/payables?size=100`).subscribe({
      next: r => {
        const items = r.content || r;
        this.payables.set(items);
        const today = new Date().toISOString().slice(0, 10);
        this.totalPagar.set(items.reduce((s: number, p: any) => s + (p.amount || 0), 0));
        this.vencidas.set(items.filter((p: any) => p.dueDate < today && p.status !== 'PAID').reduce((s: number, p: any) => s + (p.amount || 0), 0));
        this.aVencer.set(items.filter((p: any) => p.dueDate >= today).reduce((s: number, p: any) => s + (p.amount || 0), 0));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  create() {
    const body = { ...this.form, dueDate: this.form.dueDate?.toISOString?.()?.slice(0, 10), projectId: this.pid };
    this.http.post('/payables', body).subscribe({
      next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Despesa criada' }); this.ngOnInit(); },
    });
  }
}
