import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { TextareaModule } from 'primeng/textarea';
import { CalendarModule } from 'primeng/calendar';
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';
import { StatusTagComponent, EmptyStateComponent } from '../../shared/components';

@Component({
  selector: 'app-daily-log-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, DropdownModule, InputTextModule, InputNumberModule, TextareaModule, CalendarModule, TabViewModule, StatusTagComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Diário de Obra</h3>
      <p-button label="Registrar Hoje" icon="pi pi-plus" size="small" (onClick)="openForm()" />
    </div>

    @if (!loading() && logs().length === 0) {
      <sp-empty title="Nenhum registro" message="Registre o primeiro dia de obra" icon="calendar" actionLabel="Registrar Hoje" (action)="openForm()" />
    } @else {
      <p-table [value]="logs()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="15">
        <ng-template pTemplate="header">
          <tr><th style="width:110px">Data</th><th>Clima</th><th style="width:80px">Efetivo</th><th>Observações</th></tr>
        </ng-template>
        <ng-template pTemplate="body" let-l>
          <tr>
            <td><strong>{{ l.logDate }}</strong></td>
            <td><i class="pi pi-sun"></i> {{ l.weatherMorning }} / <i class="pi pi-cloud"></i> {{ l.weatherAfternoon }}</td>
            <td>{{ l.workerCount }}</td>
            <td class="text-muted" style="max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ l.notes }}</td>
          </tr>
        </ng-template>
      </p-table>
    }

    <!-- Form Dialog -->
    <p-dialog header="Registro Diário" [(visible)]="dialogVisible" [style]="{width:'750px'}" [modal]="true">
      <p-tabView>
        <p-tabPanel header="Geral">
          <div class="grid">
            <div class="col-12 md:col-4"><label>Data</label><p-calendar [(ngModel)]="form.logDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
            <div class="col-12 md:col-4"><label>Clima Manhã</label><p-dropdown [(ngModel)]="form.weatherMorning" [options]="weatherOpts" styleClass="w-full" /></div>
            <div class="col-12 md:col-4"><label>Clima Tarde</label><p-dropdown [(ngModel)]="form.weatherAfternoon" [options]="weatherOpts" styleClass="w-full" /></div>
            <div class="col-12 md:col-4"><label>Efetivo</label><p-inputNumber [(ngModel)]="form.workerCount" styleClass="w-full" /></div>
            <div class="col-12"><label>Observações</label><textarea pTextarea [(ngModel)]="form.notes" rows="3" class="w-full"></textarea></div>
          </div>
        </p-tabPanel>
        <p-tabPanel header="Mão de Obra">
          <p-button label="Adicionar" icon="pi pi-plus" size="small" (onClick)="form.labor.push({name:'',hours:0})" class="mb-2" />
          @for (item of form.labor; track $index) {
            <div class="grid mb-1">
              <div class="col-8"><input pInputText [(ngModel)]="item.name" placeholder="Nome" class="w-full" /></div>
              <div class="col-3"><p-inputNumber [(ngModel)]="item.hours" placeholder="Horas" styleClass="w-full" /></div>
              <div class="col-1"><p-button icon="pi pi-trash" severity="danger" [text]="true" (onClick)="form.labor.splice($index,1)" /></div>
            </div>
          }
        </p-tabPanel>
        <p-tabPanel header="Equipamentos">
          <p-button label="Adicionar" icon="pi pi-plus" size="small" (onClick)="form.equipment.push({name:'',hours:0})" class="mb-2" />
          @for (item of form.equipment; track $index) {
            <div class="grid mb-1">
              <div class="col-8"><input pInputText [(ngModel)]="item.name" placeholder="Equipamento" class="w-full" /></div>
              <div class="col-3"><p-inputNumber [(ngModel)]="item.hours" placeholder="Horas" styleClass="w-full" /></div>
              <div class="col-1"><p-button icon="pi pi-trash" severity="danger" [text]="true" (onClick)="form.equipment.splice($index,1)" /></div>
            </div>
          }
        </p-tabPanel>
        <p-tabPanel header="Materiais">
          <p-button label="Adicionar" icon="pi pi-plus" size="small" (onClick)="form.materials.push({description:'',quantity:0,type:'IN'})" class="mb-2" />
          @for (item of form.materials; track $index) {
            <div class="grid mb-1">
              <div class="col-5"><input pInputText [(ngModel)]="item.description" placeholder="Descrição" class="w-full" /></div>
              <div class="col-3"><p-inputNumber [(ngModel)]="item.quantity" placeholder="Qtd" styleClass="w-full" /></div>
              <div class="col-3"><p-dropdown [(ngModel)]="item.type" [options]="materialTypes" styleClass="w-full" /></div>
              <div class="col-1"><p-button icon="pi pi-trash" severity="danger" [text]="true" (onClick)="form.materials.splice($index,1)" /></div>
            </div>
          }
        </p-tabPanel>
        <p-tabPanel header="Ocorrências">
          <p-button label="Adicionar" icon="pi pi-plus" size="small" (onClick)="form.occurrences.push({description:'',severity:'LOW'})" class="mb-2" />
          @for (item of form.occurrences; track $index) {
            <div class="grid mb-1">
              <div class="col-7"><input pInputText [(ngModel)]="item.description" placeholder="Descrição" class="w-full" /></div>
              <div class="col-4"><p-dropdown [(ngModel)]="item.severity" [options]="severityOpts" styleClass="w-full" /></div>
              <div class="col-1"><p-button icon="pi pi-trash" severity="danger" [text]="true" (onClick)="form.occurrences.splice($index,1)" /></div>
            </div>
          }
        </p-tabPanel>
      </p-tabView>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="dialogVisible = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" [loading]="saving()" />
      </ng-template>
    </p-dialog>
  `,
})
export class DailyLogListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  logs = signal<any[]>([]);
  loading = signal(true);
  saving = signal(false);
  dialogVisible = false;
  form: any = {};

  weatherOpts = ['BOM', 'NUBLADO', 'CHUVOSO'].map(v => ({ label: v, value: v }));
  materialTypes = [{ label: 'Entrada', value: 'IN' }, { label: 'Saída', value: 'OUT' }];
  severityOpts = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map(v => ({ label: v, value: v }));

  ngOnInit() { this.load(); }

  load() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/daily-logs`).subscribe({
      next: res => { this.logs.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openForm() {
    this.form = { logDate: new Date(), weatherMorning: 'BOM', weatherAfternoon: 'BOM', workerCount: 0, notes: '', labor: [], equipment: [], materials: [], occurrences: [] };
    this.dialogVisible = true;
  }

  save() {
    this.saving.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/daily-logs`, this.form).subscribe({
      next: () => { this.dialogVisible = false; this.saving.set(false); this.messages.add({ severity: 'success', summary: 'Registro salvo' }); this.load(); },
      error: () => this.saving.set(false),
    });
  }
}
