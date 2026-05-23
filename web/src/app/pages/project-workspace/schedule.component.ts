import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, ProgressBarModule, TagModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, TabViewModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Cronograma</h3>
      <div class="flex gap-2">
        <p-button label="Nova Atividade" icon="pi pi-plus" size="small" (onClick)="showCreate = true" />
        <p-button label="Feriados" icon="pi pi-calendar" severity="secondary" size="small" (onClick)="loadHolidays()" />
        <p-button label="Distribuir Datas" icon="pi pi-sort-amount-down" severity="secondary" size="small" (onClick)="distributeDates()" />
        <p-button icon="pi pi-file-pdf" severity="help" size="small" pTooltip="Cronograma PDF" (onClick)="downloadPdf()" />
      </div>
    </div>

    <p-tabView>
      <!-- Gantt / Activities -->
      <p-tabPanel header="Atividades">
        <p-table [value]="activities()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="20">
          <ng-template pTemplate="header"><tr><th>Atividade</th><th style="width:100px">Início</th><th style="width:100px">Fim</th><th style="width:80px">Duração</th><th style="width:150px">Progresso</th><th style="width:80px">Crítico</th></tr></ng-template>
          <ng-template pTemplate="body" let-a>
            <tr [class.bg-red-50]="a.isCritical">
              <td>{{ a.name }}</td><td>{{ a.plannedStart }}</td><td>{{ a.plannedEnd }}</td><td>{{ a.durationDays }}d</td>
              <td><p-progressBar [value]="a.progressPct" [showValue]="true" [style]="{'height':'16px'}" /></td>
              <td>@if (a.isCritical) { <p-tag value="Crítico" severity="danger" /> }</td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhuma atividade</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Tracking: Previsto × Realizado -->
      <p-tabPanel header="Previsto × Realizado">
        <p-table [value]="tracking()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Atividade</th><th class="text-right" style="width:100px">Previsto %</th><th class="text-right" style="width:100px">Realizado %</th><th class="text-right" style="width:100px">Desvio</th></tr></ng-template>
          <ng-template pTemplate="body" let-t>
            <tr><td>{{ t.activityName }}</td><td class="text-right">{{ t.plannedPct }}%</td><td class="text-right">{{ t.actualPct }}%</td>
              <td class="text-right" [class.text-red-500]="t.actualPct < t.plannedPct" [class.text-green-500]="t.actualPct >= t.plannedPct">{{ t.actualPct - t.plannedPct }}%</td>
            </tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhum dado</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Dependencies -->
      <p-tabPanel header="Dependências">
        <p-table [value]="dependencies()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Predecessora</th><th>Sucessora</th><th style="width:80px">Tipo</th></tr></ng-template>
          <ng-template pTemplate="body" let-d><tr><td>{{ d.predecessor }}</td><td>{{ d.successor }}</td><td><p-tag [value]="d.type" /></td></tr></ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="3" class="text-center text-muted p-3">Nenhuma</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- Baselines -->
      <p-tabPanel header="Baselines">
        <div class="flex justify-content-end mb-2"><p-button label="Salvar Baseline" icon="pi pi-save" size="small" (onClick)="saveBaseline()" /></div>
        <p-table [value]="baselines()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th style="width:150px">Data</th></tr></ng-template>
          <ng-template pTemplate="body" let-b><tr><td>{{ b.name }}</td><td>{{ b.createdAt }}</td></tr></ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="2" class="text-center text-muted p-3">Nenhuma</td></tr></ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>

    <!-- Create Activity Dialog -->
    <p-dialog header="Nova Atividade" [(visible)]="showCreate" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Nome</label><input pInputText [(ngModel)]="newActivity.name" class="w-full" /></div>
        <div class="grid">
          <div class="col-4"><label>Início</label><p-calendar [(ngModel)]="newActivity.plannedStart" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
          <div class="col-4"><label>Fim</label><p-calendar [(ngModel)]="newActivity.plannedEnd" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
          <div class="col-4"><label>Duração (dias)</label><p-inputNumber [(ngModel)]="newActivity.durationDays" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showCreate = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="createActivity()" [disabled]="!newActivity.name" />
      </ng-template>
    </p-dialog>

    <!-- Holidays Dialog -->
    <p-dialog header="Feriados" [(visible)]="showHolidays" [style]="{width:'450px'}" [modal]="true">
      <div class="flex gap-2 mb-3">
        <p-calendar [(ngModel)]="newHoliday.date" dateFormat="yy-mm-dd" styleClass="flex-1" placeholder="Data" />
        <input pInputText [(ngModel)]="newHoliday.description" placeholder="Descrição" class="flex-1" />
        <p-button icon="pi pi-plus" (onClick)="addHoliday()" [disabled]="!newHoliday.date" />
      </div>
      <p-table [value]="holidays()" styleClass="p-datatable-sm">
        <ng-template pTemplate="body" let-h><tr><td>{{ h.date }}</td><td>{{ h.description }}</td></tr></ng-template>
        <ng-template pTemplate="emptymessage"><tr><td colspan="2" class="text-center text-muted p-3">Nenhum feriado</td></tr></ng-template>
      </p-table>
    </p-dialog>
  `,
})
export class ScheduleComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  activities = signal<any[]>([]);
  tracking = signal<any[]>([]);
  dependencies = signal<any[]>([]);
  baselines = signal<any[]>([]);
  holidays = signal<any[]>([]);
  loading = signal(true);

  showCreate = false;
  showHolidays = false;
  newActivity: any = { name: '', plannedStart: null, plannedEnd: null, durationDays: 5 };
  newHoliday: any = { date: null, description: '' };

  private get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any[]>(`/projects/${this.projectId}/schedule`).subscribe({
      next: res => { this.activities.set(res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
    this.http.get<any>(`/projects/${this.projectId}/schedule/dependencies`).subscribe(r => this.dependencies.set(r.content || r));
    this.http.get<any>(`/projects/${this.projectId}/schedule/baselines`).subscribe(r => this.baselines.set(r.content || r));
    this.http.get<any>(`/projects/${this.projectId}/schedule/tracking`).subscribe({ next: r => this.tracking.set(r.content || r), error: () => {} });
  }

  createActivity() {
    this.http.post(`/projects/${this.projectId}/schedule`, this.newActivity).subscribe(() => {
      this.showCreate = false; this.messages.add({ severity: 'success', summary: 'Atividade criada' }); this.ngOnInit();
    });
  }

  saveBaseline() {
    this.http.post<any>(`/projects/${this.projectId}/schedule/baselines`, {}).subscribe(b => {
      this.baselines.update(list => [b, ...list]); this.messages.add({ severity: 'success', summary: 'Baseline salva' });
    });
  }

  distributeDates() {
    this.http.post(`/projects/${this.projectId}/schedule/distribute-dates`, {}).subscribe(() => {
      this.messages.add({ severity: 'success', summary: 'Datas distribuídas' }); this.ngOnInit();
    });
  }

  loadHolidays() {
    this.http.get<any[]>(`/projects/${this.projectId}/schedule/holidays`).subscribe(h => { this.holidays.set(h); this.showHolidays = true; });
  }

  addHoliday() {
    const date = this.newHoliday.date instanceof Date ? this.newHoliday.date.toISOString().slice(0, 10) : this.newHoliday.date;
    this.http.post(`/projects/${this.projectId}/schedule/holidays`, { date, description: this.newHoliday.description }).subscribe(() => {
      this.newHoliday = { date: null, description: '' }; this.loadHolidays();
    });
  }

  downloadPdf() { window.open(`/api/v1/projects/${this.projectId}/schedule/reports/physical-financial.pdf`, '_blank'); }
}
