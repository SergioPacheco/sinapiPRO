interface Payable { id: string; description: string; supplierName: string; amount: number; dueDate: string; status: string; paidAt: string | null; }
interface PageResponse<T> { content: T[]; totalElements: number; totalPages: number; size: number; number: number; }

import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe, DatePipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-payable-list',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  template: `
    <div class="summary">
      <div class="stat"><span class="label">Total Pendente</span><span class="value danger">R$ {{ totalPending() | number:'1.2-2' }}</span></div>
      <div class="stat"><span class="label">Vencidas</span><span class="value danger">{{ overdueCount() }}</span></div>
      <div class="stat"><span class="label">A vencer (30d)</span><span class="value">{{ upcomingCount() }}</span></div>
    </div>
    <table>
      <thead><tr><th>Descrição</th><th>Fornecedor</th><th>Valor</th><th>Vencimento</th><th>Status</th><th>Ações</th></tr></thead>
      <tbody>
        @for (p of payables(); track p.id) {
          <tr>
            <td>{{ p.description }}</td>
            <td>{{ p.supplierName }}</td>
            <td class="num">R$ {{ p.amount | number:'1.2-2' }}</td>
            <td>{{ p.dueDate | date:'dd/MM/yyyy' }}</td>
            <td><span class="badge" [attr.data-status]="p.status">{{ p.status }}</span></td>
            <td class="actions">
              @if (p.status === 'PENDING' || p.status === 'OVERDUE') {
                @if (auth.hasPermission('finance.pay')) {
                  <button class="btn-sm btn-success" (click)="pay(p.id)">Pagar</button>
                }
                <button class="btn-sm btn-danger" (click)="cancel(p.id)">Cancelar</button>
              }
            </td>
          </tr>
        }
      </tbody>
    </table>
  `,
  styles: [`
    .summary { display: flex; gap: 2rem; margin-bottom: 1.5rem;
      .stat { .label { font-size: 0.75rem; color: #8a8aaa; display: block; } .value { font-size: 1.3rem; font-weight: 700; } .danger { color: #ef5350; } }
    }
    table { width: 100%; border-collapse: collapse; background: #16213e; border-radius: 10px; overflow: hidden; }
    th { text-align: left; padding: 0.75rem 1rem; color: #8a8aaa; font-size: 0.8rem; border-bottom: 1px solid #2a2a4a; }
    td { padding: 0.75rem 1rem; border-bottom: 1px solid #1a1a2e; }
    .num { text-align: right; font-family: monospace; }
    .actions { display: flex; gap: 0.5rem; }
    .btn-sm { padding: 0.3rem 0.6rem; border-radius: 4px; border: none; cursor: pointer; font-size: 0.75rem; }
    .btn-success { background: #66bb6a; color: #1a1a2e; }
    .btn-danger { background: #ef5350; color: #fff; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;
      &[data-status="PENDING"] { background: #e65100; color: #ffcc80; }
      &[data-status="PAID"] { background: #1b5e20; color: #a5d6a7; }
      &[data-status="OVERDUE"] { background: #b71c1c; color: #ef9a9a; }
      &[data-status="CANCELLED"] { background: #4a4a6a; color: #b0b0b0; }
    }
  `]
})
export class PayableListComponent implements OnInit {
  private http = inject(HttpClient);
  auth = inject(AuthService);
  payables = signal<Payable[]>([]);
  totalPending = signal(0);
  overdueCount = signal(0);
  upcomingCount = signal(0);

  ngOnInit() { this.load(); }

  private load() {
    this.http.get<PageResponse<Payable>>(`/finance/payables`).subscribe(res => {
      this.payables.set(res.content);
      const pending = res.content.filter(p => p.status === 'PENDING' || p.status === 'OVERDUE');
      this.totalPending.set(pending.reduce((sum, p) => sum + p.amount, 0));
      this.overdueCount.set(res.content.filter(p => p.status === 'OVERDUE').length);
      this.upcomingCount.set(pending.length);
    });
  }

  pay(id: string) {
    this.http.post(`/finance/payables/${id}/pay`, {
      amount: null, date: new Date().toISOString().split('T')[0]
    }).subscribe(() => this.load());
  }

  cancel(id: string) {
    this.http.post(`/finance/payables/${id}/cancel`, {}).subscribe(() => this.load());
  }
}
