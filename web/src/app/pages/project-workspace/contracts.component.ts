import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe, DatePipe } from '@angular/common';
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
  selector: 'app-contracts',
  standalone: true,
  imports: [DecimalPipe, DatePipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Contratos</h2>
      <p-button label="Novo Contrato" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="contracts()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th style="width:100px">Número</th>
          <th>Fornecedor</th>
          <th style="width:130px" class="text-right">Valor</th>
          <th style="width:100px">Início</th>
          <th style="width:100px">Término</th>
          <th style="width:100px">Status</th>
          <th style="width:80px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-c>
        <tr>
          <td class="font-mono" style="font-size:0.8rem">{{ c.contractNumber }}</td>
          <td>{{ c.supplierName }}</td>
          <td class="text-right font-mono">{{ c.totalValue | number:'1.2-2' }}</td>
          <td style="font-size:0.8rem">{{ c.startDate }}</td>
          <td style="font-size:0.8rem">{{ c.endDate }}</td>
          <td><sp-status [status]="c.status" /></td>
          <td>
            <p-button icon="pi pi-file-pdf" [text]="true" size="small" (onClick)="downloadPdf(c.id)" pTooltip="PDF" />
            <p-button icon="pi pi-check" [text]="true" size="small" (onClick)="activate(c.id)" pTooltip="Ativar" [disabled]="c.status === 'ACTIVE'" />
          </td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="7" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum contrato</td></tr></ng-template>
    </p-table>

    <!-- Novo Contrato -->
    <p-dialog header="Novo Contrato" [(visible)]="showNew" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Número</label><input pInputText [(ngModel)]="form.contractNumber" class="w-full" /></div>
        <div><label>Fornecedor</label><p-dropdown [(ngModel)]="form.supplierId" [options]="suppliers()" optionLabel="name" optionValue="id" placeholder="Selecionar..." styleClass="w-full" [filter]="true" /></div>
        <div class="grid">
          <div class="col-6"><label>Valor</label><p-inputNumber [(ngModel)]="form.totalValue" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-3"><label>Início</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-3"><label>Término</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
        <div><label>Descrição</label><input pInputText [(ngModel)]="form.description" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ContractsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  contracts = signal<any[]>([]);
  suppliers = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id') || this.route.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/contracts`).subscribe({ next: r => { this.contracts.set(r.content || r); this.loading.set(false); }, error: () => this.loading.set(false) });
    this.http.get<any>('/suppliers?size=100').subscribe({ next: r => this.suppliers.set(r.content || r) });
  }

  create() {
    const body = { ...this.form, startDate: this.form.startDate?.toISOString?.()?.slice(0, 10) || this.form.startDate, endDate: this.form.endDate?.toISOString?.()?.slice(0, 10) || this.form.endDate };
    this.http.post(`/projects/${this.pid}/contracts`, body).subscribe({
      next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Contrato criado' }); this.ngOnInit(); },
    });
  }

  activate(id: string) {
    this.http.post(`/projects/${this.pid}/contracts/${id}/activate`, {}).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Contrato ativado' }); this.ngOnInit(); },
    });
  }

  downloadPdf(id: string) { window.open(`/api/v1/projects/${this.pid}/contracts/${id}/reports/contract.pdf`, '_blank'); }
}
