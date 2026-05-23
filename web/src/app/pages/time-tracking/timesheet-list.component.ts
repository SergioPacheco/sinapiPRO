import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DatePickerModule } from 'primeng/datepicker';

@Component({
  selector: 'app-timesheet-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DatePickerModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Apontamento de Horas</h3>
      <p-button label="Novo Apontamento" icon="pi pi-plus" size="small" (onClick)="showCreate = true" />
    </div>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
      <ng-template pTemplate="header"><tr><th>Colaborador</th><th style="width:110px">Data</th><th style="width:80px">Horas</th><th>Atividade</th><th>Observações</th></tr></ng-template>
      <ng-template pTemplate="body" let-r>
        <tr><td>{{ r.employeeName }}</td><td>{{ r.date }}</td><td>{{ r.hoursWorked }}</td><td>{{ r.activity }}</td><td>{{ r.notes }}</td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhum apontamento</td></tr></ng-template>
    </p-table>

    <p-dialog header="Novo Apontamento" [(visible)]="showCreate" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Colaborador</label><input pInputText [(ngModel)]="form.employeeName" class="w-full" /></div>
        <div><label>Data</label><p-datePicker [(ngModel)]="form.date" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Horas</label><p-inputNumber [(ngModel)]="form.hoursWorked" [min]="0" [max]="24" styleClass="w-full" /></div>
        <div><label>Atividade</label><input pInputText [(ngModel)]="form.activity" class="w-full" /></div>
        <div><label>Observações</label><input pInputText [(ngModel)]="form.notes" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>
  `,
})
export class TimesheetListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);
  showCreate = false;
  form: any = { employeeName: '', date: null, hoursWorked: 8, activity: '', notes: '' };

  ngOnInit() { this.load(); }
  private get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  load() {
    this.http.get<any>(`/projects/${this.projectId}/timesheets`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() {
    this.http.post(`/projects/${this.projectId}/timesheets`, this.form).subscribe(() => {
      this.showCreate = false; this.form = { employeeName: '', date: null, hoursWorked: 8, activity: '', notes: '' }; this.load();
    });
  }
}
