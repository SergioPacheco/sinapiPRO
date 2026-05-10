import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

interface Delivery {
  id: string; status: string; provisionalDate: string; definitiveDate: string;
  deliveredBy: string; receivedBy: string; notes: string;
  checklist: { id: string; description: string; checked: boolean }[];
}

@Component({
  selector: 'app-delivery',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatCheckboxModule, PageHeader],
  template: `
    <page-header title="Entrega da Obra" subtitle="Checklist e termos de recebimento">
      @if (!delivery()) {
        <button mat-flat-button color="primary" (click)="initDelivery()"><mat-icon>add</mat-icon> Iniciar Entrega</button>
      }
    </page-header>

    @if (delivery(); as d) {
      <div class="delivery-status">
        <mat-card>
          <mat-card-content>
            <div class="status-row">
              <span class="badge" [attr.data-status]="d.status">{{ statusLabel[d.status] || d.status }}</span>
              @if (d.provisionalDate) { <span>Provisório: {{ d.provisionalDate }}</span> }
              @if (d.definitiveDate) { <span>Definitivo: {{ d.definitiveDate }}</span> }
            </div>
          </mat-card-content>
        </mat-card>
      </div>

      <h3>Checklist de Entrega</h3>
      <div class="checklist">
        @for (item of d.checklist; track item.id) {
          <mat-checkbox [checked]="item.checked" (change)="checkItem(item.id)" [disabled]="item.checked">
            {{ item.description }}
          </mat-checkbox>
        }
      </div>

      <div class="actions">
        @if (d.status === 'PENDING') {
          <button mat-flat-button color="primary" (click)="provisional()">
            <mat-icon>assignment_turned_in</mat-icon> Recebimento Provisório
          </button>
        }
        @if (d.status === 'PROVISIONAL') {
          <button mat-flat-button color="primary" (click)="definitive()">
            <mat-icon>verified</mat-icon> Recebimento Definitivo
          </button>
        }
      </div>
    } @else if (!loading()) {
      <p class="empty">Nenhum processo de entrega iniciado para esta obra.</p>
    }
  `,
  styles: `
    .status-row { display: flex; align-items: center; gap: 16px; }
    .badge { font-size: 12px; font-weight: 600; padding: 4px 12px; border-radius: 4px; text-transform: uppercase; }
    .badge[data-status="PENDING"] { background: rgba(245,158,11,.12); color: #f59e0b; }
    .badge[data-status="PROVISIONAL"] { background: rgba(59,130,246,.12); color: #3b82f6; }
    .badge[data-status="DEFINITIVE"] { background: rgba(16,185,129,.12); color: #10b981; }
    .checklist { display: flex; flex-direction: column; gap: 8px; margin: 16px 0; }
    .actions { margin-top: 24px; display: flex; gap: 12px; }
    .empty { text-align: center; padding: 40px; color: var(--mat-sys-on-surface-variant); }
    h3 { margin: 24px 0 8px; color: var(--mat-sys-on-surface); }
  `,
})
export class DeliveryComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  delivery = signal<Delivery | null>(null);
  loading = signal(true);

  statusLabel: Record<string, string> = {
    PENDING: 'Pendente', PROVISIONAL: 'Recebimento Provisório', DEFINITIVE: 'Entregue',
  };

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

  initDelivery() {
    const items = [
      'Punch List zerado', 'Documentação as-built entregue', 'Limpeza final concluída',
      'Instalações testadas', 'Chaves entregues', 'Manual do proprietário entregue',
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
