import { Component, inject, OnInit, HostListener, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe, NgClass } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { MenuModule } from 'primeng/menu';
import { MessageService } from 'primeng/api';
import { BudgetTreeService, BudgetRow } from './budget-tree.service';
import { BdiDialogComponent } from './bdi-dialog.component';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-budget-worksheet',
  standalone: true,
  imports: [DecimalPipe, NgClass, FormsModule, AutoCompleteModule, ButtonModule, TooltipModule, MenuModule, StatusTagComponent, BdiDialogComponent],
  templateUrl: './budget-worksheet.component.html',
  styleUrl: './budget-worksheet.component.scss',
})
export class BudgetWorksheetComponent implements OnInit {
  tree = inject(BudgetTreeService);
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  selectedRow: BudgetRow | null = null;
  suggestions: any[] = [];
  showBdi = false;
  showComposition = false;
  showMultiply = false;
  showInfo = false;
  multiplyFactor = 1;
  budgetInfo: any = {};
  compositionItems: any[] = [];
  compositionName = '';
  compositionId: string | null = null;

  get budgetId() { return this.route.snapshot.paramMap.get('budgetId'); }

  menuItems = [
    { label: 'Acessar Composição', icon: 'pi pi-list', command: () => this.openComposition() },
    { label: 'Salvar como Própria', icon: 'pi pi-copy', command: () => this.saveAsOwn() },
    { separator: true },
    { label: 'Expandir Tudo (Analítico)', icon: 'pi pi-angle-double-down', command: () => this.expandAll() },
    { label: 'Colapsar Tudo (Sintético)', icon: 'pi pi-angle-double-up', command: () => this.collapseAll() },
    { separator: true },
    { label: 'Recalcular Valores', icon: 'pi pi-refresh', command: () => this.tree.load(this.budgetId!) },
    { label: 'Multiplicar Quantidades', icon: 'pi pi-times', command: () => this.showMultiply = true },
    { label: 'Aplicar Preço a Iguais', icon: 'pi pi-equals', command: () => this.applyPriceToEquals() },
    { separator: true },
    { label: 'Informações do Orçamento', icon: 'pi pi-info-circle', command: () => this.loadInfo() },
    { separator: true },
    { label: 'Sintético PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/worksheet.pdf`, '_blank') },
    { label: 'Analítico PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/analytical.pdf`, '_blank') },
    { label: 'Curva ABC', icon: 'pi pi-chart-bar', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/abc-services.pdf`, '_blank') },
    { separator: true },
    { label: 'Efetivar', icon: 'pi pi-lock', command: () => this.http.post<any>(`/budgets/${this.budgetId}/effectuate`, {}).subscribe({ next: r => this.tree.budgetStatus.set(r.status) }) },
  ];

  ngOnInit() { this.tree.load(this.budgetId!); }

  @HostListener('document:keydown.insert', ['$event'])
  onInsert(e: Event) { e.preventDefault(); this.insertItem(); }

  insertItem() {
    const idx = this.selectedRow ? this.tree.rows().indexOf(this.selectedRow) : this.tree.rows().length - 1;
    this.selectedRow = this.tree.insertEmpty(idx);
  }

  deleteSelected() { if (this.selectedRow) { this.tree.deleteRow(this.selectedRow); this.selectedRow = null; } }

  searchUnified(event: any) {
    const q = event.query || '';
    const n = this.tree.rows().filter(r => r.type === 'LEVEL').length + 1;
    const levelOption = { _type: 'LEVEL', label: `📁 ${String(n).padStart(2, '0')}. — Novo Nível`, id: null, description: '' };

    // Buscar composições e insumos em paralelo, montar resultado único
    this.http.get<any>(`/compositions?search=${encodeURIComponent(q)}&size=15`).subscribe({
      next: res => {
        const results: any[] = [levelOption];
        for (const c of (res.content || res)) {
          results.push({ ...c, _type: 'COMPOSITION', label: `🧱 ${c.sinapiCode} — ${c.description} (${c.unit})` });
        }
        this.suggestions = results;
      },
    });
  }

  onSelect(row: BudgetRow, sel: any) { this.tree.resolveRow(row, sel); }
  onStageBlur(row: BudgetRow) { if (row.description && !row.stageId) this.tree.resolveRow(row, { _type: 'LEVEL', description: row.description }); }

  /** Acessar Composição — abre dialog com insumos editáveis (coeficientes) */
  openComposition() {
    if (!this.selectedRow?.compositionId) { this.messages.add({ severity: 'warn', summary: 'Selecione uma composição' }); return; }
    this.compositionId = this.selectedRow.compositionId;
    this.http.get<any>(`/compositions/${this.compositionId}`).subscribe({
      next: comp => {
        this.compositionName = comp.description;
        this.compositionItems = (comp.items || []).map((i: any) => ({ ...i, _dirty: false }));
        this.showComposition = true;
      },
    });
  }

  /** Salvar alterações nos coeficientes da composição */
  saveCompositionChanges() {
    const items = this.compositionItems.map(i => ({ childCompositionId: i.itemType === 'COMPOSITION' ? i.id : null, materialId: i.itemType !== 'COMPOSITION' ? i.id : null, coefficient: i.coefficient, itemType: i.itemType }));
    this.http.put(`/compositions/${this.compositionId}`, { description: this.compositionName, items }).subscribe({
      next: () => { this.showComposition = false; this.messages.add({ severity: 'success', summary: 'Composição atualizada' }); this.tree.load(this.budgetId!); },
    });
  }

  /** Salvar como Própria — copia composição para banco próprio */
  saveAsOwn() {
    if (!this.selectedRow?.compositionId) { this.messages.add({ severity: 'warn', summary: 'Selecione uma composição' }); return; }
    this.http.get<any>(`/compositions/${this.selectedRow.compositionId}`).subscribe({
      next: comp => {
        const body = { sinapiCode: 'P-' + comp.sinapiCode, description: comp.description, unit: comp.unit, origin: 'PROPRIO', items: (comp.items || []).map((i: any) => ({ materialId: i.itemType !== 'COMPOSITION' ? i.id : null, childCompositionId: i.itemType === 'COMPOSITION' ? i.id : null, coefficient: i.coefficient })) };
        this.http.post('/compositions', body).subscribe({
          next: () => this.messages.add({ severity: 'success', summary: 'Composição salva no banco próprio', detail: body.sinapiCode }),
        });
      },
    });
  }

  /** Expandir Tudo (Analítico) — expande todas as composições */
  expandAll() {
    for (const row of this.tree.rows()) {
      if ((row.type === 'LEVEL' || row.type === 'COMPOSITION') && !row.expanded) {
        this.tree.toggle(row);
      }
    }
  }

  /** Colapsar Tudo (Sintético) — colapsa tudo */
  collapseAll() {
    for (const row of [...this.tree.rows()].reverse()) {
      if ((row.type === 'LEVEL' || row.type === 'COMPOSITION') && row.expanded) {
        this.tree.toggle(row);
      }
    }
  }

  /** Multiplicar Quantidades — aplica fator a todos os itens */
  applyMultiply() {
    if (!this.multiplyFactor || this.multiplyFactor === 1) return;
    for (const row of this.tree.rows()) {
      if ((row.type === 'COMPOSITION' || row.type === 'INPUT') && row.quantity) {
        row.quantity = row.quantity * this.multiplyFactor;
        row.total = (row.quantity || 0) * (row.unitCost || 0);
        row.dirty = true;
      }
    }
    this.tree.rows.set([...this.tree.rows()]);
    this.showMultiply = false;
    this.messages.add({ severity: 'success', summary: `Quantidades multiplicadas por ${this.multiplyFactor}` });
  }

  /** Aplicar Preço a Insumos Iguais — propaga preço do selecionado para todos com mesmo compositionId */
  applyPriceToEquals() {
    if (!this.selectedRow?.compositionId || !this.selectedRow.unitCost) { this.messages.add({ severity: 'warn', summary: 'Selecione um item com preço' }); return; }
    const targetId = this.selectedRow.compositionId;
    const price = this.selectedRow.unitCost;
    let count = 0;
    for (const row of this.tree.rows()) {
      if (row.compositionId === targetId && row !== this.selectedRow) {
        row.unitCost = price;
        row.total = (row.quantity || 0) * price;
        row.dirty = true;
        count++;
      }
    }
    this.tree.rows.set([...this.tree.rows()]);
    this.messages.add({ severity: 'success', summary: `Preço aplicado a ${count} item(ns) iguais` });
  }

  /** Carregar informações do orçamento */
  loadInfo() {
    this.http.get<any>(`/budgets/${this.budgetId}`).subscribe({
      next: b => { this.budgetInfo = b; this.showInfo = true; },
    });
  }

  rowClass(row: BudgetRow): Record<string, boolean> {
    return {
      'r-level': row.type === 'LEVEL',
      'r-sublevel': row.type === 'SUB_LEVEL',
      'r-comp': row.type === 'COMPOSITION',
      'r-input': row.type === 'INPUT',
      'r-sub': row.type === 'SUB_COMPOSITION',
      'r-empty': row.type === 'EMPTY',
      'r-dirty': row.dirty,
      'r-selected': row === this.selectedRow,
    };
  }
}
