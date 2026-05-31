import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-budget-list',
  standalone: true,
  imports: [DecimalPipe, RouterLink, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Orçamentos</h2>
      <p-button label="Novo Orçamento" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="budgets()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th style="width:90px">Código</th>
          <th>Título</th>
          <th class="text-right" style="width:120px">Valor Total</th>
          <th style="width:90px">Status</th>
          <th style="width:60px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-b>
        <tr>
          <td class="font-mono" style="font-size:0.8rem">{{ b.code }}</td>
          <td>{{ b.title }}</td>
          <td class="text-right font-mono">{{ b.totalAmount | number:'1.2-2' }}</td>
          <td><sp-status [status]="b.status" /></td>
          <td><a [routerLink]="['/projects', projectId, 'budgets', b.id]" class="pi pi-arrow-right" style="color:var(--sp-primary)"></a></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum orçamento. Crie o primeiro.</td></tr></ng-template>
    </p-table>

    <!-- Novo Orçamento -->
    <p-dialog header="Novo Orçamento" [(visible)]="showNew" [style]="{width:'480px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-4"><label>Código</label><input pInputText [(ngModel)]="form.code" class="w-full" placeholder="ORC-001" /></div>
          <div class="col-8"><label>Título</label><input pInputText [(ngModel)]="form.title" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-6"><label>Cliente</label><input pInputText [(ngModel)]="form.customerName" class="w-full" /></div>
          <div class="col-6"><label>Valor Estimado</label><p-inputNumber [(ngModel)]="form.totalAmount" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-4"><label>Início</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-4"><label>Término</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-4"><label>Arredondamento</label><p-dropdown [(ngModel)]="form.roundingMethod" [options]="roundingOpts" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar e Abrir" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
})
export class BudgetListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  budgets = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = this.defaultForm();
  roundingOpts = [{ label: 'Truncar (TCU)', value: 'TRUNCATE' }, { label: 'ABNT', value: 'ROUND_ABNT' }, { label: 'Simples', value: 'ROUND_SIMPLE' }];

  get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.projectId}/budgets`).subscribe({ next: r => { this.budgets.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  create() {
    const body = {
      ...this.form,
      metadata: this.form.metadata || {},
      startDate: this.form.startDate?.toISOString?.()?.slice(0, 10),
      endDate: this.form.endDate?.toISOString?.()?.slice(0, 10),
    };
    this.http.post<any>(`/projects/${this.projectId}/budgets`, body).subscribe({
      next: (res) => {
        this.showNew = false;
        this.form = this.defaultForm();
        this.messages.add({ severity: 'success', summary: 'Orçamento criado' });
        this.router.navigate(['/projects', this.projectId, 'budgets', res.id]);
      },
    });
  }

  private defaultForm() {
    return {
      status: 'DRAFT',
      roundingMethod: 'TRUNCATE',
      decimalPlaces: 4,
      totalAmount: 0,
      startDate: new Date(),
      referenceDate: '2024-12-01',
      state: 'SP',
      metadata: {},
    };
  }
}
