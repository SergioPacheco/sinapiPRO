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
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-procurement-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Suprimentos — Pedidos de Compra</h2>
      <p-button label="Novo Pedido" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="orders()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th style="width:90px">Número</th>
          <th>Fornecedor</th>
          <th>Descrição</th>
          <th class="text-right" style="width:110px">Valor</th>
          <th style="width:90px">Status</th>
          <th style="width:80px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-o>
        <tr>
          <td class="font-mono" style="font-size:0.8rem">{{ o.orderNumber }}</td>
          <td>{{ o.supplierName }}</td>
          <td style="font-size:0.85rem">{{ o.description }}</td>
          <td class="text-right font-mono">{{ o.totalValue | number:'1.2-2' }}</td>
          <td><sp-status [status]="o.status" /></td>
          <td>
            @if (o.status === 'DRAFT') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" (onClick)="approve(o.id)" pTooltip="Aprovar" /> }
            @if (o.status === 'APPROVED') { <p-button icon="pi pi-inbox" [text]="true" size="small" (onClick)="receive(o.id)" pTooltip="Receber" /> }
          </td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum pedido</td></tr></ng-template>
    </p-table>

    <!-- Novo Pedido -->
    <p-dialog header="Novo Pedido de Compra" [(visible)]="showNew" [style]="{width:'480px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Fornecedor</label><p-dropdown [(ngModel)]="form.supplierId" [options]="suppliers()" optionLabel="name" optionValue="id" placeholder="Selecionar..." styleClass="w-full" [filter]="true" /></div>
        <div><label>Descrição</label><input pInputText [(ngModel)]="form.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Valor Total</label><p-inputNumber [(ngModel)]="form.totalValue" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-6"><label>Valor Unitário</label><p-inputNumber [(ngModel)]="form.unitPrice" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ProcurementListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  orders = signal<any[]>([]);
  suppliers = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/procurement`).subscribe({ next: r => { this.orders.set(r.content || r); this.loading.set(false); }, error: () => this.loading.set(false) });
    this.http.get<any>('/suppliers?size=100').subscribe({ next: r => this.suppliers.set(r.content || r) });
  }

  create() {
    this.http.post(`/projects/${this.pid}/procurement`, this.form).subscribe({
      next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Pedido criado' }); this.ngOnInit(); },
    });
  }

  approve(id: string) { this.http.post(`/projects/${this.pid}/procurement/${id}/approve`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Aprovado' }); this.ngOnInit(); } }); }
  receive(id: string) { this.http.post(`/projects/${this.pid}/procurement/${id}/receive`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Recebido' }); this.ngOnInit(); } }); }
}
