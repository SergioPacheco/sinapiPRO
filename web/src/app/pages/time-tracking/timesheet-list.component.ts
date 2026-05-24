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
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-timesheet-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Mão de Obra</h2>
      <div class="flex gap-2">
        <p-button label="Apontar Horas" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
        <p-button label="Competências" icon="pi pi-calendar" size="small" severity="secondary" (onClick)="showPeriods = true" />
      </div>
    </div>

    <!-- Competências -->
    @if (showPeriods) {
      <div class="mb-3" style="background:var(--sp-surface-card);border:1px solid var(--sp-border);border-radius:6px;padding:12px">
        <div class="flex align-items-center justify-content-between mb-2">
          <strong style="font-size:12px">Períodos de Competência</strong>
          <p-button label="Novo Período" icon="pi pi-plus" size="small" [text]="true" (onClick)="createPeriod()" />
        </div>
        <p-table [value]="periods()" styleClass="p-datatable-sm" [rowHover]="true">
          <ng-template pTemplate="body" let-p>
            <tr>
              <td>{{ p.yearMonth }}</td>
              <td><sp-status [status]="p.status" /></td>
              <td style="width:100px">
                @if (p.status === 'OPEN') { <p-button label="Fechar" size="small" severity="warn" [text]="true" (onClick)="closePeriod(p.id)" /> }
                @if (p.status === 'CLOSED') { <p-button label="Reabrir" size="small" [text]="true" (onClick)="reopenPeriod(p.id)" /> }
              </td>
            </tr>
          </ng-template>
        </p-table>
      </div>
    }

    <!-- Apontamentos -->
    <p-table [value]="entries()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15">
      <ng-template pTemplate="header">
        <tr>
          <th>Funcionário</th>
          <th style="width:80px">Data</th>
          <th style="width:60px" class="text-right">Horas</th>
          <th style="width:80px">Tipo</th>
          <th>Atividade</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-e>
        <tr>
          <td>{{ e.employeeName }}</td>
          <td style="font-size:0.8rem">{{ e.date }}</td>
          <td class="text-right font-mono">{{ e.hours | number:'1.1-1' }}</td>
          <td style="font-size:0.8rem">{{ e.hourType || 'Normal' }}</td>
          <td style="font-size:0.85rem;color:var(--sp-text-muted)">{{ e.activity }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum apontamento</td></tr></ng-template>
    </p-table>

    <!-- Novo Apontamento -->
    <p-dialog header="Apontar Horas" [(visible)]="showNew" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Funcionário</label><p-dropdown [(ngModel)]="form.employeeId" [options]="employees()" optionLabel="name" optionValue="id" placeholder="Selecionar..." styleClass="w-full" [filter]="true" /></div>
        <div class="grid">
          <div class="col-4"><label>Data</label><p-calendar [(ngModel)]="form.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-4"><label>Horas</label><p-inputNumber [(ngModel)]="form.hours" [maxFractionDigits]="1" styleClass="w-full" /></div>
          <div class="col-4"><label>Tipo</label><p-dropdown [(ngModel)]="form.hourType" [options]="hourTypes" styleClass="w-full" /></div>
        </div>
        <div><label>Atividade</label><input pInputText [(ngModel)]="form.activity" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
})
export class TimesheetListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  entries = signal<any[]>([]);
  periods = signal<any[]>([]);
  employees = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  showPeriods = false;
  form: any = { hours: 8 };
  hourTypes = ['Normal', 'Extra 50%', 'Extra 100%', 'Noturna'].map(t => ({ label: t, value: t }));

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/timesheets`).subscribe({ next: r => { this.entries.set(r.content || r); this.loading.set(false); }, error: () => this.loading.set(false) });
    this.http.get<any>(`/projects/${this.pid}/labor/competency-periods`).subscribe({ next: r => this.periods.set(r.content || r || []) });
    this.http.get<any>('/registry/employees?page=0&size=100').subscribe({ next: r => this.employees.set(r.content || r) });
  }

  create() {
    const body = { ...this.form, date: this.form.date?.toISOString?.()?.slice(0, 10) };
    this.http.post(`/projects/${this.pid}/timesheets`, body).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Horas apontadas' }); this.ngOnInit(); } });
  }

  createPeriod() {
    const ym = prompt('Mês (YYYY-MM-01):', new Date().toISOString().slice(0, 8) + '01');
    if (ym) this.http.post(`/projects/${this.pid}/labor/competency-periods`, { yearMonth: ym }).subscribe({ next: () => this.ngOnInit() });
  }

  closePeriod(id: string) { this.http.post(`/projects/${this.pid}/labor/competency-periods/${id}/close`, { closedBy: 'admin' }).subscribe({ next: () => { this.messages.add({ severity: 'warn', summary: 'Competência fechada' }); this.ngOnInit(); } }); }
  reopenPeriod(id: string) { this.http.post(`/projects/${this.pid}/labor/competency-periods/${id}/reopen`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Reaberta' }); this.ngOnInit(); } }); }
}
