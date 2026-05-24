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
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-procurement-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DropdownModule, TabViewModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Suprimentos</h2>
      <div class="flex gap-2">
        <p-button label="Nova Requisição" icon="pi pi-plus" size="small" (onClick)="showNewReq = true" />
        <p-button label="Pedidos em Atraso" icon="pi pi-exclamation-triangle" size="small" severity="warn" (onClick)="loadOverdue()" />
      </div>
    </div>

    <p-tabView>
      <!-- Tab 1: Requisições -->
      <p-tabPanel header="Requisições">
        <p-table [value]="requests()" [loading]="loadingReq()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:80px" class="text-right">Qtd</th><th style="width:100px" class="text-right">Valor Est.</th><th style="width:80px">Status</th><th style="width:100px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-r>
            <tr>
              <td>{{ r.description }}</td>
              <td class="text-right font-mono">{{ r.quantity }}</td>
              <td class="text-right font-mono">{{ r.estimatedValue | number:'1.2-2' }}</td>
              <td><sp-status [status]="r.status || 'DRAFT'" /></td>
              <td class="flex gap-1">
                @if (r.status === 'DRAFT' || r.status === 'PENDING') { <p-button icon="pi pi-comments" [text]="true" size="small" pTooltip="Gerar Cotação" (onClick)="generateQuotation(r.id)" /> }
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:1.5rem;color:var(--sp-text-muted)">Nenhuma requisição</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Tab 2: Cotações -->
      <p-tabPanel header="Cotações">
        <p-table [value]="quotations()" [loading]="loadingQuot()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:100px">Fornecedores</th><th style="width:80px">Status</th><th style="width:160px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-q>
            <tr>
              <td>{{ q.description }}</td>
              <td class="text-center">{{ q.responseCount || 0 }} respostas</td>
              <td><sp-status [status]="q.status || 'OPEN'" /></td>
              <td class="flex gap-1">
                <p-button icon="pi pi-envelope" [text]="true" size="small" pTooltip="Enviar Email" (onClick)="sendEmail(q.id)" />
                <p-button icon="pi pi-chart-bar" [text]="true" size="small" pTooltip="Mapa Comparativo" (onClick)="viewAnalysis(q.id)" />
                <p-button icon="pi pi-shopping-cart" [text]="true" size="small" severity="success" pTooltip="Gerar Pedido" (onClick)="generateOrder(q.id)" />
                <p-button icon="pi pi-file-pdf" [text]="true" size="small" pTooltip="PDF" (onClick)="quotationPdf(q.id)" />
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center" style="padding:1.5rem;color:var(--sp-text-muted)">Nenhuma cotação</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Tab 3: Pedidos de Compra -->
      <p-tabPanel header="Pedidos">
        <p-table [value]="orders()" [loading]="loadingOrd()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
          <ng-template pTemplate="header"><tr><th style="width:80px">Número</th><th>Fornecedor</th><th>Descrição</th><th class="text-right" style="width:100px">Valor</th><th style="width:80px">Status</th><th style="width:120px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-o>
            <tr>
              <td class="font-mono" style="font-size:0.8rem">{{ o.orderNumber }}</td>
              <td>{{ o.supplierName }}</td>
              <td style="font-size:0.85rem">{{ o.description }}</td>
              <td class="text-right font-mono">{{ o.totalValue | number:'1.2-2' }}</td>
              <td><sp-status [status]="o.status" /></td>
              <td class="flex gap-1">
                @if (o.status === 'DRAFT') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" pTooltip="Aprovar" (onClick)="approveOrder(o.id)" /> }
                @if (o.status === 'APPROVED') { <p-button icon="pi pi-inbox" [text]="true" size="small" pTooltip="Receber" (onClick)="receiveOrder(o.id)" /> }
                <p-button icon="pi pi-file-pdf" [text]="true" size="small" pTooltip="PDF Pedido" (onClick)="orderPdf(o.id)" />
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:1.5rem;color:var(--sp-text-muted)">Nenhum pedido</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Tab 4: Limite de Compra -->
      <p-tabPanel header="Limites">
        <div style="padding:1rem;color:var(--sp-text-muted);font-size:12px">
          <p>Limite de compra por obra. Pedidos acima do limite requerem autorização.</p>
          <p-button label="Configurar Limite" icon="pi pi-cog" size="small" (onClick)="showLimit = true" />
        </div>
      </p-tabPanel>
    </p-tabView>

    <!-- Dialog: Nova Requisição -->
    <p-dialog header="Nova Requisição" [(visible)]="showNewReq" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Descrição / Material</label><input pInputText [(ngModel)]="reqForm.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-4"><label>Quantidade</label><p-inputNumber [(ngModel)]="reqForm.quantity" styleClass="w-full" /></div>
          <div class="col-4"><label>Unidade</label><input pInputText [(ngModel)]="reqForm.unit" class="w-full" placeholder="UN" /></div>
          <div class="col-4"><label>Valor Estimado</label><p-inputNumber [(ngModel)]="reqForm.estimatedValue" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNewReq = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="createRequest()" />
      </ng-template>
    </p-dialog>

    <!-- Dialog: Limite de Compra -->
    <p-dialog header="Limite de Compra" [(visible)]="showLimit" [style]="{width:'380px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Valor Limite (R$)</label><p-inputNumber [(ngModel)]="limitValue" mode="currency" currency="BRL" styleClass="w-full" /></div>
        <p style="color:var(--sp-text-muted)">Pedidos acima deste valor precisarão de autorização do gestor.</p>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveLimit()" />
      </ng-template>
    </p-dialog>

    <!-- Dialog: Pedidos em Atraso -->
    <p-dialog header="Pedidos em Atraso" [(visible)]="showOverdue" [style]="{width:'550px'}" [modal]="true">
      <p-table [value]="overdueOrders()" styleClass="p-datatable-sm" [rowHover]="true">
        <ng-template pTemplate="header"><tr><th>Pedido</th><th>Fornecedor</th><th>Prazo</th><th>Dias Atraso</th></tr></ng-template>
        <ng-template pTemplate="body" let-o>
          <tr><td class="font-mono">{{ o.orderNumber }}</td><td>{{ o.supplierName }}</td><td>{{ o.expectedDate }}</td><td style="color:#ef4444;font-weight:600">{{ o.daysOverdue }}</td></tr>
        </ng-template>
      </p-table>
    </p-dialog>
  `,
})
export class ProcurementListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  requests = signal<any[]>([]);
  quotations = signal<any[]>([]);
  orders = signal<any[]>([]);
  overdueOrders = signal<any[]>([]);
  loadingReq = signal(true);
  loadingQuot = signal(true);
  loadingOrd = signal(true);
  showNewReq = false;
  showLimit = false;
  showOverdue = false;
  reqForm: any = {};
  limitValue = 50000;

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/procurement/requests`).subscribe({ next: r => { this.requests.set(r.content || r || []); this.loadingReq.set(false); }, error: () => this.loadingReq.set(false) });
    this.http.get<any>(`/projects/${this.pid}/procurement/quotations`).subscribe({ next: r => { this.quotations.set(r.content || r || []); this.loadingQuot.set(false); }, error: () => this.loadingQuot.set(false) });
    this.http.get<any>(`/projects/${this.pid}/procurement/orders`).subscribe({ next: r => { this.orders.set(r.content || r || []); this.loadingOrd.set(false); }, error: () => this.loadingOrd.set(false) });
  }

  createRequest() {
    this.http.post(`/projects/${this.pid}/procurement/requests`, this.reqForm).subscribe({
      next: () => { this.showNewReq = false; this.reqForm = {}; this.messages.add({ severity: 'success', summary: 'Requisição criada' }); this.ngOnInit(); },
    });
  }

  generateQuotation(reqId: string) {
    this.http.post(`/projects/${this.pid}/procurement/requests/${reqId}/quotations`, {}).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Cotação gerada' }); this.ngOnInit(); },
    });
  }

  sendEmail(quotId: string) {
    this.http.post(`/projects/${this.pid}/procurement/quotations/${quotId}/send-email`, {}).subscribe({
      next: () => this.messages.add({ severity: 'success', summary: 'Email enviado aos fornecedores' }),
    });
  }

  viewAnalysis(quotId: string) {
    this.http.get<any>(`/projects/${this.pid}/procurement/quotations/${quotId}/analysis`).subscribe({
      next: r => this.messages.add({ severity: 'info', summary: 'Análise', detail: JSON.stringify(r).slice(0, 200), life: 10000 }),
    });
  }

  generateOrder(quotId: string) {
    this.http.post(`/projects/${this.pid}/procurement/quotations/${quotId}/generate-order`, {}).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Pedido gerado a partir da cotação' }); this.ngOnInit(); },
    });
  }

  approveOrder(orderId: string) {
    this.http.post(`/projects/${this.pid}/procurement/orders/${orderId}/approve`, {}).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Pedido aprovado' }); this.ngOnInit(); },
    });
  }

  receiveOrder(orderId: string) {
    this.http.post(`/projects/${this.pid}/procurement/orders/${orderId}/receive`, {}).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Material recebido' }); this.ngOnInit(); },
    });
  }

  loadOverdue() {
    this.http.get<any>(`/projects/${this.pid}/procurement/orders/overdue`).subscribe({
      next: r => { this.overdueOrders.set(r.content || r || []); this.showOverdue = true; },
    });
  }

  saveLimit() {
    this.http.post(`/projects/${this.pid}/procurement/limits`, { limitAmount: this.limitValue }).subscribe({
      next: () => { this.showLimit = false; this.messages.add({ severity: 'success', summary: 'Limite configurado' }); },
    });
  }

  quotationPdf(id: string) { window.open(`/api/v1/projects/${this.pid}/procurement/quotations/${id}/reports/comparative-map.pdf`, '_blank'); }
  orderPdf(id: string) { window.open(`/api/v1/projects/${this.pid}/procurement/orders/${id}/reports/order.pdf`, '_blank'); }
}
