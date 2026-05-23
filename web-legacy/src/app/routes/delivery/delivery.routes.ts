import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatStepperModule } from '@angular/material/stepper';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

interface Delivery {
  id: string; status: string; provisionalDate: string; definitiveDate: string;
  deliveredBy: string; receivedBy: string; notes: string;
  checklist: { id: string; description: string; checked: boolean; phase: string }[];
}

@Component({
  selector: 'app-delivery',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatCheckboxModule, MatStepperModule, PageHeader],
  template: `
    <page-header title="Encerramento da Obra" subtitle="Wizard de entrega — siga as etapas para concluir">
      @if (!delivery()) {
        <button mat-flat-button color="primary" (click)="initDelivery()"><mat-icon>play_arrow</mat-icon> Iniciar Encerramento</button>
      }
    </page-header>

    @if (delivery(); as d) {
      <mat-stepper [linear]="false" [selectedIndex]="currentStep()">
        <!-- Step 1: Punch List -->
        <mat-step [completed]="isPhaseComplete('PUNCH_LIST')" label="Punch List">
          <div class="step-content">
            <p class="step-desc">Resolva todas as pendências antes de prosseguir.</p>
            @for (item of getByPhase('PUNCH_LIST'); track item.id) {
              <mat-checkbox [checked]="item.checked" (change)="checkItem(item.id)" [disabled]="item.checked">
                {{ item.description }}
              </mat-checkbox>
            }
            <div class="step-actions">
              <button mat-flat-button color="primary" matStepperNext [disabled]="!isPhaseComplete('PUNCH_LIST')">
                Próximo <mat-icon>arrow_forward</mat-icon>
              </button>
            </div>
          </div>
        </mat-step>

        <!-- Step 2: Documentação -->
        <mat-step [completed]="isPhaseComplete('DOCS')" label="Documentação">
          <div class="step-content">
            <p class="step-desc">Entregue toda a documentação técnica e legal.</p>
            @for (item of getByPhase('DOCS'); track item.id) {
              <mat-checkbox [checked]="item.checked" (change)="checkItem(item.id)" [disabled]="item.checked">
                {{ item.description }}
              </mat-checkbox>
            }
            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
              <button mat-flat-button color="primary" matStepperNext [disabled]="!isPhaseComplete('DOCS')">
                Próximo <mat-icon>arrow_forward</mat-icon>
              </button>
            </div>
          </div>
        </mat-step>

        <!-- Step 3: Vistoria -->
        <mat-step [completed]="isPhaseComplete('INSPECTION')" label="Vistoria">
          <div class="step-content">
            <p class="step-desc">Realize a vistoria final com o cliente.</p>
            @for (item of getByPhase('INSPECTION'); track item.id) {
              <mat-checkbox [checked]="item.checked" (change)="checkItem(item.id)" [disabled]="item.checked">
                {{ item.description }}
              </mat-checkbox>
            }
            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
              <button mat-flat-button color="primary" matStepperNext [disabled]="!isPhaseComplete('INSPECTION')">
                Próximo <mat-icon>arrow_forward</mat-icon>
              </button>
            </div>
          </div>
        </mat-step>

        <!-- Step 4: Entrega -->
        <mat-step [completed]="d.status === 'DEFINITIVE'" label="Entrega">
          <div class="step-content">
            <p class="step-desc">Formalize a entrega da obra ao cliente.</p>

            @if (d.status === 'PENDING') {
              <mat-card class="action-card">
                <mat-card-content>
                  <p>Todas as etapas anteriores estão concluídas. Emita o recebimento provisório.</p>
                  <button mat-flat-button color="primary" (click)="provisional()" [disabled]="!allPhasesComplete()">
                    <mat-icon>assignment_turned_in</mat-icon> Recebimento Provisório
                  </button>
                </mat-card-content>
              </mat-card>
            }

            @if (d.status === 'PROVISIONAL') {
              <mat-card class="action-card success">
                <mat-card-content>
                  <p>✅ Recebimento provisório em {{ d.provisionalDate }}. Após o prazo de garantia, emita o definitivo.</p>
                  <button mat-flat-button color="primary" (click)="definitive()">
                    <mat-icon>verified</mat-icon> Recebimento Definitivo
                  </button>
                </mat-card-content>
              </mat-card>
            }

            @if (d.status === 'DEFINITIVE') {
              <mat-card class="action-card done">
                <mat-card-content>
                  <p>🎉 Obra entregue definitivamente em {{ d.definitiveDate }}.</p>
                </mat-card-content>
              </mat-card>
            }

            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
            </div>
          </div>
        </mat-step>
      </mat-stepper>
    } @else if (!loading()) {
      <mat-card class="empty-card">
        <mat-card-content>
          <mat-icon class="empty-icon">fact_check</mat-icon>
          <h3>Pronto para encerrar?</h3>
          <p>Inicie o processo de encerramento quando a obra estiver pronta para entrega.</p>
          <button mat-flat-button color="primary" (click)="initDelivery()"><mat-icon>play_arrow</mat-icon> Iniciar Encerramento</button>
        </mat-card-content>
      </mat-card>
    }
  `,
  styles: `
    .step-content { padding: 16px 0; max-width: 600px; display: flex; flex-direction: column; gap: 8px; }
    .step-desc { color: var(--mat-sys-on-surface-variant); margin-bottom: 12px; }
    .step-actions { display: flex; gap: 12px; margin-top: 24px; }
    .action-card { margin-top: 16px; }
    .action-card.success { border-left: 4px solid #4caf50; }
    .action-card.done { border-left: 4px solid #2196f3; background: rgba(33,150,243,.04); }
    .empty-card { text-align: center; padding: 48px; }
    .empty-icon { font-size: 64px; width: 64px; height: 64px; color: var(--mat-sys-outline); margin-bottom: 16px; }
    .empty-card h3 { margin: 0 0 8px; }
    .empty-card p { color: var(--mat-sys-on-surface-variant); margin-bottom: 16px; }
  `,
})
export class DeliveryComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  delivery = signal<Delivery | null>(null);
  loading = signal(true);

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.load();
  }

  load() {
    this.http.get<Delivery>(`/projects/${this.projectId}/delivery`).subscribe({
      next: d => { this.delivery.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  currentStep(): number {
    const d = this.delivery();
    if (!d) return 0;
    if (!this.isPhaseComplete('PUNCH_LIST')) return 0;
    if (!this.isPhaseComplete('DOCS')) return 1;
    if (!this.isPhaseComplete('INSPECTION')) return 2;
    return 3;
  }

  getByPhase(phase: string) {
    return this.delivery()?.checklist.filter(i => i.phase === phase) || [];
  }

  isPhaseComplete(phase: string): boolean {
    const items = this.getByPhase(phase);
    return items.length > 0 && items.every(i => i.checked);
  }

  allPhasesComplete(): boolean {
    return this.isPhaseComplete('PUNCH_LIST') && this.isPhaseComplete('DOCS') && this.isPhaseComplete('INSPECTION');
  }

  initDelivery() {
    const items = [
      { description: 'Punch List zerado (0 itens abertos)', phase: 'PUNCH_LIST' },
      { description: 'Todas as não-conformidades resolvidas', phase: 'PUNCH_LIST' },
      { description: 'Documentação as-built entregue', phase: 'DOCS' },
      { description: 'Manual do proprietário entregue', phase: 'DOCS' },
      { description: 'Certificados e laudos técnicos', phase: 'DOCS' },
      { description: 'ART/RRT de conclusão', phase: 'DOCS' },
      { description: 'Vistoria com cliente realizada', phase: 'INSPECTION' },
      { description: 'Instalações elétricas testadas', phase: 'INSPECTION' },
      { description: 'Instalações hidráulicas testadas', phase: 'INSPECTION' },
      { description: 'Limpeza final concluída', phase: 'INSPECTION' },
    ];
    this.http.post<Delivery>(`/projects/${this.projectId}/delivery`, { checklistItems: items }).subscribe(d => this.delivery.set(d));
  }

  checkItem(itemId: string) {
    this.http.post<Delivery>(`/projects/${this.projectId}/delivery/checklist/${itemId}/check`, {}).subscribe(d => this.delivery.set(d));
  }

  provisional() {
    this.http.post<Delivery>(`/projects/${this.projectId}/delivery/provisional`, {}).subscribe(d => this.delivery.set(d));
  }

  definitive() {
    this.http.post<Delivery>(`/projects/${this.projectId}/delivery/definitive`, {}).subscribe(d => this.delivery.set(d));
  }
}

export const routes: Routes = [{ path: '', component: DeliveryComponent }];
