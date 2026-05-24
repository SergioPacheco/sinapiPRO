import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
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
  selector: 'app-projects',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Obras</h2>
      <p-button label="Nova Obra" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="projects()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15" [globalFilterFields]="['name','code','clientName']">
      <ng-template pTemplate="caption">
        <input pInputText [(ngModel)]="filterText" placeholder="Buscar obra..." style="width:250px" />
      </ng-template>
      <ng-template pTemplate="header">
        <tr>
          <th style="width:90px">Código</th>
          <th>Nome</th>
          <th style="width:150px">Cliente</th>
          <th class="text-right" style="width:110px">Valor</th>
          <th style="width:80px">Início</th>
          <th style="width:80px">Status</th>
          <th style="width:50px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-p>
        <tr style="cursor:pointer" (dblclick)="open(p.id)">
          <td class="font-mono" style="font-size:0.8rem">{{ p.code }}</td>
          <td>{{ p.name }}</td>
          <td style="font-size:0.85rem;color:var(--sp-text-muted)">{{ p.clientName }}</td>
          <td class="text-right font-mono">{{ p.budgetValue | number:'1.0-0' }}</td>
          <td style="font-size:0.8rem">{{ p.startDate }}</td>
          <td><sp-status [status]="p.status" /></td>
          <td><p-button icon="pi pi-arrow-right" [text]="true" size="small" (onClick)="open(p.id)" /></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="7" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhuma obra cadastrada</td></tr></ng-template>
    </p-table>

    <!-- Nova Obra -->
    <p-dialog header="Nova Obra" [(visible)]="showNew" [style]="{width:'550px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-3"><label>Código</label><input pInputText [(ngModel)]="form.code" class="w-full" placeholder="OBR-001" /></div>
          <div class="col-9"><label>Nome da Obra</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-6"><label>Cliente</label><input pInputText [(ngModel)]="form.clientName" class="w-full" /></div>
          <div class="col-6"><label>Responsável</label><input pInputText [(ngModel)]="form.responsibleEngineer" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-4"><label>Área (m²)</label><p-inputNumber [(ngModel)]="form.totalArea" styleClass="w-full" /></div>
          <div class="col-4"><label>Início</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-4"><label>Término</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-8"><label>Endereço</label><input pInputText [(ngModel)]="form.address" class="w-full" /></div>
          <div class="col-4"><label>Cidade/UF</label><input pInputText [(ngModel)]="form.city" class="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ProjectsComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private messages = inject(MessageService);

  projects = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};
  filterText = '';

  ngOnInit() {
    this.http.get<any>('/projects?page=0&size=50').subscribe({ next: r => { this.projects.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  open(id: string) { this.router.navigate(['/projects', id, 'summary']); }

  create() {
    const body = { ...this.form, startDate: this.form.startDate?.toISOString?.()?.slice(0, 10), endDate: this.form.endDate?.toISOString?.()?.slice(0, 10) };
    this.http.post<any>('/projects', body).subscribe({
      next: (res) => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Obra criada' }); this.router.navigate(['/projects', res.id, 'summary']); },
    });
  }
}
