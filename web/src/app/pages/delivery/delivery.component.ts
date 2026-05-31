import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';

interface DeliveryChecklist { id: string; projectName: string; status: string; scheduledDate: string; completedDate: string | null; items: DeliveryItem[]; }
interface DeliveryItem { id: string; description: string; checked: boolean; notes: string; }

@Component({
  selector: 'app-delivery',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div class="delivery">
      <h2>Entrega de Obra</h2>
      @for (checklist of checklists(); track checklist.id) {
        <div class="checklist-card">
          <div class="card-header">
            <div>
              <strong>{{ checklist.projectName }}</strong>
              <span class="badge" [attr.data-status]="checklist.status">{{ checklist.status }}</span>
            </div>
            <span class="date">Prevista: {{ checklist.scheduledDate | date:'dd/MM/yyyy' }}</span>
          </div>
          <div class="items">
            @for (item of checklist.items; track item.id) {
              <label class="check-item" [class.done]="item.checked">
                <input type="checkbox" [checked]="item.checked" (change)="toggle(checklist.id, item.id, !item.checked)" />
                {{ item.description }}
              </label>
            }
          </div>
          <div class="card-footer">
            <span>{{ checklist.items.filter(i => i.checked).length }}/{{ checklist.items.length }} itens</span>
            @if (checklist.status === 'PENDING' && allChecked(checklist)) {
              <button class="btn-success" (click)="complete(checklist.id)">Concluir Entrega</button>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    h2 { margin: 0 0 1.5rem; color: #e0e0e0; }
    .checklist-card { background: #16213e; border-radius: 10px; border: 1px solid #2a2a4a; padding: 1.5rem; margin-bottom: 1.5rem; }
    .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;
      strong { color: #e0e0e0; margin-right: 0.75rem; }
      .date { color: #8a8aaa; font-size: 0.85rem; } }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;
      &[data-status="PENDING"] { background: #e65100; color: #ffcc80; }
      &[data-status="COMPLETED"] { background: #1b5e20; color: #a5d6a7; } }
    .items { display: flex; flex-direction: column; gap: 0.5rem; margin-bottom: 1rem; }
    .check-item { display: flex; align-items: center; gap: 0.5rem; padding: 0.5rem; border-radius: 6px; cursor: pointer;
      &:hover { background: #1a2744; }
      &.done { color: #6a6a8a; text-decoration: line-through; }
      input { accent-color: #4fc3f7; } }
    .card-footer { display: flex; justify-content: space-between; align-items: center; color: #8a8aaa; font-size: 0.85rem; }
    .btn-success { padding: 0.5rem 1rem; border-radius: 6px; border: none; background: #66bb6a; color: #1a1a2e; font-weight: 600; cursor: pointer; }
  `]
})
export class DeliveryComponent implements OnInit {
  private http = inject(HttpClient);
  checklists = signal<DeliveryChecklist[]>([]);

  ngOnInit() { this.load(); }

  load() { this.http.get<DeliveryChecklist[]>('/delivery/checklists').subscribe(d => this.checklists.set(d)); }

  toggle(checklistId: string, itemId: string, checked: boolean) {
    this.http.patch(`/delivery/checklists/${checklistId}/items/${itemId}`, { checked }).subscribe(() => this.load());
  }

  complete(id: string) { this.http.post(`/delivery/checklists/${id}/complete`, {}).subscribe(() => this.load()); }

  allChecked(c: DeliveryChecklist) { return c.items.every(i => i.checked); }
}
