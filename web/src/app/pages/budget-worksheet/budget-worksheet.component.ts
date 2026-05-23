import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TreeTableModule } from 'primeng/treetable';
import { TreeNode } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { TabViewModule } from 'primeng/tabview';
import { MessageService } from 'primeng/api';
import { MenuModule } from 'primeng/menu';
import { InlineCreateDrawerComponent } from '../../shared/components';

@Component({
  selector: 'app-budget-worksheet',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TreeTableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, AutoCompleteModule, DropdownModule, CalendarModule, TabViewModule, MenuModule, InlineCreateDrawerComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <div>
        <h3 style="margin:0">Planilha Orçamentária</h3>
        <span class="text-muted">Custo direto: {{ summary().directCost | number:'1.2-2' }} | BDI: {{ summary().bdiPct | number:'1.2-2' }}% | Total: {{ summary().total | number:'1.2-2' }}</span>
      </div>
      <div class="flex gap-2">
        <p-button label="Adicionar Item" icon="pi pi-plus" size="small" (onClick)="addItemVisible = true" />
        <p-button label="BDI" icon="pi pi-percentage" severity="secondary" size="small" (onClick)="openBdi()" />
        <p-button label="Atualizar Data Base" icon="pi pi-refresh" severity="warn" size="small" (onClick)="showUpdateBase = true" />
        <p-button icon="pi pi-file-pdf" severity="help" size="small" pTooltip="Relatório Analítico" (onClick)="downloadAnalyticalPdf()" />
        <p-button icon="pi pi-ellipsis-v" severity="secondary" size="small" (onClick)="menu.toggle($event)" />
        <p-menu #menu [popup]="true" [model]="moreActions" />
      </div>
    </div>

    <p-treetable [value]="nodes()" [loading]="loading()" styleClass="p-treetable-sm" [scrollable]="true" scrollHeight="calc(100vh - 280px)">
      <ng-template pTemplate="header">
        <tr><th style="width:45%">Descrição</th><th style="width:80px">Unid.</th><th style="width:100px" class="text-right">Qtd.</th><th style="width:30px"></th><th style="width:120px" class="text-right">Custo Unit.</th><th style="width:130px" class="text-right">Total</th></tr>
      </ng-template>
      <ng-template pTemplate="body" let-rowNode let-rowData="rowData">
        <tr [ttRow]="rowNode">
          <td><p-treeTableToggler [rowNode]="rowNode" />
            @if (rowData.isStage) { <strong>{{ rowData.name }}</strong> } @else { <span class="font-mono text-muted" style="margin-right:0.5rem">{{ rowData.code }}</span>{{ rowData.description }} }
          </td>
          <td>{{ rowData.unit || '' }}</td>
          <td class="text-right">{{ rowData.quantity ? (rowData.quantity | number:'1.2-4') : '' }}</td>
          <td>@if (!rowData.isStage) { <i class="pi pi-calculator cursor-pointer text-primary" style="font-size:0.85rem" (click)="openMemo(rowData)"></i> }</td>
          <td class="text-right">{{ rowData.unitCost ? (rowData.unitCost | number:'1.2-2') : '' }}</td>
          <td class="text-right"><strong>{{ rowData.totalCost | number:'1.2-2' }}</strong></td>
        </tr>
      </ng-template>
    </p-treetable>

    <!-- Add Item Dialog -->
    <p-dialog header="Adicionar Item" [(visible)]="addItemVisible" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Composição</label><p-autoComplete [(ngModel)]="selectedItem" [suggestions]="itemSuggestions()" (completeMethod)="searchItems($event)" field="description" styleClass="w-full" placeholder="Buscar composição..." /></div>
        <div><label>Quantidade</label><p-inputNumber [(ngModel)]="addQty" [maxFractionDigits]="4" styleClass="w-full" /></div>
        @if (selectedItem && !selectedItem.id) {
          <a class="cursor-pointer text-primary" (click)="openDrawer()"><i class="pi pi-plus mr-1"></i>Criar Composição Própria</a>
        }
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="addItemVisible = false" />
        <p-button label="Adicionar" icon="pi pi-check" (onClick)="addItem()" [disabled]="!selectedItem?.id" />
      </ng-template>
    </p-dialog>

    <!-- BDI Dialog with Tabs by Type -->
    <p-dialog header="Configuração de BDI" [(visible)]="showBdi" [style]="{width:'550px'}" [modal]="true">
      <p-tabView>
        @for (tab of bdiTabs; track tab.type) {
          <p-tabPanel [header]="tab.label">
            <div class="flex flex-column gap-3">
              <div class="flex justify-content-between align-items-center"><span>Administração (%)</span><p-inputNumber [(ngModel)]="tab.data.administration" [maxFractionDigits]="4" size="small" styleClass="w-8rem" /></div>
              <div class="flex justify-content-between align-items-center"><span>Lucro (%)</span><p-inputNumber [(ngModel)]="tab.data.profit" [maxFractionDigits]="4" size="small" styleClass="w-8rem" /></div>
              <div class="flex justify-content-between align-items-center"><span>Impostos (%)</span><p-inputNumber [(ngModel)]="tab.data.taxes" [maxFractionDigits]="4" size="small" styleClass="w-8rem" /></div>
              <div class="flex justify-content-between align-items-center"><span>Encargos Sociais (%)</span><p-inputNumber [(ngModel)]="tab.data.socialCharges" [maxFractionDigits]="4" size="small" styleClass="w-8rem" /></div>
              <div class="flex justify-content-between align-items-center"><span>Despesas Financeiras (%)</span><p-inputNumber [(ngModel)]="tab.data.financialExpenses" [maxFractionDigits]="4" size="small" styleClass="w-8rem" /></div>
              <div class="flex justify-content-between align-items-center"><span>Riscos (%)</span><p-inputNumber [(ngModel)]="tab.data.risks" [maxFractionDigits]="4" size="small" styleClass="w-8rem" /></div>
              <div class="flex justify-content-between border-top-1 surface-border pt-2"><strong>Total BDI</strong><strong>{{ bdiTotal(tab.data) | number:'1.2-4' }}%</strong></div>
            </div>
          </p-tabPanel>
        }
      </p-tabView>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showBdi = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveBdi()" [loading]="savingBdi()" />
      </ng-template>
    </p-dialog>

    <!-- Update Base Date Dialog -->
    <p-dialog header="Atualizar Data Base" [(visible)]="showUpdateBase" [style]="{width:'400px'}" [modal]="true">
      <p class="text-muted mb-3">Recalcula todos os preços com base na tabela SINAPI para o mês/UF selecionados.</p>
      <div class="flex flex-column gap-3">
        <div><label>Mês de Referência</label><p-calendar [(ngModel)]="baseDate" view="month" dateFormat="mm/yy" styleClass="w-full" /></div>
        <div><label>UF</label><p-dropdown [(ngModel)]="baseState" [options]="states" styleClass="w-full" placeholder="Selecione o estado" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showUpdateBase = false" />
        <p-button label="Atualizar Preços" icon="pi pi-refresh" severity="warn" (onClick)="updateBaseDate()" [loading]="updatingBase()" />
      </ng-template>
    </p-dialog>

    <!-- Memo (Memória de Cálculo) Dialog -->
    <p-dialog header="Memória de Cálculo" [(visible)]="showMemo" [style]="{width:'650px'}" [modal]="true">
      @if (memoItem) {
        <p class="text-muted mb-2">{{ memoItem.description }} ({{ memoItem.unit }})</p>
        <table class="w-full" style="border-collapse:collapse">
          <thead><tr style="border-bottom:1px solid var(--surface-border)"><th class="text-left p-2">Descrição</th><th class="text-left p-2" style="width:150px">Fórmula</th><th class="text-right p-2" style="width:100px">Resultado</th><th style="width:40px"></th></tr></thead>
          <tbody>
            @for (line of memoLines; track $index) {
              <tr>
                <td class="p-1"><input pInputText [(ngModel)]="line.description" class="w-full" /></td>
                <td class="p-1"><input pInputText [(ngModel)]="line.formula" class="w-full font-mono" (blur)="evalFormula(line)" /></td>
                <td class="p-1 text-right font-mono">{{ line.value | number:'1.2-4' }}</td>
                <td class="p-1"><i class="pi pi-trash cursor-pointer text-red-400" (click)="memoLines.splice($index,1)"></i></td>
              </tr>
            }
          </tbody>
          <tfoot><tr style="border-top:2px solid var(--surface-border)"><td colspan="2" class="p-2"><strong>TOTAL</strong></td><td class="p-2 text-right font-mono"><strong>{{ memoTotal() | number:'1.2-4' }}</strong></td><td></td></tr></tfoot>
        </table>
        <p-button label="+ Linha" icon="pi pi-plus" size="small" [text]="true" (onClick)="memoLines.push({description:'',formula:'',value:0})" styleClass="mt-2" />
      }
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showMemo = false" />
        <p-button label="Salvar e aplicar quantidade" icon="pi pi-check" (onClick)="saveMemo()" [loading]="savingMemo()" />
      </ng-template>
    </p-dialog>

    <!-- Create Composition Drawer -->
    <sp-drawer #drawer header="Nova Composição Própria" (save)="saveNewComp()">
      <div class="flex flex-column gap-3">
        <div><label>Código</label><input pInputText [(ngModel)]="newComp.sinapiCode" class="w-full" placeholder="P-001" /></div>
        <div><label>Descrição</label><input pInputText [(ngModel)]="newComp.description" class="w-full" /></div>
        <div><label>Unidade</label><input pInputText [(ngModel)]="newComp.unit" class="w-full" /></div>
        <h5>Itens</h5>
        @for (item of newComp.items; track $index) {
          <div class="grid mb-1">
            <div class="col-5"><p-autoComplete [(ngModel)]="item.description" [suggestions]="matSuggestions()" (completeMethod)="searchMaterials($event)" field="description" styleClass="w-full" /></div>
            <div class="col-3"><p-inputNumber [(ngModel)]="item.coefficient" [maxFractionDigits]="4" styleClass="w-full" placeholder="Coef." /></div>
            <div class="col-3"><input pInputText [(ngModel)]="item.unit" class="w-full" placeholder="Un." /></div>
            <div class="col-1"><p-button icon="pi pi-trash" severity="danger" [text]="true" (onClick)="newComp.items.splice($index,1)" /></div>
          </div>
        }
        <p-button label="Adicionar Item" icon="pi pi-plus" size="small" [text]="true" (onClick)="newComp.items.push({description:'',coefficient:1,unit:'UN'})" />
      </div>
    </sp-drawer>
  `,
})
export class BudgetWorksheetComponent implements OnInit {
  @ViewChild('drawer') drawer!: InlineCreateDrawerComponent;
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  nodes = signal<TreeNode[]>([]);
  summary = signal<any>({ directCost: 0, bdiPct: 0, total: 0 });
  loading = signal(true);

  // Add Item
  addItemVisible = false;
  selectedItem: any = null;
  addQty = 1;
  itemSuggestions = signal<any[]>([]);
  matSuggestions = signal<any[]>([]);
  newComp: any = { sinapiCode: 'P-', description: '', unit: 'UN', items: [] };

  // BDI
  showBdi = false;
  savingBdi = signal(false);
  bdiTabs = [
    { type: 'ALL', label: 'Geral', data: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 } },
    { type: 'MATERIAL', label: 'Material', data: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 } },
    { type: 'LABOR', label: 'Mão de Obra', data: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 } },
    { type: 'EQUIPMENT', label: 'Equipamento', data: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 } },
    { type: 'SERVICE', label: 'Serviço', data: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 } },
  ];

  // Update Base Date
  showUpdateBase = false;
  updatingBase = signal(false);
  baseDate: Date | null = null;
  baseState = '';
  states = ['AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT','PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'].map(s => ({ label: s, value: s }));

  // Memo
  showMemo = false;
  savingMemo = signal(false);
  memoItem: any = null;
  memoLines: any[] = [];

  private get budgetId() { return this.route.snapshot.paramMap.get('budgetId'); }

  moreActions = [
    { label: 'Duplicar Itens/Etapas', icon: 'pi pi-copy', command: () => this.duplicateItems() },
    { label: 'Importar de Outro Orçamento', icon: 'pi pi-download', command: () => this.showImportDialog = true },
    { label: 'Comparar Orçamentos', icon: 'pi pi-arrows-h', command: () => this.showCompareDialog = true },
    { label: 'Substituir Item', icon: 'pi pi-sync', command: () => this.showSubstituteDialog = true },
    { separator: true },
    { label: 'Curva ABC Materiais', icon: 'pi pi-chart-bar', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/abc-services.pdf`, '_blank') },
    { label: 'Planilha Sintética PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/worksheet.pdf`, '_blank') },
    { separator: true },
    { label: 'Propostas para Pregão', icon: 'pi pi-briefcase', command: () => this.loadProposals() },
    { label: 'Tags', icon: 'pi pi-tag', command: () => this.showTags = true },
    { label: 'Encargos Sociais', icon: 'pi pi-users', command: () => this.loadSocialCharges() },
  ];

  // Sprint 3 dialogs
  showImportDialog = false;
  showCompareDialog = false;
  showSubstituteDialog = false;
  importBudgetId = '';
  compareBudgetId = '';
  substituteItemId = ''; substituteNewCompId = '';

  // Sprint 6 dialogs
  showProposals = false; proposals = signal<any[]>([]);
  showTags = false;
  showSocialCharges = false; socialCharges = signal<any[]>([]);
  newProposal = { description: '', discountPct: 0 };

  ngOnInit() {
    this.http.get<any>(`/budgets/${this.budgetId}/worksheet`).subscribe({
      next: ws => { this.summary.set({ directCost: ws.directCost, bdiPct: ws.bdiPct * 100, total: ws.total }); this.nodes.set(ws.stages.map((s: any) => this.stageToNode(s))); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  // --- Add Item ---
  searchItems(event: any) {
    this.http.get<any[]>(`/compositions/items/search?q=${encodeURIComponent(event.query)}`).subscribe(res => this.itemSuggestions.set(res));
  }
  searchMaterials(event: any) {
    this.http.get<any[]>(`/compositions/items/search?q=${encodeURIComponent(event.query)}`).subscribe(res => this.matSuggestions.set(res));
  }
  addItem() {
    this.http.post(`/budgets/${this.budgetId}/worksheet/items`, { compositionId: this.selectedItem.id, quantity: this.addQty }).subscribe({
      next: () => { this.addItemVisible = false; this.messages.add({ severity: 'success', summary: 'Item adicionado' }); this.ngOnInit(); },
    });
  }
  openDrawer() { this.newComp = { sinapiCode: 'P-', description: '', unit: 'UN', items: [] }; this.drawer.open(); }
  saveNewComp() {
    this.http.post<any>('/compositions', { ...this.newComp, origin: 'PROPRIO' }).subscribe({
      next: res => { this.drawer.close(); this.selectedItem = res; this.messages.add({ severity: 'success', summary: 'Composição criada' }); },
    });
  }

  // --- BDI ---
  openBdi() {
    for (const tab of this.bdiTabs) {
      this.http.get<any>(`/budgets/${this.budgetId}/bdi?itemType=${tab.type}`).subscribe(b => {
        tab.data = { administration: b.administration || 0, profit: b.profit || 0, taxes: b.taxes || 0, socialCharges: b.socialCharges || 0, financialExpenses: b.financialExpenses || 0, risks: b.risks || 0 };
      });
    }
    this.showBdi = true;
  }
  bdiTotal(d: any): number { return (d.administration || 0) + (d.profit || 0) + (d.taxes || 0) + (d.socialCharges || 0) + (d.financialExpenses || 0) + (d.risks || 0); }
  saveBdi() {
    this.savingBdi.set(true);
    const batch = this.bdiTabs.map(t => ({ itemType: t.type, ...t.data }));
    this.http.put(`/budgets/${this.budgetId}/bdi/batch`, batch).subscribe({
      next: () => { this.showBdi = false; this.savingBdi.set(false); this.messages.add({ severity: 'success', summary: 'BDI salvo' }); this.ngOnInit(); },
      error: () => this.savingBdi.set(false),
    });
  }

  // --- Update Base Date ---
  updateBaseDate() {
    if (!this.baseDate || !this.baseState) return;
    this.updatingBase.set(true);
    const referenceDate = this.baseDate.toISOString().slice(0, 10);
    this.http.post<any>(`/budgets/${this.budgetId}/update-base-date`, { referenceDate, state: this.baseState }).subscribe({
      next: res => {
        this.updatingBase.set(false); this.showUpdateBase = false;
        this.messages.add({ severity: 'success', summary: `Preços atualizados`, detail: `${res.updatedPrices} atualizados, ${res.divergentPrices} divergentes` });
        this.ngOnInit();
      },
      error: () => this.updatingBase.set(false),
    });
  }

  // --- Memo (Memória de Cálculo) ---
  openMemo(item: any) {
    this.memoItem = item;
    this.memoLines = [];
    this.http.get<any>(`/budgets/${this.budgetId}/items/${item.id}/memo`).subscribe({
      next: memo => { this.memoLines = memo.lines || []; this.showMemo = true; },
      error: () => { this.memoLines = [{ description: '', formula: '', value: 0 }]; this.showMemo = true; },
    });
  }
  evalFormula(line: any) {
    try { line.value = Function('"use strict"; return (' + line.formula.replace(/,/g, '.').replace(/×/g, '*') + ')')(); } catch { /* keep current */ }
  }
  memoTotal(): number { return this.memoLines.reduce((sum: number, l: any) => sum + (l.value || 0), 0); }
  saveMemo() {
    this.savingMemo.set(true);
    const body = { lines: this.memoLines.filter((l: any) => l.description || l.formula), result: this.memoTotal() };
    this.http.put(`/budgets/${this.budgetId}/items/${this.memoItem.id}/memo`, body).subscribe({
      next: () => { this.savingMemo.set(false); this.showMemo = false; this.messages.add({ severity: 'success', summary: 'Memória salva' }); this.ngOnInit(); },
      error: () => this.savingMemo.set(false),
    });
  }

  // --- PDF Report ---
  downloadAnalyticalPdf() {
    window.open(`/api/v1/budgets/${this.budgetId}/reports/analytical.pdf`, '_blank');
  }

  // --- Sprint 3: Duplicate ---
  duplicateItems() {
    this.http.post(`/budgets/${this.budgetId}/duplicate`, {}).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Itens duplicados' }); this.ngOnInit(); },
    });
  }

  // --- Sprint 3: Import from another budget ---
  importFromBudget() {
    if (!this.importBudgetId) return;
    this.http.post(`/budgets/${this.budgetId}/import-items`, { sourceBudgetId: this.importBudgetId }).subscribe({
      next: () => { this.showImportDialog = false; this.messages.add({ severity: 'success', summary: 'Itens importados' }); this.ngOnInit(); },
    });
  }

  // --- Sprint 3: Compare budgets ---
  compareBudgets() {
    if (!this.compareBudgetId) return;
    window.open(`/api/v1/budgets/${this.budgetId}/compare/${this.compareBudgetId}`, '_blank');
  }

  // --- Sprint 3: Substitute item ---
  substituteItem() {
    if (!this.substituteItemId || !this.substituteNewCompId) return;
    this.http.put(`/budgets/${this.budgetId}/items/${this.substituteItemId}/substitute`, { newCompositionId: this.substituteNewCompId }).subscribe({
      next: () => { this.showSubstituteDialog = false; this.messages.add({ severity: 'success', summary: 'Item substituído' }); this.ngOnInit(); },
    });
  }

  // --- Sprint 6: Proposals ---
  loadProposals() {
    this.http.get<any[]>(`/budgets/${this.budgetId}/proposals`).subscribe(p => { this.proposals.set(p); this.showProposals = true; });
  }
  createProposal() {
    this.http.post(`/budgets/${this.budgetId}/proposals`, this.newProposal).subscribe(() => {
      this.messages.add({ severity: 'success', summary: 'Proposta criada' }); this.loadProposals();
    });
  }

  // --- Sprint 6: Social Charges ---
  loadSocialCharges() {
    this.http.get<any[]>(`/budgets/${this.budgetId}/social-charges`).subscribe(s => { this.socialCharges.set(s); this.showSocialCharges = true; });
  }

  // --- Helpers ---
  private stageToNode(stage: any): TreeNode {
    return {
      data: { isStage: true, name: stage.name, totalCost: stage.subtotal },
      children: [
        ...stage.items.map((i: any) => ({ data: { isStage: false, id: i.id, code: i.code, description: i.description, unit: i.unit, quantity: i.quantity, unitCost: i.unitCost, totalCost: i.totalCost } })),
        ...(stage.children || []).map((c: any) => this.stageToNode(c)),
      ],
      expanded: true,
    };
  }
}
