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

@Component({
  selector: 'app-daily-log-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, CalendarModule, DropdownModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Diário de Obra</h2>
      <p-button label="Novo Registro" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="logs()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15">
      <ng-template pTemplate="header">
        <tr>
          <th style="width:90px">Data</th>
          <th style="width:80px">Clima</th>
          <th>Atividades</th>
          <th style="width:80px">Equipe</th>
          <th style="width:80px">Fotos</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-l>
        <tr>
          <td class="font-mono" style="font-size:0.8rem">{{ l.date }}</td>
          <td>{{ l.weather }}</td>
          <td style="font-size:0.85rem">{{ l.activities }}</td>
          <td class="text-center">{{ l.crewCount }}</td>
          <td class="text-center">{{ l.photoCount || 0 }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum registro</td></tr></ng-template>
    </p-table>

    <!-- Novo Registro -->
    <p-dialog header="Novo Diário" [(visible)]="showNew" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-6"><label>Data</label><p-calendar [(ngModel)]="form.date" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-6"><label>Clima</label><p-dropdown [(ngModel)]="form.weather" [options]="climaOpts" styleClass="w-full" /></div>
        </div>
        <div><label>Atividades Executadas</label><textarea pInputText [(ngModel)]="form.activities" class="w-full" rows="3"></textarea></div>
        <div class="grid">
          <div class="col-6"><label>Equipe (pessoas)</label><input pInputText type="number" [(ngModel)]="form.crewCount" class="w-full" /></div>
          <div class="col-6"><label>Ocorrências</label><input pInputText [(ngModel)]="form.incidents" class="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="create()" />
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
  showNew = false;
  form: any = { crewCount: 0 };
  climaOpts = ['Ensolarado', 'Nublado', 'Chuvoso', 'Parcialmente Nublado'].map(c => ({ label: c, value: c }));

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/daily-logs`).subscribe({ next: r => { this.logs.set(r.content || r); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  create() {
    const body = { ...this.form, date: this.form.date?.toISOString?.()?.slice(0, 10) || new Date().toISOString().slice(0, 10) };
    this.http.post(`/projects/${this.pid}/daily-logs`, body).subscribe({
      next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Diário registrado' }); this.ngOnInit(); },
    });
  }
}
