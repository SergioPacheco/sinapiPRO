import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MessageService } from 'primeng/api';

export type ItemType = 'EMPTY' | 'LEVEL' | 'SUB_LEVEL' | 'COMPOSITION' | 'INPUT' | 'SUB_COMPOSITION';

export interface BudgetRow {
  id?: string;
  parentId?: string;
  stageId?: string;
  compositionId?: string;
  type: ItemType;
  level: number;
  expanded: boolean;
  code: string;         // itemização (01., 01.001)
  refCode: string;      // código SINAPI
  description: string;
  unit: string;
  quantity: number | null;
  unitCost: number | null;
  leisSociais: number;
  bdi: number;
  total: number;
  editable: boolean;    // linha em modo edição (nova)
  dirty: boolean;
  hidden: boolean;      // colapsado pelo pai
  children?: BudgetRow[];
}

@Injectable({ providedIn: 'root' })
export class BudgetTreeService {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  rows = signal<BudgetRow[]>([]);
  budgetId = signal<string | null>(null);
  budgetStatus = signal('DRAFT');
  summary = signal({ directCost: 0, bdiPct: 0, bdiAmount: 0, total: 0 });

  visibleRows = computed(() => this.rows().filter(r => !r.hidden));
  dirtyCount = computed(() => this.rows().filter(r => r.dirty).length);

  load(budgetId: string) {
    this.budgetId.set(budgetId);
    this.http.get<any>(`/budgets/${budgetId}/worksheet`).subscribe({
      next: ws => {
        this.summary.set({ directCost: ws.directCost, bdiPct: (ws.bdiPct || 0) * 100, bdiAmount: ws.bdiAmount || 0, total: ws.total });
        this.rows.set(this.buildFromWorksheet(ws.stages));
      },
    });
    this.http.get<any>(`/budgets/${budgetId}`).subscribe({
      next: b => this.budgetStatus.set(b.status || 'DRAFT'),
    });
  }

  /** Insere linha vazia ABAIXO do índice dado (ou no final) */
  insertEmpty(afterIndex: number): BudgetRow {
    const all = this.rows();
    const ref = all[afterIndex];
    const level = ref ? (ref.type === 'LEVEL' || ref.type === 'SUB_LEVEL' ? ref.level + 1 : ref.level) : 0;
    const stageId = ref?.stageId;
    const newRow: BudgetRow = {
      type: 'EMPTY', level, expanded: false, code: '', refCode: '', description: '',
      unit: '', quantity: null, unitCost: null, leisSociais: 0, bdi: 0, total: 0,
      editable: true, dirty: false, hidden: false, stageId,
    };
    // Inserir após filhos do item selecionado
    let insertIdx = afterIndex + 1;
    if (ref && (ref.type === 'LEVEL' || ref.type === 'SUB_LEVEL') && ref.expanded) {
      while (insertIdx < all.length && all[insertIdx].level > ref.level) insertIdx++;
    }
    all.splice(insertIdx, 0, newRow);
    this.rows.set([...all]);
    return newRow;
  }

  /** Transforma linha EMPTY no tipo correto após seleção no autocomplete */
  resolveRow(row: BudgetRow, selection: any) {
    if (selection._type === 'LEVEL') {
      row.type = 'LEVEL';
      row.description = selection.description || 'Novo Nível';
      row.level = 0;
      row.editable = true; // nome editável
      row.dirty = true;
      this.saveStage(row);
    } else if (selection._type === 'SUB_LEVEL') {
      row.type = 'SUB_LEVEL';
      row.description = selection.description || 'Sub-Nível';
      row.level = 1;
      row.editable = true;
      row.dirty = true;
      this.saveStage(row);
    } else {
      // Composição ou Insumo
      row.type = selection.items?.length > 0 || selection._type === 'COMPOSITION' ? 'COMPOSITION' : 'INPUT';
      row.compositionId = selection.id;
      row.refCode = selection.sinapiCode || '';
      row.description = selection.description;
      row.unit = selection.unit || '';
      row.editable = false;
      row.dirty = true;
      // Se composição, inserir filhos automaticamente
      if (row.type === 'COMPOSITION') {
        this.expandCompositionOnInsert(row, selection);
      }
    }
    this.renumber();
    this.rows.set([...this.rows()]);
  }

  /** Expande/colapsa */
  toggle(row: BudgetRow) {
    const all = this.rows();
    const idx = all.indexOf(row);
    if (row.expanded) {
      // Colapsar: esconder filhos
      for (let i = idx + 1; i < all.length && all[i].level > row.level; i++) {
        all[i].hidden = true;
      }
      row.expanded = false;
    } else if (row.type === 'COMPOSITION' && !row.children?.length) {
      // Primeira expansão de composição: buscar insumos do backend
      this.http.get<any>(`/compositions/${row.compositionId}`).subscribe({
        next: comp => {
          const subRows = this.buildSubItems(comp.items || [], row.level + 1);
          row.children = subRows;
          all.splice(idx + 1, 0, ...subRows);
          row.expanded = true;
          this.rows.set([...all]);
        },
      });
      return;
    } else {
      // Re-expandir: mostrar filhos
      for (let i = idx + 1; i < all.length && all[i].level > row.level; i++) {
        all[i].hidden = false;
      }
      row.expanded = true;
    }
    this.rows.set([...all]);
  }

  /** Edição de quantidade/valor */
  updateCell(row: BudgetRow) {
    row.total = (row.quantity || 0) * (row.unitCost || 0);
    row.dirty = true;
    this.recalcParentTotals();
    this.rows.set([...this.rows()]);
  }

