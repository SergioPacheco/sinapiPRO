import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { TagModule } from 'primeng/tag';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-daily-log-detail',
  standalone: true,
  imports: [DatePipe, TagModule, TableModule],
  template: `
    @if (log(); as l) {
      <div class="flex align-items-center gap-3 mb-3">
        <h3 style="margin:0">Diário — {{ l.logDate | date:'dd/MM/yyyy' }}</h3>
        <p-tag [value]="l.weather" />
      </div>
      <div class="grid mb-3">
        <div class="col-6 md:col-3"><strong>Efetivo:</strong> {{ l.workerCount }} pessoas</div>
        <div class="col-12"><strong>Observações:</strong> {{ l.notes || '—' }}</div>
      </div>
      @if (l.laborEntries?.length) {
        <h4>Mão de Obra</h4>
        <p-table [value]="l.laborEntries" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Função</th><th>Qtd</th><th>Horas</th></tr></ng-template>
          <ng-template pTemplate="body" let-e><tr><td>{{ e.role }}</td><td>{{ e.quantity }}</td><td>{{ e.hours }}</td></tr></ng-template>
        </p-table>
      }
      @if (l.equipmentEntries?.length) {
        <h4>Equipamentos</h4>
        <p-table [value]="l.equipmentEntries" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Equipamento</th><th>Horas</th></tr></ng-template>
          <ng-template pTemplate="body" let-e><tr><td>{{ e.description }}</td><td>{{ e.hours }}</td></tr></ng-template>
        </p-table>
      }
      @if (l.occurrences?.length) {
        <h4>Ocorrências</h4>
        <p-table [value]="l.occurrences" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Tipo</th><th>Descrição</th></tr></ng-template>
          <ng-template pTemplate="body" let-o><tr><td><p-tag [value]="o.type" /></td><td>{{ o.description }}</td></tr></ng-template>
        </p-table>
      }
      @if (l.materials?.length) {
        <h4>Materiais</h4>
        <p-table [value]="l.materials" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Material</th><th>Tipo</th><th>Qtd</th></tr></ng-template>
          <ng-template pTemplate="body" let-m><tr><td>{{ m.description }}</td><td><p-tag [value]="m.direction" [severity]="m.direction === 'IN' ? 'success' : 'warn'" /></td><td>{{ m.quantity }}</td></tr></ng-template>
        </p-table>
      }
      @if (l.tasks?.length) {
        <h4>Atividades</h4>
        <p-table [value]="l.tasks" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Descrição</th><th>Status</th></tr></ng-template>
          <ng-template pTemplate="body" let-t><tr><td>{{ t.description }}</td><td><p-tag [value]="t.status" /></td></tr></ng-template>
        </p-table>
      }
    }
  `,
})
export class DailyLogDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  log = signal<any>(null);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    const logId = this.route.snapshot.paramMap.get('logId');
    this.http.get(`/projects/${id}/daily-logs/${logId}`).subscribe(res => this.log.set(res));
  }
}
