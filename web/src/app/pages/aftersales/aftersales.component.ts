import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Ticket { id: string; number: number; subject: string; description: string; status: string; priority: string; clientName: string; projectName: string; createdAt: string; resolvedAt: string | null; }

@Component({
  selector: 'app-aftersales',
  standalone: true,
  imports: [DatePipe, FormsModule],
  template: `
    <div class="aftersales">
      <div class="header">
        <h2>Pós-Venda — Tickets</h2>
        <button class="btn-primary" (click)="showForm = true">+ Novo Ticket</button>
      </div>
      <div class="filters">
        <select [(ngModel)]="statusFilter" (change)="load()">
          <option value="">Todos</option>
          <option value="OPEN">Abertos</option>
          <option value="IN_PROGRESS">Em Andamento</option>
          <option value="RESOLVED">Resolvidos</option>
        </select>
      </div>
      <table>
        <thead><tr><th>#</th><th>Assunto</th><th>Cliente</th><th>Obra</th><th>Prioridade</th><th>Status</th><th>Criado</th><th>Ações</th></tr></thead>
        <tbody>
          @for (t of tickets(); track t.id) {
            <tr>
              <td>{{ t.number }}</td>
              <td>{{ t.subject }}</td>
              <td>{{ t.clientName }}</td>
              <td>{{ t.projectName }}</td>
              <td><span class="badge priority" [attr.data-p]="t.priority">{{ t.priority }}</span></td>
              <td><span class="badge" [attr.data-status]="t.status">{{ t.status }}</span></td>
              <td>{{ t.createdAt | date:'dd/MM/yy' }}</td>
              <td>
                @if (t.status === 'OPEN') { <button class="btn-sm" (click)="start(t.id)">Iniciar</button> }
                @if (t.status === 'IN_PROGRESS') { <button class="btn-sm btn-success" (click)="resolve(t.id)">Resolver</button> }
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    h2 { margin: 0; color: #e0e0e0; }
    .filters { margin-bottom: 1rem; select { padding: 0.5rem; border-radius: 6px; border: 1px solid #3a3a5a; background: #1a1a2e; color: #e0e0e0; } }
    .btn-primary { padding: 0.5rem 1rem; border-radius: 6px; border: none; background: #4fc3f7; color: #1a1a2e; font-weight: 600; cursor: pointer; }
    .btn-sm { padding: 0.3rem 0.6rem; border-radius: 4px; border: none; cursor: pointer; font-size: 0.75rem; background: #3a3a5a; color: #e0e0e0; }
    .btn-success { background: #66bb6a; color: #1a1a2e; }
    table { width: 100%; border-collapse: collapse; background: #16213e; border-radius: 10px; overflow: hidden; }
    th { text-align: left; padding: 0.75rem 1rem; color: #8a8aaa; font-size: 0.8rem; border-bottom: 1px solid #2a2a4a; }
    td { padding: 0.75rem 1rem; border-bottom: 1px solid #1a1a2e; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;
      &[data-status="OPEN"] { background: #e65100; color: #ffcc80; }
      &[data-status="IN_PROGRESS"] { background: #0f3460; color: #4fc3f7; }
      &[data-status="RESOLVED"] { background: #1b5e20; color: #a5d6a7; }
      &[data-p="HIGH"] { background: #b71c1c; color: #ef9a9a; }
      &[data-p="MEDIUM"] { background: #e65100; color: #ffcc80; }
      &[data-p="LOW"] { background: #4a4a6a; color: #b0b0b0; }
    }
  `]
})
export class AftersalesComponent implements OnInit {
  private http = inject(HttpClient);
  tickets = signal<Ticket[]>([]);
  statusFilter = '';
  showForm = false;

  ngOnInit() { this.load(); }

  load() {
    const params: any = {};
    if (this.statusFilter) params.status = this.statusFilter;
    this.http.get<any>('/aftersales/tickets', { params }).subscribe(res => this.tickets.set(res.content || res));
  }

  start(id: string) { this.http.post(`/aftersales/tickets/${id}/start`, {}).subscribe(() => this.load()); }
  resolve(id: string) { this.http.post(`/aftersales/tickets/${id}/resolve`, {}).subscribe(() => this.load()); }
}
