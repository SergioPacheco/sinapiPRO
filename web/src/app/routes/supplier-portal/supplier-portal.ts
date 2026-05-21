import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SupplierPortalService, PortalQuotation } from './supplier-portal.service';

@Component({
  selector: 'app-supplier-portal',
  standalone: true,
  imports: [FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="portal-container">
      <div class="portal-header">
        <img src="assets/logo.svg" alt="SinapiPRO" class="logo" onerror="this.style.display='none'" />
        <h1>Portal do Fornecedor</h1>
        <p>Responda à solicitação de cotação abaixo</p>
      </div>

      @if (loading()) {
        <mat-spinner diameter="40" />
      } @else if (error()) {
        <mat-card class="error-card">
          <mat-icon color="warn">error</mat-icon>
          <h2>{{ error() }}</h2>
          <p>Verifique o link recebido por e-mail ou entre em contato com o solicitante.</p>
        </mat-card>
      } @else if (submitted()) {
        <mat-card class="success-card">
          <mat-icon color="primary">check_circle</mat-icon>
          <h2>Cotação enviada com sucesso!</h2>
          <p>Obrigado, {{ quotation()?.supplierName }}. Sua proposta foi registrada.</p>
        </mat-card>
      } @else if (quotation()) {
        <mat-card>
          <mat-card-header>
            <mat-card-title>Solicitação de Cotação</mat-card-title>
            <mat-card-subtitle>Fornecedor: {{ quotation()!.supplierName }}</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            <div class="quotation-info">
              <div class="info-row"><strong>Item:</strong> {{ quotation()!.itemDescription }}</div>
              <div class="info-row"><strong>Quantidade:</strong> {{ quotation()!.quantity }} {{ quotation()!.unit }}</div>
              @if (quotation()!.deadline) {
                <div class="info-row"><strong>Prazo para resposta:</strong> {{ quotation()!.deadline }}</div>
              }
            </div>

            @if (quotation()!.alreadyResponded) {
              <p class="already-responded">Você já respondeu a esta cotação.</p>
            } @else {
              <form (ngSubmit)="submit()" class="response-form">
                <mat-form-field appearance="outline">
                  <mat-label>Preço Unitário (R$)</mat-label>
                  <input matInput type="number" step="0.01" [(ngModel)]="unitPrice" name="unitPrice" required />
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Prazo de Entrega (dias)</mat-label>
                  <input matInput type="number" [(ngModel)]="deliveryDays" name="deliveryDays" />
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Observações</mat-label>
                  <textarea matInput [(ngModel)]="notes" name="notes" rows="3"></textarea>
                </mat-form-field>

                <button mat-flat-button color="primary" type="submit" [disabled]="!unitPrice || unitPrice <= 0">
                  <mat-icon>send</mat-icon> Enviar Cotação
                </button>
              </form>
            }
          </mat-card-content>
        </mat-card>
      }
    </div>
  `,
  styles: [`
    .portal-container { max-width: 600px; margin: 40px auto; padding: 0 16px; }
    .portal-header { text-align: center; margin-bottom: 24px; }
    .portal-header h1 { font-size: 24px; margin: 8px 0; }
    .portal-header p { color: var(--mat-sys-on-surface-variant); }
    .logo { height: 48px; margin-bottom: 12px; }
    .quotation-info { margin: 16px 0; display: grid; gap: 8px; }
    .info-row { padding: 8px 12px; background: var(--mat-sys-surface-container-low); border-radius: 8px; }
    .response-form { display: grid; gap: 12px; margin-top: 24px; }
    .error-card, .success-card { text-align: center; padding: 32px; }
    .error-card mat-icon, .success-card mat-icon { font-size: 48px; width: 48px; height: 48px; }
    .already-responded { color: var(--mat-sys-on-surface-variant); font-style: italic; margin-top: 16px; }
    mat-spinner { margin: 40px auto; }
  `]
})
export class SupplierPortalComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly portalService = inject(SupplierPortalService);

  loading = signal(true);
  error = signal<string | null>(null);
  submitted = signal(false);
  quotation = signal<PortalQuotation | null>(null);

  unitPrice = 0;
  deliveryDays: number | null = null;
  notes = '';
  private token = '';

  ngOnInit() {
    this.token = this.route.snapshot.queryParams['token'] || '';
    if (!this.token) {
      this.loading.set(false);
      this.error.set('Token não informado');
      return;
    }
    this.portalService.getQuotation(this.token).subscribe({
      next: q => { this.quotation.set(q); this.loading.set(false); },
      error: err => {
        this.loading.set(false);
        this.error.set(err.status === 410 ? 'Link expirado' : 'Link inválido ou expirado');
      }
    });
  }

  submit() {
    if (!this.unitPrice || this.unitPrice <= 0) return;
    this.portalService.submitResponse(this.token, {
      unitPrice: this.unitPrice,
      deliveryDays: this.deliveryDays ?? undefined,
      notes: this.notes || undefined,
    }).subscribe({
      next: () => this.submitted.set(true),
      error: err => this.error.set(err.error?.message || 'Erro ao enviar cotação'),
    });
  }
}
