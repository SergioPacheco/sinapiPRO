import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-safety-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, CalendarModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Segurança do Trabalho</h2>
      <div class="flex gap-2">
        <p-button label="Nova Inspeção" icon="pi pi-search" size="small" (onClick)="showInspection = true" />
        <p-button label="Registrar Incidente" icon="pi pi-exclamation-triangle" size="small" severity="warn" (onClick)="showIncident = true" />
        <p-button label="Relatório PDF" icon="pi pi-file-pdf" size="small" severity="secondary" (onClick)="pdf()" />
      </div>
    </div>

    <h3 style="font-size:13px;color:var(--sp-text-muted);margin:0 0 8px">Inspeções</h3>
    <p-table [value]="inspections()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header"><tr><th style="width:90px">Data</th><th>Tipo</th><th>Observações</th><th style="width:80px">Status</th></tr></ng-template>
      <ng-template pTemplate="body" let-i>
        <tr><td style="font-size:0.8rem">{{ i.date }}</td><td>{{ i.type }}</td><td style="font-size:0.85rem">{{ i.notes }}</td><td><sp-status [status]="i.status || 'COMPLETED'" /></td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center" style="padding:1rem;color:var(--sp-text-muted)">Nenhuma inspeção</td></tr></ng-template>
    </p-table>

    <h3 style="font-size:13px;color:var(--sp-text-muted);margin:16px 0 8px">Incidentes</h3>
    <p-table [value]="incidents()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header"><tr><th style="width:90px">Data</th><th>Descrição</th><th style="width:80px">Gravidade</th><th style="width:80px">Status</th><th style="width:60px"></th></tr></ng-template>
      <ng-template pTemplate="body" let-i>
        <tr>
          <td style="font-size:0.8rem">{{ i.date }}</td>
          <td>{{ i.description }}</td>
          <td><span [style.color]="i.severity === 'HIGH' ? '#ef4444' : i.severity === 'MEDIUM' ? '#f59e0b' : '#10b981'">{{ i.severity }}</span></td>
          <td><sp-status [status]="i.status" /></td>
          <td>@if (i.status !== 'RESOLVED') { <p-button icon="pi pi-check" [text]="true" size="small" severity="success" (onClick)="resolve(i.id)" pTooltip="Resolver" /> }</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:1rem;color:var(--sp-text-muted)">Nenhum incidente</td></tr></ng-template>
    </p-table>

    <!-- Nova Inspeção -->
    <p-dialog header="Nova Inspeção" [(visible)]="showInspection" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-6"><label>Data</label><p-calendar [(ngModel)]="inspForm.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-6"><label>Tipo</label><p-dropdown [(ngModel)]="inspForm.type" [options]="inspTypes" styleClass="w-full" /></div>
        </div>
        <div><label>Observações</label><textarea pInputText [(ngModel)]="inspForm.notes" class="w-full" rows="3"></textarea></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="createInspection()" /></ng-template>
    </p-dialog>

    <!-- Novo Incidente -->
    <p-dialog header="Registrar Incidente" [(visible)]="showIncident" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Descrição</label><input pInputText [(ngModel)]="incForm.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Data</label><p-calendar [(ngModel)]="incForm.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-6"><label>Gravidade</label><p-dropdown [(ngModel)]="incForm.severity" [options]="severities" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Registrar" icon="pi pi-check" severity="warn" (onClick)="createIncident()" /></ng-template>
    </p-dialog>
  `,
})
export class SafetyListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  inspections = signal<any[]>([]);
  incidents = signal<any[]>([]);
  showInspection = false;
  showIncident = false;
  inspForm: any = {};
  incForm: any = { severity: 'LOW' };
  inspTypes = ['Diária', 'Semanal', 'Mensal', 'Especial'].map(t => ({ label: t, value: t }));
  severities = ['LOW', 'MEDIUM', 'HIGH'].map(s => ({ label: s, value: s }));

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/safety/inspections`).subscribe({ next: r => this.inspections.set(r.content || r || []) });
    this.http.get<any>(`/projects/${this.pid}/safety/incidents`).subscribe({ next: r => this.incidents.set(r.content || r || []) });
  }

  createInspection() {
    const body = { ...this.inspForm, date: this.inspForm.date?.toISOString?.()?.slice(0, 10) };
    this.http.post(`/projects/${this.pid}/safety/inspections`, body).subscribe({ next: () => { this.showInspection = false; this.messages.add({ severity: 'success', summary: 'Inspeção registrada' }); this.ngOnInit(); } });
  }

  createIncident() {
    const body = { ...this.incForm, date: this.incForm.date?.toISOString?.()?.slice(0, 10) };
    this.http.post(`/projects/${this.pid}/safety/incidents`, body).subscribe({ next: () => { this.showIncident = false; this.messages.add({ severity: 'success', summary: 'Incidente registrado' }); this.ngOnInit(); } });
  }

  resolve(id: string) {
    this.http.post(`/projects/${this.pid}/safety/incidents/${id}/resolve`, { resolution: 'Resolvido' }).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Resolvido' }); this.ngOnInit(); } });
  }

  pdf() { window.open(`/api/v1/projects/${this.pid}/safety/reports/safety-report.pdf`, '_blank'); }
}
