import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { ProjectService, Project, ProjectDashboard } from '../services/project.service';

interface ActivityEvent {
  id: string;
  description: string;
  type: string;
  color: string;
  createdAt: string;
}

@Component({
  selector: 'app-project-summary',
  template: `
    @if (project() && dashboard()) {
      <!-- Phase Stepper -->
      <div class="phase-stepper">
        @for (phase of phases; track phase.id) {
          <div class="phase" [class.active]="phase.id === currentPhase()" [class.done]="isPhaseComplete(phase.id)">
            <div class="phase-indicator">
              @if (isPhaseComplete(phase.id)) { <mat-icon>check_circle</mat-icon> }
              @else { <mat-icon>{{ phase.icon }}</mat-icon> }
            </div>
            <span class="phase-label">{{ phase.label }}</span>
          </div>
          @if (!$last) { <div class="phase-connector" [class.done]="isPhaseComplete(phase.id)"></div> }
        }
      </div>

      <!-- Planning Checklist -->
      <mat-card class="section-card">
        <mat-card-header><mat-card-title><mat-icon>checklist</mat-icon> Planejamento</mat-card-title></mat-card-header>
        <mat-card-content>
          <div class="checklist">
            <div class="check-item" [class.done]="dashboard()!.planning.hasBudget">
              <mat-icon>{{ dashboard()!.planning.hasBudget ? 'check_circle' : 'radio_button_unchecked' }}</mat-icon>
              <span>Orçamento</span>
              @if (!dashboard()!.planning.hasBudget) { <a mat-button color="primary" routerLink="../budgets">Criar</a> }
            </div>
            <div class="check-item" [class.done]="dashboard()!.planning.hasSchedule">
              <mat-icon>{{ dashboard()!.planning.hasSchedule ? 'check_circle' : 'radio_button_unchecked' }}</mat-icon>
              <span>Cronograma</span>
              @if (!dashboard()!.planning.hasSchedule) { <a mat-button color="primary" routerLink="../schedule">Criar</a> }
            </div>
            <div class="check-item" [class.done]="dashboard()!.planning.hasContract">
              <mat-icon>{{ dashboard()!.planning.hasContract ? 'check_circle' : 'radio_button_unchecked' }}</mat-icon>
              <span>Contrato</span>
              @if (!dashboard()!.planning.hasContract) { <a mat-button color="primary" routerLink="../contracts">Criar</a> }
            </div>
            <div class="check-item" [class.done]="dashboard()!.planning.hasTeam">
              <mat-icon>{{ dashboard()!.planning.hasTeam ? 'check_circle' : 'radio_button_unchecked' }}</mat-icon>
              <span>Equipe definida</span>
            </div>
          </div>
          <mat-progress-bar mode="determinate" [value]="planningProgress()" />
          <p class="progress-label">{{ planningProgress() }}% do planejamento concluído</p>
        </mat-card-content>
      </mat-card>

      <!-- Execution KPIs -->
      @if (project()!.status === 'IN_PROGRESS') {
        <div class="kpi-row">
          <mat-card class="kpi-card">
            <span class="kpi-value">{{ dashboard()!.execution.dailyLogs }}</span>
            <span class="kpi-label">Diários</span>
          </mat-card>
          <mat-card class="kpi-card">
            <span class="kpi-value">{{ dashboard()!.execution.measurements }}</span>
            <span class="kpi-label">Medições</span>
          </mat-card>
          <mat-card class="kpi-card">
            <span class="kpi-value">{{ dashboard()!.execution.purchaseOrders }}</span>
            <span class="kpi-label">Pedidos</span>
          </mat-card>
          <mat-card class="kpi-card warn" [class.warn]="dashboard()!.execution.pendingMeasurements > 0">
            <span class="kpi-value">{{ dashboard()!.execution.pendingMeasurements }}</span>
            <span class="kpi-label">Medições pendentes</span>
          </mat-card>
        </div>
      }

      <!-- Next Actions -->
      @if (dashboard()!.nextActions.length > 0) {
        <mat-card class="section-card next-actions">
          <mat-card-header><mat-card-title><mat-icon>bolt</mat-icon> Próximas Ações</mat-card-title></mat-card-header>
          <mat-card-content>
            @for (action of dashboard()!.nextActions; track action.id) {
              <a class="action-item" [routerLink]="action.route">
                <mat-icon color="primary">{{ action.icon }}</mat-icon>
                <span>{{ action.label }}</span>
                <mat-icon class="arrow">chevron_right</mat-icon>
              </a>
            }
          </mat-card-content>
        </mat-card>
      }

      <!-- Timeline -->
      @if (timeline().length > 0) {
        <mat-card class="section-card">
          <mat-card-header><mat-card-title><mat-icon>history</mat-icon> Atividades Recentes</mat-card-title></mat-card-header>
          <mat-card-content>
            <div class="timeline">
              @for (event of timeline().slice(0, 10); track event.id) {
                <div class="timeline-item">
                  <div class="timeline-dot" [style.background]="event.color || '#666'"></div>
                  <div class="timeline-content">
                    <span class="timeline-text">{{ event.description }}</span>
                    <span class="timeline-time">{{ event.createdAt | date:'dd/MM HH:mm' }}</span>
                  </div>
                </div>
              }
            </div>
          </mat-card-content>
        </mat-card>
      }

      <!-- Quick Info -->
      <div class="info-row">
        <mat-card class="info-card">
          <mat-icon>person</mat-icon>
          <div><strong>Cliente</strong><p>{{ project()!.customerName }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon>location_on</mat-icon>
          <div><strong>Local</strong><p>{{ project()!.city || '—' }}/{{ project()!.state || '—' }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon>calendar_today</mat-icon>
          <div><strong>Prazo</strong><p>{{ project()!.startDate || '—' }} → {{ project()!.expectedEndDate || '—' }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon>payments</mat-icon>
          <div><strong>Valor</strong><p>{{ project()!.totalBudget ? formatCurrency(project()!.totalBudget!) : '—' }}</p></div>
        </mat-card>
      </div>

      <div class="actions-row">
        <button mat-flat-button color="primary" (click)="edit()"><mat-icon>edit</mat-icon> Editar Obra</button>
        @if (project()!.status === 'PLANNING' && planningProgress() === 100) {
          <button mat-flat-button color="accent" (click)="startExecution()"><mat-icon>play_arrow</mat-icon> Iniciar Execução</button>
        }
      </div>
    }
  `,
  styles: `
    /* Phase Stepper */
    .phase-stepper { display: flex; align-items: center; justify-content: center; gap: 0; padding: 24px 16px; margin-bottom: 16px; }
    .phase { display: flex; flex-direction: column; align-items: center; gap: 4px; opacity: 0.5; }
    .phase.active, .phase.done { opacity: 1; }
    .phase-indicator { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: var(--mat-sys-surface-container); }
    .phase.active .phase-indicator { background: var(--mat-sys-primary); color: var(--mat-sys-on-primary); }
    .phase.done .phase-indicator { background: #4caf50; color: white; }
    .phase-label { font-size: 11px; font-weight: 500; text-transform: uppercase; }
    .phase-connector { flex: 1; height: 2px; background: var(--mat-sys-outline-variant); margin: 0 8px; margin-bottom: 20px; }
    .phase-connector.done { background: #4caf50; }

    /* Checklist */
    .section-card { margin-bottom: 16px; }
    .section-card mat-card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; }
    .checklist { display: flex; flex-direction: column; gap: 8px; margin-bottom: 12px; }
    .check-item { display: flex; align-items: center; gap: 12px; padding: 8px 12px; border-radius: 8px; }
    .check-item.done { background: rgba(76, 175, 80, 0.08); }
    .check-item.done mat-icon { color: #4caf50; }
    .check-item:not(.done) mat-icon { color: var(--mat-sys-outline); }
    .check-item span { flex: 1; }
    .progress-label { font-size: 12px; color: var(--mat-sys-on-surface-variant); margin-top: 4px; }

    /* KPIs */
    .kpi-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; margin-bottom: 16px; }
    .kpi-card { text-align: center; padding: 16px; }
    .kpi-value { display: block; font-size: 28px; font-weight: 700; color: var(--mat-sys-primary); }
    .kpi-card.warn .kpi-value { color: #ff9800; }
    .kpi-label { font-size: 12px; color: var(--mat-sys-on-surface-variant); }

    /* Next Actions */
    .next-actions mat-card-content { display: flex; flex-direction: column; }
    .action-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 8px; text-decoration: none; color: var(--mat-sys-on-surface); cursor: pointer; transition: background 0.15s; }
    .action-item:hover { background: var(--mat-sys-surface-container-highest); }
    .action-item span { flex: 1; font-weight: 500; }
    .action-item .arrow { color: var(--mat-sys-outline); }

    /* Info */
    .info-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px; margin-bottom: 16px; }
    .info-card { display: flex; align-items: center; gap: 12px; padding: 16px; }
    .info-card mat-icon { color: var(--mat-sys-primary); }
    .info-card p { margin: 2px 0 0; font-size: 13px; color: var(--mat-sys-on-surface-variant); }
    .actions-row { display: flex; gap: 12px; }

    /* Timeline */
    .timeline { display: flex; flex-direction: column; gap: 0; }
    .timeline-item { display: flex; align-items: flex-start; gap: 12px; padding: 8px 0; border-left: 2px solid var(--mat-sys-outline-variant); margin-left: 6px; padding-left: 16px; position: relative; }
    .timeline-dot { width: 12px; height: 12px; border-radius: 50%; position: absolute; left: -7px; top: 12px; }
    .timeline-content { display: flex; justify-content: space-between; flex: 1; align-items: center; }
    .timeline-text { font-size: 13px; }
    .timeline-time { font-size: 11px; color: var(--mat-sys-outline); white-space: nowrap; }
  `,
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatProgressBarModule, MatDividerModule, MatChipsModule, RouterLink, DatePipe],
})
export class ProjectSummaryComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly service = inject(ProjectService);
  private readonly http = inject(HttpClient);

  project = signal<Project | null>(null);
  dashboard = signal<ProjectDashboard | null>(null);
  timeline = signal<ActivityEvent[]>([]);

  phases = [
    { id: 'PLANNING', label: 'Planejamento', icon: 'architecture' },
    { id: 'IN_PROGRESS', label: 'Execução', icon: 'engineering' },
    { id: 'CLOSING', label: 'Encerramento', icon: 'fact_check' },
    { id: 'COMPLETED', label: 'Concluída', icon: 'verified' },
  ];

  ngOnInit() {
    const id = this.route.parent!.snapshot.paramMap.get('projectId')!;
    this.service.getById(id).subscribe(p => this.project.set(p));
    this.service.getDashboard(id).subscribe(d => this.dashboard.set(d));
    this.http.get<ActivityEvent[]>(`/projects/${id}/timeline`).subscribe({
      next: events => this.timeline.set(events),
      error: () => {},
    });
  }

  currentPhase(): string {
    return this.project()?.status || 'PLANNING';
  }

  isPhaseComplete(phaseId: string): boolean {
    const order = ['PLANNING', 'IN_PROGRESS', 'CLOSING', 'COMPLETED'];
    const current = order.indexOf(this.currentPhase());
    const target = order.indexOf(phaseId);
    return target < current;
  }

  planningProgress(): number {
    const d = this.dashboard();
    if (!d) return 0;
    const items = [d.planning.hasBudget, d.planning.hasContract, d.planning.hasSchedule, d.planning.hasTeam];
    return Math.round((items.filter(Boolean).length / items.length) * 100);
  }

  edit() { this.router.navigate(['/projects', this.project()!.id, 'edit']); }

  startExecution() {
    this.service.updateStatus(this.project()!.id, 'IN_PROGRESS').subscribe(p => {
      this.project.set(p);
    });
  }

  formatCurrency(value: number): string {
    return 'R$ ' + value.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }
}
