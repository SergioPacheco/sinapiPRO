import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { TagModule } from 'primeng/tag';
import { MessageService } from 'primeng/api';
import { StatusTagComponent, CurrencyDisplayComponent } from '../../shared/components';

@Component({
  selector: 'app-timesheet-list',
  standalone: true,
  imports: [FormsModule, TableModule, TabViewModule, ButtonModule, DialogModule, DropdownModule, InputNumberModule, CalendarModule, TagModule, StatusTagComponent, CurrencyDisplayComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Mão de Obra</h3>
      <div class="flex gap-2 align-items-center">
        <p-dropdown [options]="periods()" [(ngModel)]="selectedPeriod" optionLabel="label" placeholder="Competência" styleClass="w-10rem" (onChange)="loadPeriodData()" />
        <p-tag [value]="selectedPeriod?.status || ''" [severity]="selectedPeriod?.status === 'OPEN' ? 'success' : 'secondary'" />
        <p-button label="Lançar Horas" icon="pi pi-plus" size="small" (onClick)="showLancar = true" [disabled]="selectedPeriod?.status !== 'OPEN'" />
      </div>
    </div>

    <p-tabView>
      <p-tabPanel header="Apontamento">
        <p-table [value]="timesheets()" styleClass="p-datatable-sm" [paginator]="true" [rows]="15">
          <ng-template pTemplate="header"><tr><th>Funcionário</th><th style="width:100px">Data</th><th style="width:80px">Tipo</th><th style="width:80px" class="text-right">Horas</th><th style="width:120px">Etapa</th></tr></ng-template>
          <ng-template pTemplate="body" let-t>
            <tr><td>{{ t.employeeName }}</td><td>{{ t.date }}</td><td><p-tag [value]="t.hourType" [severity]="t.hourType === 'NORMAL' ? 'info' : 'warn'" /></td><td class="text-right font-semibold">{{ t.hours }}h</td><td>{{ t.activity }}</td></tr>
          </ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Banco de Horas">
        <p-table [value]="hourBank()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Funcionário</th><th style="width:100px">Data</th><th style="width:80px">Tipo</th><th style="width:80px" class="text-right">Horas</th><th>Descrição</th></tr></ng-template>
          <ng-template pTemplate="body" let-h>
            <tr><td>{{ h.employeeName }}</td><td>{{ h.referenceDate }}</td><td><span [class]="h.type === 'CREDIT' ? 'text-green-500' : 'text-red-500'">{{ h.type }}</span></td><td class="text-right font-semibold">{{ h.hours }}h</td><td>{{ h.description }}</td></tr>
          </ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Resumo">
        <div class="grid">
          @for (s of summary(); track s.employee) {
            <div class="col-12 md:col-6 lg:col-4">
              <div class="summary-card">
                <div class="font-semibold">{{ s.employee }}</div>
                <div class="grid mt-2" style="font-size:13px">
                  <div class="col-6">Normal: <strong>{{ s.normal }}h</strong></div>
                  <div class="col-6">Extra 50%: <strong>{{ s.he50 }}h</strong></div>
                  <div class="col-6">Extra 100%: <strong>{{ s.he100 }}h</strong></div>
                  <div class="col-6">Noturna: <strong>{{ s.noturna }}h</strong></div>
                  <div class="col-12 mt-1 font-semibold">Total: {{ s.total }}h</div>
                </div>
              </div>
            </div>
          }
        </div>
      </p-tabPanel>
    </p-tabView>

    <!-- Dialog: Fechar/Abrir Competência -->
    <div class="flex gap-2 mt-3">
      @if (selectedPeriod?.status === 'OPEN') { <p-button label="Fechar Competência" icon="pi pi-lock" severity="warn" size="small" (onClick)="closePeriod()" /> }
      @if (selectedPeriod?.status === 'CLOSED') { <p-button label="Reabrir" icon="pi pi-lock-open" severity="secondary" size="small" (onClick)="reopenPeriod()" /> }
    </div>

    <!-- Dialog: Lançar Horas -->
    <p-dialog header="Lançar Horas" [(visible)]="showLancar" [style]="{width:'400px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Funcionário</label><p-dropdown [options]="employees()" [(ngModel)]="newEntry.employeeId" optionLabel="name" optionValue="id" styleClass="w-full" /></div>
        <div><label>Data</label><p-calendar [(ngModel)]="newEntry.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        <div><label>Tipo Hora</label><p-dropdown [options]="hourTypes" [(ngModel)]="newEntry.hourType" styleClass="w-full" /></div>
        <div><label>Horas</label><p-inputNumber [(ngModel)]="newEntry.hours" [min]="0.5" [max]="24" [step]="0.5" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveEntry()" /></ng-template>
    </p-dialog>
  `,
  styles: [`.summary-card { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: var(--sp-radius); padding: 1rem; }`],
})
export class TimesheetListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  periods = signal<any[]>([]);
  timesheets = signal<any[]>([]);
  hourBank = signal<any[]>([]);
  summary = signal<any[]>([]);
  employees = signal<any[]>([]);
  selectedPeriod: any = null;
  showLancar = false;
  newEntry: any = { employeeId: null, date: new Date(), hourType: 'NORMAL', hours: 8 };
  hourTypes = ['NORMAL', 'HE50', 'HE100', 'NOTURNA', 'FERIADO'].map(v => ({ label: v, value: v }));

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/labor/competency-periods`).subscribe(res => {
      const list = (res.content || res).map((p: any) => ({ ...p, label: p.yearMonth }));
      this.periods.set(list);
      if (list.length) { this.selectedPeriod = list[0]; this.loadPeriodData(); }
    });
    this.http.get<any>('/registry/employees?page=0&size=100').subscribe(res => this.employees.set(res.content || res));
  }

  loadPeriodData() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/timesheets`).subscribe(res => this.timesheets.set(res.content || res));
    this.http.get<any>(`/projects/${id}/labor/hour-bank/${this.selectedPeriod?.id || 'none'}`).subscribe({ next: res => this.hourBank.set(res), error: () => this.hourBank.set([]) });
  }

  saveEntry() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/timesheets`, this.newEntry).subscribe({
      next: () => { this.showLancar = false; this.messages.add({ severity: 'success', summary: 'Horas lançadas' }); this.loadPeriodData(); },
    });
  }

  closePeriod() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/labor/competency-periods/${this.selectedPeriod.id}/close`, { closedBy: 'admin' }).subscribe({
      next: () => { this.selectedPeriod.status = 'CLOSED'; this.messages.add({ severity: 'info', summary: 'Competência fechada' }); },
    });
  }

  reopenPeriod() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/labor/competency-periods/${this.selectedPeriod.id}/reopen`, {}).subscribe({
      next: () => { this.selectedPeriod.status = 'OPEN'; this.messages.add({ severity: 'info', summary: 'Competência reaberta' }); },
    });
  }
}
