interface Receivable { id: string; description: string; clientName: string; amount: number; dueDate: string; status: string; receivedAt: string | null; }
interface PageResponse<T> { content: T[]; totalElements: number; totalPages: number; size: number; number: number; }

import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe, DatePipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-receivable-list',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  template: `
    <div class="summary">
      <div class="stat"><span class="label">Total a Receber</span><span class="value good">R$ {{ totalPending() | number:'1.2-2' }}</span></div>
      <div class="stat"><span class="label">Vencidas</span><span class="value danger">{{ overdueCount() }}</span></div>
    </div>
    <table>
      <thead><tr><th>Descrição</th><th>Cliente</th><th>Valor</th><th>Vencimento</th><th>Status</th><th>Ações</th></tr></thead>
      <tbody>
        @for (r of receivables(); track r.id) {
          <tr>
            <td>{{ r.description }}</td>
            <td>{{ r.clientName }}</td>
            <td class="num">R$ {{ r.amount | number:'1.2-2' }}</td>
            <td>{{ r.dueDate | date:'dd/MM/yyyy' }}</td>
            <td><span class="badge" [attr.data-status]="r.status">{{ r.status }}</span></td>
            <td>
              @if ((r.status === 'PENDING' || r.status === 'OVERDUE') && auth.hasPermission('finance.receive')) {
                <button class="btn-sm btn-success" (click)="receive(r.id)">Receber</button>
              }
            </td>
          </tr>
        }
      </tbody>
    </table>
  `,
  styles: [`
    .summary { display: flex; gap: 2rem; margin-bottom: 1.5rem;
      .stat { .label { font-size: 0.75rem; color: #8a8aaa; display: block; } .value { font-size: 1.3rem; font-weight: 700; } .good { color: #66bb6a; } .danger { color: #ef5350; } }
    }
    table { width: 100%; border-collapse: collapse; background: #16213e; border-radius: 10px; overflow: hidden; }
    th { text-align: left; padding: 0.75rem 1rem; color: #8a8aaa; font-size: 0.8rem; border-bottom: 1px solid #2a2a4a; }
    td { padding: 0.75rem 1rem; border-bottom: 1px solid #1a1a2e; }
    .num { text-align: right; font-family: monospace; }
    .btn-sm { padding: 0.3rem 0.6rem; border-radius: 4px; border: none; cursor: pointer; font-size: 0.75rem; }
    .btn-success { background: #66bb6a; color: #1a1a2e; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;
      &[data-status="PENDING"] { background: #e65100; color: #ffcc80; }
      &[data-status="RECEIVED"] { background: #1b5e20; color: #a5d6a7; }
      &[data-status="OVERDUE"] { background: #b71c1c; color: #ef9a9a; }
      &[data-status="CANCELLED"] { background: #4a4a6a; color: #b0b0b0; }
    }
  `]
})
export class ReceivableListComponent implements OnInit {
  private http = inject(HttpClient);
  auth = inject(AuthService);
  receivables = signal<Receivable[]>([]);
  totalPending = signal(0);
  overdueCount = signal(0);

  ngOnInit() { this.load(); }

  private load() {
    this.http.get<PageResponse<Receivable>>(`/finance/receivables`).subscribe(res => {
      this.receivables.set(res.content);
      const pending = res.content.filter(r => r.status === 'PENDING' || r.status === 'OVERDUE');
      this.totalPending.set(pending.reduce((sum, r) => sum + r.amount, 0));
      this.overdueCount.set(res.content.filter(r => r.status === 'OVERDUE').length);
    });
  }

  receive(id: string) {
    this.http.post(`/finance/receivables/${id}/receive`, {
      amount: null, date: new Date().toISOString().split('T')[0]
    }).subscribe(() => this.load());
  }
}
