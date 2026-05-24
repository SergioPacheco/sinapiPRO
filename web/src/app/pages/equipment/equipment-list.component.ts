import { Component, inject, OnInit, signal } from '@angular/core';
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
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-equipment-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, DropdownModule, TabViewModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Equipamentos / Frota</h2>
      <p-button label="Novo Equipamento" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <!-- Alertas de Manutenção -->
    @if (alerts().length > 0) {
      <div style="background:rgba(245,158,11,0.08);border:1px solid rgba(245,158,11,0.3);border-radius:6px;padding:10px 14px;margin-bottom:12px;font-size:12px;color:#f59e0b">
        ⚠️ {{ alerts().length }} equipamento(s) com manutenção pendente
      </div>
    }

    <p-tabView>
      <!-- Cadastro -->
      <p-tabPanel header="Equipamentos">
        <p-table [value]="equipment()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
          <ng-template pTemplate="header"><tr><th>Nome</th><th style="width:80px">Tipo</th><th style="width:80px">Placa</th><th class="text-right" style="width:80px">Custo/h</th><th style="width:80px">Status</th><th style="width:100px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-e>
            <tr>
              <td>{{ e.name }}</td>
              <td style="font-size:0.8rem">{{ e.type }}</td>
              <td class="font-mono" style="font-size:0.8rem">{{ e.licensePlate }}</td>
              <td class="text-right font-mono">{{ e.hourlyCost | number:'1.2-2' }}</td>
              <td style="font-size:0.8rem;color:{{ e.status === 'ACTIVE' ? '#10b981' : '#f59e0b' }}">{{ e.status }}</td>
              <td class="flex gap-1">
                <p-button icon="pi pi-clock" [text]="true" size="small" pTooltip="Registrar Uso" (onClick)="showUsage = true; selectedEquip = e" />
                <p-button icon="pi pi-wrench" [text]="true" size="small" pTooltip="Manutenção" (onClick)="scheduleMaintenance(e)" />
                <p-button icon="pi pi-car" [text]="true" size="small" pTooltip="Abastecimento" (onClick)="showFuel = true; selectedEquip = e" />
              </td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum equipamento</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Resumo de Custos -->
      <p-tabPanel header="Custos">
        <p-table [value]="costSummary()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
          <ng-template pTemplate="header"><tr><th>Equipamento</th><th class="text-right">Horas</th><th class="text-right">Custo Total</th><th class="text-right">Combustível</th></tr></ng-template>
          <ng-template pTemplate="body" let-c>
            <tr>
              <td>{{ c.equipmentName }}</td>
              <td class="text-right font-mono">{{ c.totalHours | number:'1.1-1' }}</td>
              <td class="text-right font-mono">{{ c.totalCost | number:'1.2-2' }}</td>
              <td class="text-right font-mono">{{ c.fuelCost | number:'1.2-2' }}</td>
            </tr>
          </ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>

    <!-- Novo Equipamento -->
    <p-dialog header="Novo Equipamento" [(visible)]="showNew" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Nome</label><input pInputText [(ngModel)]="form.name" class="w-full" placeholder="Retroescavadeira CAT 416" /></div>
        <div class="grid">
          <div class="col-4"><label>Tipo</label><p-dropdown [(ngModel)]="form.type" [options]="types" styleClass="w-full" /></div>
          <div class="col-4"><label>Placa</label><input pInputText [(ngModel)]="form.licensePlate" class="w-full" /></div>
          <div class="col-4"><label>Custo/Hora</label><p-inputNumber [(ngModel)]="form.hourlyCost" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Criar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>

    <!-- Registrar Uso -->
    <p-dialog header="Registrar Uso" [(visible)]="showUsage" [style]="{width:'350px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Horas</label><p-inputNumber [(ngModel)]="usageForm.hours" [maxFractionDigits]="1" styleClass="w-full" /></div>
        <div><label>Data</label><p-calendar [(ngModel)]="usageForm.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        <div><label>Obra/Atividade</label><input pInputText [(ngModel)]="usageForm.activity" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Registrar" icon="pi pi-check" (onClick)="registerUsage()" /></ng-template>
    </p-dialog>

    <!-- Abastecimento -->
    <p-dialog header="Abastecimento" [(visible)]="showFuel" [style]="{width:'350px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-6"><label>Litros</label><p-inputNumber [(ngModel)]="fuelForm.liters" [maxFractionDigits]="1" styleClass="w-full" /></div>
          <div class="col-6"><label>Valor Total</label><p-inputNumber [(ngModel)]="fuelForm.totalCost" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
        <div><label>Odômetro (km)</label><p-inputNumber [(ngModel)]="fuelForm.odometer" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Registrar" icon="pi pi-check" (onClick)="registerFuel()" /></ng-template>
    </p-dialog>
  `,
})
export class EquipmentListComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  equipment = signal<any[]>([]);
  costSummary = signal<any[]>([]);
  alerts = signal<any[]>([]);
  loading = signal(true);
  showNew = false; showUsage = false; showFuel = false;
  form: any = {};
  usageForm: any = { hours: 8 };
  fuelForm: any = {};
  selectedEquip: any = null;
  types = ['Escavadeira', 'Retroescavadeira', 'Caminhão', 'Betoneira', 'Guindaste', 'Compactador', 'Gerador', 'Outro'].map(t => ({ label: t, value: t }));

  ngOnInit() {
    this.http.get<any>('/equipment').subscribe({ next: r => { this.equipment.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
    this.http.get<any>('/equipment/cost-summary').subscribe({ next: r => this.costSummary.set(r || []), error: () => {} });
    this.http.get<any>('/equipment/maintenance-alerts').subscribe({ next: r => this.alerts.set(r || []), error: () => {} });
  }

  create() {
    this.http.post('/equipment', this.form).subscribe({ next: () => { this.showNew = false; this.form = {}; this.messages.add({ severity: 'success', summary: 'Equipamento cadastrado' }); this.ngOnInit(); } });
  }

  registerUsage() {
    const body = { ...this.usageForm, date: this.usageForm.date?.toISOString?.()?.slice(0, 10) };
    this.http.post(`/equipment/${this.selectedEquip.id}/usage`, body).subscribe({ next: () => { this.showUsage = false; this.messages.add({ severity: 'success', summary: 'Uso registrado' }); } });
  }

  registerFuel() {
    this.http.post(`/equipment/${this.selectedEquip.id}/fueling`, this.fuelForm).subscribe({ next: () => { this.showFuel = false; this.messages.add({ severity: 'success', summary: 'Abastecimento registrado' }); } });
  }

  scheduleMaintenance(e: any) {
    const date = prompt('Data da manutenção (YYYY-MM-DD):', new Date(Date.now() + 30*86400000).toISOString().slice(0, 10));
    if (date) this.http.post(`/equipment/${e.id}/maintenance-schedule`, { scheduledDate: date, type: 'PREVENTIVA' }).subscribe({ next: () => this.messages.add({ severity: 'success', summary: 'Manutenção agendada' }) });
  }
}
