import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { CalendarModule } from 'primeng/calendar';
import { InputNumberModule } from 'primeng/inputnumber';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageService, MenuItem } from 'primeng/api';
import { StepsModule } from 'primeng/steps';
import { StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent } from '../../shared/components';
import { TextareaModule } from 'primeng/textarea';

@Component({
  selector: 'app-measurement-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, RouterLink, TableModule, ButtonModule, DialogModule, CalendarModule, InputNumberModule, CheckboxModule, StepsModule, TextareaModule, StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Medições</h3>
      <div class="flex gap-2">
        <p-button label="Nova Medição" icon="pi pi-plus" size="small" (onClick)="openWizard()" />
        <p-button label="Importar Excel" icon="pi pi-file-excel" severity="secondary" size="small" (onClick)="showImportExcel = true" />
      </div>
    </div>

    @if (!loading() && measurements().length === 0) {
      <sp-empty title="Nenhuma medição" message="Crie a primeira medição para iniciar o faturamento" icon="chart-line" actionLabel="Criar Medição" (action)="openWizard()" />
    } @else {
      <p-table [value]="measurements()" [loading]="loading()" styleClass="p-datatable-sm" [rowHover]="true">
        <ng-template pTemplate="body" let-m>
          <tr style="cursor:pointer" [routerLink]="[m.id]">
            <td style="width:50px"><strong>#{{ m.number }}</strong></td>
            <td>{{ m.periodStart }} a {{ m.periodEnd }}</td>
            <td style="width:130px"><sp-currency [value]="m.grossAmount" /></td>
            <td style="width:130px"><sp-currency [value]="m.netAmount" /></td>
            <td style="width:120px"><sp-status [status]="m.status" /></td>
          </tr>
        </ng-template>
      </p-table>
    }

    <!-- WIZARD: Nova Medição -->
    <p-dialog header="Nova Medição" [(visible)]="wizardVisible" [style]="{width:'700px'}" [modal]="true">
      <p-steps [model]="wizardSteps" [activeIndex]="wizardStep()" [readonly]="true" styleClass="mb-4" />

      @if (wizardStep() === 0) {
        <div class="grid">
          <div class="col-4"><label>Número</label><p-inputNumber [(ngModel)]="newMeasurement.number" styleClass="w-full" /></div>
          <div class="col-4"><label>Início</label><p-calendar [(ngModel)]="newMeasurement.periodStart" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
          <div class="col-4"><label>Fim</label><p-calendar [(ngModel)]="newMeasurement.periodEnd" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
          <div class="col-4"><label>Retenção %</label><p-inputNumber [(ngModel)]="newMeasurement.retentionPct" [maxFractionDigits]="2" suffix="%" styleClass="w-full" /></div>
        </div>
      }

      @if (wizardStep() === 1) {
        <p class="text-muted mb-2">Selecione os itens e informe as quantidades medidas:</p>
        <p-table [value]="availableItems()" styleClass="p-datatable-sm" [scrollable]="true" scrollHeight="300px">
          <ng-template pTemplate="header"><tr><th style="width:40px"></th><th>Descrição</th><th style="width:80px">Saldo</th><th style="width:100px">Qtd. Medida</th></tr></ng-template>
          <ng-template pTemplate="body" let-item let-i="rowIndex">
            <tr>
              <td><p-checkbox [(ngModel)]="item.selected" [binary]="true" /></td>
              <td><span class="font-mono text-muted">{{ item.code }}</span> {{ item.description }}</td>
              <td class="text-right">{{ item.balanceQuantity | number:'1.2-2' }}</td>
              <td><p-inputNumber [(ngModel)]="item.quantity" [maxFractionDigits]="4" [disabled]="!item.selected" styleClass="w-full" size="small" /></td>
            </tr>
          </ng-template>
        </p-table>
      }

      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="wizardVisible = false" />
        @if (wizardStep() === 0) { <p-button label="Próximo" icon="pi pi-arrow-right" iconPos="right" (onClick)="wizardNext()" /> }
        @if (wizardStep() === 1) {
          <p-button label="Voltar" severity="secondary" icon="pi pi-arrow-left" (onClick)="wizardStep.set(0)" />
          <p-button label="Criar Medição" icon="pi pi-check" (onClick)="createMeasurement()" [loading]="creating()" />
        }
      </ng-template>
    </p-dialog>

    <!-- Import Excel Dialog -->
    <p-dialog header="Importar Medição via Excel" [(visible)]="showImportExcel" [style]="{width:'500px'}" [modal]="true">
      <p class="text-muted mb-3">Cole os dados no formato: Descrição | Quantidade | Preço Unitário (uma linha por item)</p>
      <textarea pTextarea [(ngModel)]="importText" rows="8" class="w-full font-mono" placeholder="Alvenaria;10.5;150.00&#10;Reboco;25.0;45.00"></textarea>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showImportExcel = false" />
        <p-button label="Importar" icon="pi pi-upload" (onClick)="importExcel()" [disabled]="!importText" />
      </ng-template>
    </p-dialog>
  `,
})
export class MeasurementListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  measurements = signal<any[]>([]);
  loading = signal(true);
  availableItems = signal<any[]>([]);
  wizardVisible = false;
  wizardStep = signal(0);
  creating = signal(false);
  wizardSteps: MenuItem[] = [{ label: 'Período' }, { label: 'Itens' }];
  newMeasurement: any = { number: 1, periodStart: null, periodEnd: null, retentionPct: 5 };

  ngOnInit() { this.load(); }

  load() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/measurements`).subscribe({
      next: res => { this.measurements.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openWizard() {
    this.wizardStep.set(0);
    this.newMeasurement = { number: this.measurements().length + 1, periodStart: null, periodEnd: null, retentionPct: 5 };
    this.wizardVisible = true;
  }

  wizardNext() {
    this.wizardStep.set(1);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any[]>(`/projects/${id}/measurements/available-items`).subscribe(items => {
      this.availableItems.set(items.map(i => ({ ...i, selected: false, quantity: 0 })));
    });
  }

  createMeasurement() {
    this.creating.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    const items = this.availableItems().filter(i => i.selected && i.quantity > 0)
      .map(i => ({ budgetItemId: i.budgetItemId, quantity: i.quantity }));
    const body = { ...this.newMeasurement, items };
    this.http.post(`/projects/${id}/measurements`, body).subscribe({
      next: () => { this.wizardVisible = false; this.creating.set(false); this.messages.add({ severity: 'success', summary: 'Medição criada' }); this.load(); },
      error: () => this.creating.set(false),
    });
  }

  // Sprint 5: Import Excel
  showImportExcel = false;
  importText = '';
  importExcel() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    const rows = this.importText.split('\n').filter(l => l.trim()).map(l => {
      const [description, quantity, unitPrice] = l.split(/[;|\t]/);
      return { description: description?.trim(), quantity: parseFloat(quantity), unitPrice: parseFloat(unitPrice) };
    }).filter(r => r.description && r.quantity && r.unitPrice);
    // First create a measurement, then import items
    const lastMid = this.measurements().length > 0 ? this.measurements()[0].id : null;
    if (lastMid) {
      this.http.post(`/projects/${id}/measurements/${lastMid}/import`, rows).subscribe({
        next: () => { this.showImportExcel = false; this.importText = ''; this.messages.add({ severity: 'success', summary: `${rows.length} itens importados` }); this.load(); },
      });
    }
  }
}
