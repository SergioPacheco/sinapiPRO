import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { CalendarModule } from 'primeng/calendar';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-measurement-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, RouterLink, TableModule, ButtonModule, DialogModule, InputNumberModule, CalendarModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Medições</h2>
      <p-button label="Nova Medição" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="measurements()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th style="width:50px">#</th>
          <th style="width:100px">Início</th>
          <th style="width:100px">Fim</th>
          <th class="text-right" style="width:120px">Valor Medido</th>
          <th class="text-right" style="width:100px">Retenção</th>
          <th style="width:100px">Status</th>
          <th style="width:140px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-m>
        <tr>
          <td class="font-mono">{{ m.number }}</td>
          <td style="font-size:0.8rem">{{ m.periodStart }}</td>
          <td style="font-size:0.8rem">{{ m.periodEnd }}</td>
          <td class="text-right font-mono">{{ m.measuredValue | number:'1.2-2' }}</td>
          <td class="text-right font-mono">{{ m.retentionValue | number:'1.2-2' }}</td>
          <td><sp-status [status]="m.status" /></td>
          <td class="flex gap-1">
            <a [routerLink]="[m.id]"><p-button icon="pi pi-eye" [text]="true" size="small" pTooltip="Detalhe" /></a>
            @if (m.status === 'DRAFT') { <p-button icon="pi pi-send" [text]="true" size="small" severity="info" (onClick)="submit(m.id)" pTooltip="Submeter" /> }
            @if (m.status === 'SUBMITTED') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" (onClick)="approve(m.id)" pTooltip="Aprovar" /> }
            @if (m.status === 'SUBMITTED') { <p-button icon="pi pi-times" [text]="true" size="small" severity="danger" (onClick)="reject(m.id)" pTooltip="Rejeitar" /> }
            <p-button icon="pi pi-file-pdf" [text]="true" size="small" (onClick)="pdf(m.id)" pTooltip="Boletim PDF" />
          </td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="7" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhuma medição</td></tr></ng-template>
    </p-table>

    <!-- Nova Medição -->
    <p-dialog header="Nova Medição" [(visible)]="showNew" [style]="{width:'400px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-6"><label>Período Início</label><p-calendar [(ngModel)]="form.periodStart" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-6"><label>Período Fim</label><p-calendar [(ngModel)]="form.periodEnd" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
        <div><label>Retenção (%)</label><p-inputNumber [(ngModel)]="form.retentionPct" [maxFractionDigits]="2" suffix="%" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
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
  showNew = false;
  form: any = { retentionPct: 5 };

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/measurements`).subscribe({ next: r => { this.measurements.set(r.content || r); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  create() {
    const body = { periodStart: this.form.periodStart?.toISOString?.()?.slice(0, 10), periodEnd: this.form.periodEnd?.toISOString?.()?.slice(0, 10), retentionPct: (this.form.retentionPct || 5) / 100 };
    this.http.post(`/projects/${this.pid}/measurements`, body).subscribe({
      next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Medição criada' }); this.ngOnInit(); },
    });
  }

  submit(id: string) { this.http.post(`/projects/${this.pid}/measurements/${id}/submit`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Submetida' }); this.ngOnInit(); } }); }
  approve(id: string) { this.http.post(`/projects/${this.pid}/measurements/${id}/approve`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Aprovada' }); this.ngOnInit(); } }); }
  reject(id: string) { this.http.post(`/projects/${this.pid}/measurements/${id}/reject`, { reason: 'Revisão necessária' }).subscribe({ next: () => { this.messages.add({ severity: 'warn', summary: 'Rejeitada' }); this.ngOnInit(); } }); }
  pdf(id: string) { window.open(`/api/v1/projects/${this.pid}/measurements/${id}/reports/bulletin.pdf`, '_blank'); }
}
