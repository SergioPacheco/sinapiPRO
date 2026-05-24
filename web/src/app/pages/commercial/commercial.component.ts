import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { StepsModule } from 'primeng/steps';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService, MenuItem } from 'primeng/api';
import { StatusTagComponent, CurrencyDisplayComponent } from '../../shared/components';

@Component({
  selector: 'app-commercial',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, TabViewModule, ButtonModule, DialogModule, StepsModule, InputNumberModule, DropdownModule, InputTextModule, StatusTagComponent, CurrencyDisplayComponent],
  template: `
    <h3 style="margin:0 0 1rem">Comercial</h3>

    <!-- Master: Empreendimentos -->
    <div class="grid">
      <div class="col-12 md:col-4">
        <div class="master-list">
          <div class="master-header">Empreendimentos</div>
          @for (dev of developments(); track dev.id) {
            <div class="master-item" [class.active]="selectedDev?.id === dev.id" (click)="selectDev(dev)">
              <div class="item-name">{{ dev.name }}</div>
              <div class="item-meta">{{ dev.units || 0 }} unidades · {{ dev.soldUnits || 0 }} vendidas</div>
            </div>
          }
        </div>
      </div>

      <!-- Detail: Unidades + Contratos -->
      <div class="col-12 md:col-8">
        @if (selectedDev) {
          <p-tabView>
            <p-tabPanel header="Unidades">
              <p-table [value]="units()" styleClass="p-datatable-sm" [rowHover]="true">
                <ng-template pTemplate="header"><tr><th>Unidade</th><th style="width:100px">Área</th><th style="width:130px" class="text-right">Preço</th><th style="width:100px">Status</th><th style="width:80px"></th></tr></ng-template>
                <ng-template pTemplate="body" let-u>
                  <tr>
                    <td class="font-semibold">{{ u.identifier }}</td>
                    <td>{{ u.privateArea }}m²</td>
                    <td class="text-right"><sp-currency [value]="u.price" /></td>
                    <td><sp-status [status]="u.soldAt ? 'VENDIDA' : 'DISPONÍVEL'" /></td>
                    <td>@if (!u.soldAt) { <p-button icon="pi pi-shopping-cart" [text]="true" title="Vender" (onClick)="startSale(u)" /> }</td>
                  </tr>
                </ng-template>
              </p-table>
            </p-tabPanel>
            <p-tabPanel header="Contratos">
              <p-table [value]="contracts()" styleClass="p-datatable-sm">
                <ng-template pTemplate="header"><tr><th>Contrato</th><th>Comprador</th><th class="text-right" style="width:130px">Valor</th><th style="width:100px">Status</th></tr></ng-template>
                <ng-template pTemplate="body" let-c>
                  <tr><td>{{ c.contractNumber }}</td><td>{{ c.buyerName || '—' }}</td><td class="text-right"><sp-currency [value]="c.totalAmount" /></td><td><sp-status [status]="c.status" /></td></tr>
                </ng-template>
              </p-table>
            </p-tabPanel>
            <p-tabPanel header="Propostas">
              <p-table [value]="proposals()" styleClass="p-datatable-sm">
                <ng-template pTemplate="header"><tr><th>#</th><th>Cliente</th><th class="text-right" style="width:130px">Valor</th><th style="width:100px">Status</th></tr></ng-template>
                <ng-template pTemplate="body" let-r>
                  <tr><td>{{ r.number }}</td><td>{{ r.client }}</td><td class="text-right"><sp-currency [value]="r.value" /></td><td><sp-status [status]="r.status" /></td></tr>
                </ng-template>
              </p-table>
            </p-tabPanel>
          </p-tabView>
        } @else {
          <div class="text-center text-muted p-5">Selecione um empreendimento</div>
        }
      </div>
    </div>

    <!-- Stepper: Nova Venda -->
    <p-dialog header="Nova Venda" [(visible)]="saleVisible" [style]="{width:'600px'}" [modal]="true">
      <p-steps [model]="saleSteps" [activeIndex]="saleStep" [readonly]="false" styleClass="mb-4" />

      @if (saleStep === 0) {
        <div class="flex flex-column gap-3">
          <div><label>Unidade</label><input pInputText [value]="saleData.unitName" disabled class="w-full" /></div>
          <div><label>Comprador</label><input pInputText [(ngModel)]="saleData.buyerName" class="w-full" placeholder="Nome do comprador" /></div>
          <div><label>CPF/CNPJ</label><input pInputText [(ngModel)]="saleData.buyerDocument" class="w-full" /></div>
        </div>
      }
      @if (saleStep === 1) {
        <div class="flex flex-column gap-3">
          <div><label>Valor Total</label><p-inputNumber [(ngModel)]="saleData.totalAmount" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div><label>Entrada</label><p-inputNumber [(ngModel)]="saleData.downPayment" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="grid">
            <div class="col-6"><label>Parcelas</label><p-inputNumber [(ngModel)]="saleData.installments" styleClass="w-full" /></div>
            <div class="col-6"><label>Sistema</label><p-dropdown [options]="amortOptions" [(ngModel)]="saleData.amortization" styleClass="w-full" /></div>
          </div>
          <div><label>Índice Reajuste</label><p-dropdown [options]="indexOptions" [(ngModel)]="saleData.indexCode" styleClass="w-full" /></div>
        </div>
      }
      @if (saleStep === 2) {
        <div class="summary-card">
          <p><strong>Unidade:</strong> {{ saleData.unitName }}</p>
          <p><strong>Comprador:</strong> {{ saleData.buyerName }}</p>
          <p><strong>Valor:</strong> R$ {{ saleData.totalAmount | number:'1.2-2' }}</p>
          <p><strong>Entrada:</strong> R$ {{ saleData.downPayment | number:'1.2-2' }}</p>
          <p><strong>Parcelas:</strong> {{ saleData.installments }}x {{ saleData.amortization }}</p>
        </div>
      }

      <ng-template pTemplate="footer">
        @if (saleStep > 0) { <p-button label="Voltar" severity="secondary" (onClick)="saleStep = saleStep - 1" /> }
        @if (saleStep < 2) { <p-button label="Próximo" (onClick)="saleStep = saleStep + 1" /> }
        @if (saleStep === 2) { <p-button label="Gerar Contrato" icon="pi pi-check" severity="success" (onClick)="confirmSale()" /> }
      </ng-template>
    </p-dialog>
  `,
  styles: [`
    .master-list { border: 1px solid var(--sp-border); border-radius: var(--sp-radius); overflow: hidden; }
    .master-header { padding: 0.75rem 1rem; font-weight: 600; font-size: 13px; background: var(--sp-surface-ground); border-bottom: 1px solid var(--sp-border); }
    .master-item { padding: 0.75rem 1rem; cursor: pointer; border-bottom: 1px solid var(--sp-border); }
    .master-item:hover { background: var(--sp-surface-hover); }
    .master-item.active { background: color-mix(in srgb, var(--sp-primary) 10%, transparent); border-left: 3px solid var(--sp-primary); }
    .item-name { font-weight: 600; font-size: 14px; } .item-meta { font-size: 12px; color: var(--sp-text-muted); margin-top: 2px; }
    .summary-card { background: var(--sp-surface-ground); border-radius: var(--sp-radius); padding: 1rem; }
    .summary-card p { margin: 0.5rem 0; }
  `],
})
export class CommercialComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  developments = signal<any[]>([]);
  units = signal<any[]>([]);
  contracts = signal<any[]>([]);
  proposals = signal<any[]>([]);
  selectedDev: any = null;

  saleVisible = false;
  saleStep = 0;
  saleSteps: MenuItem[] = [{ label: 'Comprador' }, { label: 'Condições' }, { label: 'Revisão' }];
  saleData: any = {};
  amortOptions = [{ label: 'Price', value: 'PRICE' }, { label: 'SAC', value: 'SAC' }];
  indexOptions = [{ label: 'INCC', value: 'INCC' }, { label: 'IGPM', value: 'IGPM' }, { label: 'Sem reajuste', value: null }];

  ngOnInit() {
    this.http.get<any>('/commercial/developments').subscribe(res => this.developments.set(res.content || res));
    this.http.get<any>('/commercial/proposals').subscribe(res => this.proposals.set(res.content || res));
  }

  selectDev(dev: any) {
    this.selectedDev = dev;
    this.http.get<any>(`/commercial/developments/${dev.id}/units`).subscribe(res => this.units.set(res.content || res));
    this.http.get<any>(`/developments/${dev.id}/sales/contracts`).subscribe({ next: res => this.contracts.set(res.content || res), error: () => this.contracts.set([]) });
  }

  startSale(unit: any) {
    this.saleData = { unitId: unit.id, unitName: unit.identifier, totalAmount: unit.price, downPayment: 0, installments: 60, amortization: 'PRICE', indexCode: 'INCC', buyerName: '', buyerDocument: '' };
    this.saleStep = 0;
    this.saleVisible = true;
  }

  confirmSale() {
    this.http.post(`/developments/${this.selectedDev.id}/sales/contracts`, {
      contractNumber: 'CV-' + Date.now().toString().slice(-6),
      contractDate: new Date().toISOString().split('T')[0],
      totalAmount: this.saleData.totalAmount,
      installmentCount: this.saleData.installments,
      amortizationType: this.saleData.amortization,
      downPayment: this.saleData.downPayment,
    }).subscribe({
      next: () => { this.saleVisible = false; this.messages.add({ severity: 'success', summary: 'Contrato gerado!' }); this.selectDev(this.selectedDev); },
      error: () => this.messages.add({ severity: 'error', summary: 'Erro ao gerar contrato' }),
    });
  }
}
