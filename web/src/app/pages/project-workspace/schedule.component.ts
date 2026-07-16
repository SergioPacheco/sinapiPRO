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
import { MessageService } from 'primeng/api';
import { SCurveComponent, SCurveData } from './s-curve.component';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, CalendarModule, SCurveComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Cronograma</h2>
      <div class="flex gap-2">
        <p-button label="Nova Atividade" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
        <p-button label="Distribuir Datas" icon="pi pi-calendar" size="small" severity="secondary" (onClick)="distribute()" />
        <p-button label="Curva S" icon="pi pi-chart-line" size="small" severity="secondary" (onClick)="showCurveS()" />
      </div>
    </div>

    <p-table [value]="activities()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th>Atividade</th>
          <th style="width:80px">Duração</th>
          <th style="width:90px">Início</th>
          <th style="width:90px">Fim</th>
          <th style="width:80px">Progresso</th>
          <th style="width:60px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-a>
        <tr>
          <td>{{ a.name }}</td>
          <td class="text-center">{{ a.durationDays }}d</td>
          <td style="font-size:0.8rem">{{ a.startDate }}</td>
          <td style="font-size:0.8rem">{{ a.endDate }}</td>
          <td>
            <div style="background:var(--sp-border);height:14px;border-radius:3px;overflow:hidden">
              <div [style.width.%]="a.progress || 0" style="height:100%;background:var(--sp-primary);border-radius:3px"></div>
            </div>
          </td>
          <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="editProgress(a)" /></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhuma atividade</td></tr></ng-template>
    </p-table>

    <!-- Nova Atividade -->
    <p-dialog header="Nova Atividade" [(visible)]="showNew" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Nome</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
        <div class="grid">
          <div class="col-4"><label>Duração (dias)</label><p-inputNumber [(ngModel)]="form.durationDays" styleClass="w-full" /></div>
          <div class="col-4"><label>Início</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-4"><label>Fim</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
    <!-- Curva S Dialog -->
    <p-dialog header="Curva S — Planejado vs Realizado" [(visible)]="showSCurve" [style]="{width:'700px'}" [modal]="true">
      @if (sCurveData()) {
        <app-s-curve [data]="sCurveData()!" />
      } @else {
        <div style="text-align:center;padding:2rem;color:var(--sp-text-muted)">Carregando...</div>
      }
    </p-dialog>
  `,
})
export class ScheduleComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  activities = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  showSCurve = false;
  sCurveData = signal<SCurveData | null>(null);
  form: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/schedule`).subscribe({ next: r => { this.activities.set(r.content || r); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  create() {
    const body = { ...this.form, startDate: this.form.startDate?.toISOString?.()?.slice(0, 10), endDate: this.form.endDate?.toISOString?.()?.slice(0, 10) };
    this.http.post(`/projects/${this.pid}/schedule`, body).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Atividade criada' }); this.ngOnInit(); } });
  }

  editProgress(a: any) {
    const progress = prompt('Progresso (0-100):', String(a.progress || 0));
    if (progress !== null) {
      this.http.patch(`/projects/${this.pid}/schedule/${a.id}/progress`, { progress: Number(progress) }).subscribe({ next: () => this.ngOnInit() });
    }
  }

  distribute() {
    this.http.post(`/projects/${this.pid}/schedule/distribute-dates`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Datas distribuídas' }); this.ngOnInit(); } });
  }

  showCurveS() {
    this.showSCurve = true;
    this.sCurveData.set(null);
    this.http.get<any>(`/projects/${this.pid}/schedule/s-curve`).subscribe({
      next: r => {
        // API retorna { periods: string[], planned: number[], actual: number[] }
        this.sCurveData.set({
          periods: r.periods || r.labels || [],
          planned: r.planned || r.baseline || [],
          actual: r.actual || r.progress || [],
        });
      },
      error: () => this.messages.add({ severity: 'error', summary: 'Erro ao carregar Curva S' }),
    });
  }
}
