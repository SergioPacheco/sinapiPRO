import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { MessageService } from 'primeng/api';
import { StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent, InlineCreateDrawerComponent } from '../../shared/components';

@Component({
  selector: 'app-procurement-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, AutoCompleteModule, StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent, InlineCreateDrawerComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Suprimentos</h3>
      <div class="flex gap-2">
        <p-button [icon]="viewMode === 'kanban' ? 'pi pi-th-large' : 'pi pi-list'" [text]="true" (onClick)="viewMode = viewMode === 'kanban' ? 'table' : 'kanban'" title="Alternar visualização" />
        <p-button label="Novo Pedido" icon="pi pi-plus" size="small" (onClick)="showNewOrder = true" />
      </div>
    </div>

    @if (!loading() && orders().length === 0) {
      <sp-empty title="Nenhum pedido de compra" message="Crie um pedido para iniciar o processo de suprimentos" icon="truck" actionLabel="Criar Pedido" (action)="showNewOrder = true" />
    } @else if (viewMode === 'kanban') {
      <!-- KANBAN VIEW -->
      <div class="kanban-board">
        @for (col of kanbanColumns; track col.status) {
          <div class="kanban-col">
            <div class="kanban-col-header">{{ col.label }} <span class="badge">{{ getByStatus(col.status).length }}</span></div>
            @for (o of getByStatus(col.status); track o.id) {
              <div class="kanban-card" (click)="openReceiving(o)">
                <div class="card-number">{{ o.number }}</div>
                <div class="card-desc">{{ o.description }}</div>
                <div class="card-supplier">{{ o.supplierName }}</div>
                <div class="card-footer"><sp-currency [value]="o.totalAmount" /> <span class="text-muted">{{ o.expectedDeliveryDate }}</span></div>
              </div>
            }
          </div>
        }
      </div>
    } @else {
      <!-- TABLE VIEW -->
      <p-table [value]="orders()" [loading]="loading()" styleClass="p-datatable-sm" [rowHover]="true">
        <ng-template pTemplate="header">
          <tr><th style="width:90px">Número</th><th>Descrição</th><th>Fornecedor</th><th style="width:120px" class="text-right">Valor</th><th style="width:110px">Entrega</th><th style="width:110px">Status</th><th style="width:80px"></th></tr>
        </ng-template>
        <ng-template pTemplate="body" let-o>
          <tr>
            <td class="font-mono">{{ o.number }}</td>
            <td>{{ o.description }}</td>
            <td>{{ o.supplierName }}</td>
            <td class="text-right"><sp-currency [value]="o.totalAmount" /></td>
            <td>{{ o.expectedDeliveryDate }}</td>
            <td><sp-status [status]="o.status" /></td>
            <td>
              @if (o.status === 'APPROVED') {
                <p-button icon="pi pi-check-square" [text]="true" severity="success" title="Registrar Recebimento" (onClick)="openReceiving(o)" />
              }
            </td>
          </tr>
        </ng-template>
      </p-table>
    }

    <!-- Dialog: Novo Pedido (com autocomplete de fornecedor + drawer inline) -->
    <p-dialog header="Novo Pedido de Compra" [(visible)]="showNewOrder" [style]="{width:'550px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div>
          <label>Fornecedor</label>
          <div class="flex gap-2">
            <p-autoComplete [(ngModel)]="selectedSupplier" [suggestions]="supplierSuggestions()" (completeMethod)="searchSupplier($event)" field="name" styleClass="flex-1" placeholder="Buscar fornecedor..." />
            <p-button icon="pi pi-plus" severity="secondary" title="Cadastrar novo" (onClick)="supplierDrawer.open()" />
          </div>
        </div>
        <div><label>Descrição</label><input pInputText [(ngModel)]="newOrder.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Quantidade</label><p-inputNumber [(ngModel)]="newOrder.quantity" styleClass="w-full" /></div>
          <div class="col-6"><label>Preço Unitário</label><p-inputNumber [(ngModel)]="newOrder.unitPrice" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNewOrder = false" />
        <p-button label="Criar Pedido" icon="pi pi-check" (onClick)="createOrder()" />
      </ng-template>
    </p-dialog>

    <!-- Drawer: Cadastrar Fornecedor Inline -->
    <sp-drawer #supplierDrawer header="Novo Fornecedor" (save)="saveSupplier()">
      <div class="flex flex-column gap-3">
        <div><label>Nome/Razão Social</label><input pInputText [(ngModel)]="newSupplier.name" class="w-full" /></div>
        <div><label>CNPJ/CPF</label><input pInputText [(ngModel)]="newSupplier.document" class="w-full" /></div>
        <div><label>E-mail</label><input pInputText [(ngModel)]="newSupplier.email" class="w-full" /></div>
        <div><label>Telefone</label><input pInputText [(ngModel)]="newSupplier.phone" class="w-full" /></div>
      </div>
    </sp-drawer>

    <!-- Dialog: Registrar Recebimento -->
    <p-dialog header="Registrar Recebimento" [(visible)]="showReceiving" [style]="{width:'400px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Quantidade Recebida</label><p-inputNumber [(ngModel)]="receivingQty" styleClass="w-full" /></div>
        <div><label>Nota Fiscal</label><input pInputText [(ngModel)]="receivingNf" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Confirmar Recebimento" icon="pi pi-check" (onClick)="confirmReceiving()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ProcurementListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);
  @ViewChild('supplierDrawer') supplierDrawer!: InlineCreateDrawerComponent;

  orders = signal<any[]>([]);
  loading = signal(true);
  viewMode: 'kanban' | 'table' = 'kanban';
  kanbanColumns = [
    { status: 'PENDING', label: 'Pendente' },
    { status: 'APPROVED', label: 'Aprovado' },
    { status: 'PARTIAL', label: 'Parcial' },
    { status: 'RECEIVED', label: 'Recebido' },
  ];
  showNewOrder = false;
  showReceiving = false;
  selectedSupplier: any = null;
  supplierSuggestions = signal<any[]>([]);
  newOrder: any = { description: '', quantity: 1, unitPrice: 0 };
  newSupplier: any = { name: '', document: '', email: '', phone: '' };
  receivingOrder: any = null;
  receivingQty = 0;
  receivingNf = '';

  ngOnInit() { this.load(); }

  getByStatus(status: string) { return this.orders().filter(o => o.status === status); }

  load() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/procurement/orders`).subscribe({
      next: res => { this.orders.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  searchSupplier(event: any) {
    this.http.get<any>(`/suppliers?search=${event.query}&page=0&size=10`).subscribe(
      res => this.supplierSuggestions.set(res.content || res)
    );
  }

  createOrder() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    const body = { ...this.newOrder, supplierId: this.selectedSupplier?.id };
    this.http.post(`/projects/${id}/procurement/orders`, body).subscribe(() => {
      this.showNewOrder = false;
      this.messages.add({ severity: 'success', summary: 'Pedido criado' });
      this.load();
    });
  }

  saveSupplier() {
    this.http.post<any>('/suppliers', this.newSupplier).subscribe(s => {
      this.selectedSupplier = s;
      this.supplierDrawer.close();
      this.messages.add({ severity: 'success', summary: 'Fornecedor cadastrado' });
    });
  }

  openReceiving(order: any) { this.receivingOrder = order; this.receivingQty = 0; this.receivingNf = ''; this.showReceiving = true; }

  confirmReceiving() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/procurement/orders/${this.receivingOrder.id}/receiving`, { quantityReceived: this.receivingQty, invoiceNumber: this.receivingNf }).subscribe(() => {
      this.showReceiving = false;
      this.messages.add({ severity: 'success', summary: 'Recebimento registrado' });
      this.load();
    });
  }
}
