import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe, SlicePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-service-orders',
  standalone: true,
  imports: [DecimalPipe, SlicePipe, FormsModule, ButtonModule, DialogModule, InputTextModule, TextareaModule, DropdownModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Atendimento / Ordens de Serviço</h3>
      <div class="flex gap-2">
        <span class="kpi-badge">{{ stats().total }} total</span>
        <span class="kpi-badge warn">{{ stats().overdue }} atrasadas</span>
        <span class="kpi-badge info">{{ stats().avgHours | number:'1.0-0' }}h média</span>
        <p-button label="Nova OS" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
      </div>
    </div>

    <!-- Kanban Board -->
    <div class="kanban-board">
      @for (col of columns; track col.status) {
        <div class="kanban-column">
          <div class="kanban-header">{{ col.label }} <span class="badge">{{ getByStatus(col.status).length }}</span></div>
          <div class="kanban-cards">
            @for (ticket of getByStatus(col.status); track ticket.id) {
              <div class="kanban-card" [class.overdue]="ticket.overdue" (click)="openDetail(ticket)">
                <div class="card-priority" [class]="ticket.priority"></div>
                <div class="card-body">
                  <div class="card-title">{{ ticket.category }}</div>
                  <div class="card-desc">{{ ticket.description | slice:0:60 }}</div>
                  <div class="card-meta">{{ ticket.clientName }} · {{ ticket.dueDate }}</div>
                </div>
              </div>
            }
          </div>
        </div>
      }
    </div>

    <!-- Detail Drawer (simulated as dialog) -->
    <p-dialog [(visible)]="detailVisible" [style]="{width:'500px'}" [header]="selectedTicket?.category" [modal]="true">
      @if (selectedTicket) {
        <div class="flex flex-column gap-3">
          <div><strong>Cliente:</strong> {{ selectedTicket.clientName }}</div>
          <div><strong>Descrição:</strong> {{ selectedTicket.description }}</div>
          <div><strong>Prioridade:</strong> {{ selectedTicket.priority }}</div>
          <div><strong>Vencimento:</strong> {{ selectedTicket.dueDate }}</div>
          <div><strong>Responsável:</strong> {{ selectedTicket.assignedTo || 'Não atribuído' }}</div>
          @if (selectedTicket.resolution) { <div><strong>Resolução:</strong> {{ selectedTicket.resolution }}</div> }
        </div>
        <ng-template pTemplate="footer">
          @if (selectedTicket.status === 'OPEN') { <p-button label="Atribuir" icon="pi pi-user" (onClick)="assign()" /> }
          @if (selectedTicket.status === 'IN_PROGRESS') { <p-button label="Resolver" icon="pi pi-check" severity="success" (onClick)="resolve()" /> }
          @if (selectedTicket.status === 'RESOLVED') { <p-button label="Encerrar" icon="pi pi-lock" severity="secondary" (onClick)="close()" /> }
        </ng-template>
      }
    </p-dialog>

    <!-- New OS Dialog -->
    <p-dialog header="Nova Ordem de Serviço" [(visible)]="showNew" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Cliente</label><input pInputText [(ngModel)]="newTicket.clientName" class="w-full" /></div>
        <div><label>Categoria</label><p-dropdown [options]="categoryOptions" [(ngModel)]="newTicket.category" styleClass="w-full" /></div>
        <div><label>Descrição</label><textarea pInputTextarea [(ngModel)]="newTicket.description" rows="3" class="w-full"></textarea></div>
        <div><label>Prioridade</label><p-dropdown [options]="priorityOptions" [(ngModel)]="newTicket.priority" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Criar" icon="pi pi-check" (onClick)="createTicket()" />
      </ng-template>
    </p-dialog>
  `,
  styles: [`
    .kanban-board { display: flex; gap: 12px; overflow-x: auto; min-height: 500px; }
    .kanban-column { flex: 1; min-width: 240px; background: var(--sp-surface-ground); border-radius: var(--sp-radius); padding: 8px; }
    .kanban-header { font-weight: 600; font-size: 13px; padding: 8px; text-transform: uppercase; color: var(--sp-text-muted); }
    .kanban-header .badge { background: var(--sp-primary-subtle); color: var(--sp-primary); border-radius: 10px; padding: 2px 8px; font-size: 11px; }
    .kanban-cards { display: flex; flex-direction: column; gap: 8px; }
    .kanban-card { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 6px; padding: 10px; cursor: pointer; display: flex; gap: 8px; }
    .kanban-card:hover { border-color: var(--sp-primary); }
    .kanban-card.overdue { border-left: 3px solid var(--sp-danger); }
    .card-priority { width: 4px; border-radius: 2px; }
    .card-priority.HIGH { background: var(--sp-danger); } .card-priority.MEDIUM { background: var(--sp-warning); } .card-priority.LOW { background: var(--sp-success); }
    .card-title { font-weight: 600; font-size: 13px; } .card-desc { font-size: 12px; color: var(--sp-text-muted); margin-top: 4px; }
    .card-meta { font-size: 11px; color: var(--sp-text-muted); margin-top: 6px; }
    .kpi-badge { font-size: 12px; padding: 4px 10px; border-radius: 12px; background: var(--sp-surface-ground); }
    .kpi-badge.warn { color: var(--sp-danger); } .kpi-badge.info { color: var(--sp-primary); }
  `],
})
export class ServiceOrdersComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  tickets = signal<any[]>([]);
  stats = signal<any>({ total: 0, overdue: 0, avgHours: 0 });
  columns = [
    { status: 'OPEN', label: 'Aberto' },
    { status: 'IN_PROGRESS', label: 'Em Andamento' },
    { status: 'RESOLVED', label: 'Resolvido' },
    { status: 'CLOSED', label: 'Encerrado' },
  ];
  categoryOptions = ['Manutenção', 'Garantia', 'Reparo', 'Vistoria', 'Outros'].map(v => ({ label: v, value: v }));
  priorityOptions = ['HIGH', 'MEDIUM', 'LOW'].map(v => ({ label: v, value: v }));

  showNew = false;
  detailVisible = false;
  selectedTicket: any = null;
  newTicket: any = { clientName: '', category: '', description: '', priority: 'MEDIUM' };

  ngOnInit() {
    this.http.get<any>('/after-sales/tickets?page=0&size=100').subscribe(res => {
      const list = res.content || res;
      this.tickets.set(list);
      const overdue = list.filter((t: any) => t.dueDate && new Date(t.dueDate) < new Date() && !['CLOSED', 'RESOLVED'].includes(t.status)).length;
      this.stats.set({ total: list.length, overdue, avgHours: 0 });
    });
  }

  getByStatus(status: string) { return this.tickets().filter(t => t.status === status); }
  openDetail(ticket: any) { this.selectedTicket = ticket; this.detailVisible = true; }

  createTicket() {
    this.http.post('/after-sales/tickets', this.newTicket).subscribe({
      next: (t: any) => { this.tickets.update(list => [t, ...list]); this.showNew = false; this.newTicket = { clientName: '', category: '', description: '', priority: 'MEDIUM' }; },
      error: () => this.messages.add({ severity: 'error', summary: 'Erro ao criar OS' }),
    });
  }

  assign() { this.http.post(`/after-sales/tickets/${this.selectedTicket.id}/assign`, { assignedTo: 'Técnico' }).subscribe({ next: () => { this.selectedTicket.status = 'IN_PROGRESS'; this.detailVisible = false; this.ngOnInit(); } }); }
  resolve() { this.http.post(`/after-sales/tickets/${this.selectedTicket.id}/resolve`, { resolution: 'Resolvido' }).subscribe({ next: () => { this.selectedTicket.status = 'RESOLVED'; this.detailVisible = false; this.ngOnInit(); } }); }
  close() { this.http.post(`/after-sales/tickets/${this.selectedTicket.id}/close`, {}).subscribe({ next: () => { this.selectedTicket.status = 'CLOSED'; this.detailVisible = false; this.ngOnInit(); } }); }
}
