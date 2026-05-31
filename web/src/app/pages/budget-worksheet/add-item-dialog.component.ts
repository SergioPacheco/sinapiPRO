import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';

interface CompositionResult {
  id: string;
  sinapiCode: string;
  description: string;
  unit: string;
  unitCost: number;
}

@Component({
  selector: 'app-add-item-dialog',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  template: `
    <div class="overlay" (click)="close.emit()">
      <div class="dialog" (click)="$event.stopPropagation()">
        <h3>Adicionar Item ao Orçamento</h3>

        <div class="search-box">
          <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar composição SINAPI..." (input)="search()" />
        </div>

        @if (results().length) {
          <div class="results">
            @for (c of results(); track c.id) {
              <div class="result-item" [class.selected]="selected()?.id === c.id" (click)="select(c)">
                <span class="code">{{ c.sinapiCode }}</span>
                <span class="desc">{{ c.description }}</span>
                <span class="cost">R$ {{ c.unitCost | number:'1.2-2' }}/{{ c.unit }}</span>
              </div>
            }
          </div>
        }

        @if (selected(); as s) {
          <div class="form-fields">
            <label>Quantidade ({{ s.unit }})<input type="number" [(ngModel)]="quantity" min="0.01" step="0.01" /></label>
            <label>BDI (%)<input type="number" [(ngModel)]="bdiPct" min="0" step="0.1" /></label>
            <div class="preview">
              <span>Custo: R$ {{ (s.unitCost * quantity) | number:'1.2-2' }}</span>
              <span>Com BDI: R$ {{ (s.unitCost * quantity * (1 + bdiPct/100)) | number:'1.2-2' }}</span>
            </div>
          </div>
        }

        <div class="actions">
          <button class="btn-outline" (click)="close.emit()">Cancelar</button>
          <button class="btn-primary" [disabled]="!selected() || quantity <= 0" (click)="add()">Adicionar</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .dialog { background: #16213e; border-radius: 12px; padding: 2rem; width: 600px; max-height: 80vh; overflow-y: auto; border: 1px solid #2a2a4a; }
    h3 { margin: 0 0 1.5rem; color: #e0e0e0; }
    .search-box input { width: 100%; padding: 0.75rem; border-radius: 6px; border: 1px solid #3a3a5a; background: #1a1a2e; color: #e0e0e0; &:focus { outline: none; border-color: #4fc3f7; } }
    .results { max-height: 200px; overflow-y: auto; margin: 1rem 0; }
    .result-item { padding: 0.6rem; border-radius: 6px; cursor: pointer; display: flex; gap: 0.75rem; align-items: center;
      &:hover { background: #1a2744; }
      &.selected { background: #0f3460; border: 1px solid #4fc3f7; }
      .code { color: #4fc3f7; font-family: monospace; font-size: 0.8rem; min-width: 80px; }
      .desc { flex: 1; font-size: 0.85rem; }
      .cost { color: #8a8aaa; font-size: 0.8rem; }
    }
    .form-fields { margin: 1.5rem 0; display: flex; gap: 1rem; flex-wrap: wrap;
      label { display: flex; flex-direction: column; gap: 0.25rem; font-size: 0.8rem; color: #8a8aaa;
        input { padding: 0.5rem; border-radius: 6px; border: 1px solid #3a3a5a; background: #1a1a2e; color: #e0e0e0; width: 140px; }
      }
      .preview { width: 100%; display: flex; gap: 1.5rem; margin-top: 0.5rem; font-size: 0.9rem; color: #b0b0b0; }
    }
    .actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem; }
    .btn-primary { padding: 0.5rem 1.25rem; border-radius: 6px; border: none; background: #4fc3f7; color: #1a1a2e; font-weight: 600; cursor: pointer; &:disabled { opacity: 0.4; } }
    .btn-outline { padding: 0.5rem 1.25rem; border-radius: 6px; border: 1px solid #4a4a6a; background: transparent; color: #b0b0b0; cursor: pointer; }
  `]
})
export class AddItemDialogComponent {
  @Input() budgetId = '';
  @Output() close = new EventEmitter<void>();
  @Output() added = new EventEmitter<void>();

  private http = inject(HttpClient);

  searchTerm = '';
  results = signal<CompositionResult[]>([]);
  selected = signal<CompositionResult | null>(null);
  quantity = 1;
  bdiPct = 25;

  search() {
    if (this.searchTerm.length < 2) return;
    this.http.get<CompositionResult[]>(`/compositions/search`, {
      params: { q: this.searchTerm, limit: '10' }
    }).subscribe(r => this.results.set(r));
  }

  select(c: CompositionResult) { this.selected.set(c); }

  add() {
    const s = this.selected();
    if (!s) return;
    this.http.post(`/budgets/${this.budgetId}/items`, {
      compositionId: s.id,
      quantity: this.quantity,
      bdiPct: this.bdiPct
    }).subscribe(() => this.added.emit());
  }
}