  /** Excluir linha */
  deleteRow(row: BudgetRow) {
    if (row.id && row.type === 'LEVEL') {
      this.http.delete(`/budgets/${this.budgetId()}/stages/${row.stageId}`).subscribe({
        next: () => { this.messages.add({ severity: 'success', summary: 'Excluído' }); this.load(this.budgetId()!); },
      });
    } else if (row.id) {
      this.http.delete(`/budgets/${this.budgetId()}/items/${row.id}`).subscribe({
        next: () => { this.messages.add({ severity: 'success', summary: 'Excluído' }); this.load(this.budgetId()!); },
      });
    } else {
      const all = this.rows().filter(r => r !== row);
      this.rows.set(all);
    }
  }

  /** Salvar todas as linhas dirty */
  saveAll() {
    const dirty = this.rows().filter(r => r.dirty && r.compositionId && r.quantity && r.stageId);
    if (!dirty.length) return;
    const byStage = new Map<string, BudgetRow[]>();
    for (const r of dirty) { const l = byStage.get(r.stageId!) || []; l.push(r); byStage.set(r.stageId!, l); }
    let pending = byStage.size;
    for (const [stageId, items] of byStage) {
      const body = items.filter(r => !r.id).map(r => ({ compositionId: r.compositionId, quantity: r.quantity, unitCost: r.unitCost || undefined }));
      if (!body.length) { pending--; if (!pending) this.onSaved(); continue; }
      this.http.post(`/budgets/${this.budgetId()}/stages/${stageId}/items/bulk`, body).subscribe({
        next: () => { pending--; if (!pending) this.onSaved(); },
        error: () => { pending--; if (!pending) this.onSaved(); },
      });
    }
  }

  private onSaved() { this.messages.add({ severity: 'success', summary: 'Salvo' }); this.load(this.budgetId()!); }

  private saveStage(row: BudgetRow) {
    const sortOrder = this.rows().filter(r => r.type === 'LEVEL').indexOf(row) + 1;
    this.http.post<any>(`/budgets/${this.budgetId()}/stages`, { name: row.description, sortOrder }).subscribe({
      next: res => { row.stageId = res.id; row.id = res.id; row.dirty = false; },
    });
  }

  private expandCompositionOnInsert(row: BudgetRow, selection: any) {
    if (!selection.items?.length) {
      // Buscar filhos do backend
      this.http.get<any>(`/compositions/${selection.id}`).subscribe({
        next: comp => {
          const all = this.rows();
          const idx = all.indexOf(row);
          const subRows = this.buildSubItems(comp.items || [], row.level + 1);
          row.children = subRows;
          row.expanded = true;
          all.splice(idx + 1, 0, ...subRows);
          this.rows.set([...all]);
        },
      });
    } else {
      const all = this.rows();
      const idx = all.indexOf(row);
      const subRows = this.buildSubItems(selection.items, row.level + 1);
      row.children = subRows;
      row.expanded = true;
      all.splice(idx + 1, 0, ...subRows);
    }
  }

  private buildSubItems(items: any[], level: number): BudgetRow[] {
    return items.map(i => ({
      type: 'SUB_COMPOSITION' as ItemType, level, expanded: false,
      code: '', refCode: i.code || '', description: i.description,
      unit: i.unit || '', quantity: i.coefficient, unitCost: null,
      leisSociais: 0, bdi: 0, total: 0,
      editable: false, dirty: false, hidden: false,
    }));
  }

  private buildFromWorksheet(stages: any[]): BudgetRow[] {
    const rows: BudgetRow[] = [];
    const walk = (list: any[], level: number) => {
      for (const s of list) {
        rows.push({ type: 'LEVEL', level, expanded: true, id: s.id, stageId: s.id, code: '', refCode: '', description: s.name.replace(/^\d+\.\s*/, ''), unit: '', quantity: null, unitCost: null, leisSociais: 0, bdi: 0, total: s.subtotal || 0, editable: false, dirty: false, hidden: false });
        for (const i of s.items || []) {
          rows.push({ type: 'COMPOSITION', level: level + 1, expanded: false, id: i.id, stageId: s.id, compositionId: i.compositionId, code: '', refCode: i.code, description: i.description, unit: i.unit, quantity: i.quantity, unitCost: i.unitCost, leisSociais: 0, bdi: 0, total: i.totalCost, editable: false, dirty: false, hidden: false });
        }
        if (s.children?.length) walk(s.children, level + 1);
      }
    };
    walk(stages, 0);
    this.renumberRows(rows);
    return rows;
  }

  private renumber() { this.renumberRows(this.rows()); }

  private renumberRows(rows: BudgetRow[]) {
    const counters: number[] = [0, 0, 0, 0, 0];
    for (const r of rows) {
      if (r.type === 'LEVEL') { counters[0]++; counters[1] = 0; r.code = String(counters[0]).padStart(2, '0') + '.'; }
      else if (r.type === 'SUB_LEVEL') { counters[1]++; r.code = String(counters[0]).padStart(2, '0') + '.' + String(counters[1]).padStart(2, '0') + '.'; }
      else if (r.type === 'COMPOSITION' || r.type === 'INPUT') { counters[1]++; r.code = String(counters[0]).padStart(2, '0') + '.' + String(counters[1]).padStart(3, '0'); }
      else { r.code = ''; }
    }
  }

  private recalcParentTotals() {
    const all = this.rows();
    for (const r of all) {
      if (r.type === 'LEVEL' || r.type === 'SUB_LEVEL') {
        r.total = all.filter(c => c.stageId === r.stageId && c.type !== 'LEVEL' && c.type !== 'SUB_LEVEL' && c.type !== 'SUB_COMPOSITION').reduce((s, c) => s + (c.total || 0), 0);
      }
    }
  }
}
