import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { FileUploadModule } from 'primeng/fileupload';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageService, LazyLoadEvent } from 'primeng/api';
import { StatusTagComponent, EmptyStateComponent } from '../../shared/components';

@Component({
  selector: 'app-sinapi',
  standalone: true,
  imports: [FormsModule, DecimalPipe, TableModule, ButtonModule, TagModule, InputTextModule, DropdownModule, DialogModule, FileUploadModule, CheckboxModule, StatusTagComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0">Base de Composições e Insumos</h2>
      <div class="flex gap-2">
        <p-button label="Importar SINAPI" icon="pi pi-upload" severity="secondary" (onClick)="importVisible = true" />
        <p-button label="Nova Composição" icon="pi pi-plus" (onClick)="openCreate()" />
      </div>
    </div>

    <div class="sinapi-layout">
      <!-- Painel esquerdo: lista -->
      <div class="list-panel">
        <div class="tabs-row">
          <button [class.active]="activeTab === 'compositions'" (click)="activeTab = 'compositions'; search()">Composições</button>
          <button [class.active]="activeTab === 'materials'" (click)="activeTab = 'materials'; search()">Insumos</button>
        </div>

        <div class="flex gap-2 mb-2 mt-2">
          <input pInputText [(ngModel)]="searchText" placeholder="Buscar código ou descrição..." (keyup.enter)="search()" class="flex-1" />
          <p-dropdown [(ngModel)]="filterOrigin" [options]="originOpts" placeholder="Origem" [showClear]="true" (onChange)="search()" styleClass="w-8rem" />
        </div>

        @if (activeTab === 'compositions') {
          <p-dropdown [(ngModel)]="filterGroup" [options]="groupOpts()" placeholder="Grupo/Classe" [showClear]="true" [filter]="true" (onChange)="search()" styleClass="w-full mb-2" />
        }

        <p-table [value]="compositions()" [lazy]="true" (onLazyLoad)="onLazy($event)" [paginator]="true" [rows]="20" [totalRecords]="totalRecords()" [loading]="loading()" styleClass="p-datatable-sm" [rowHover]="true" selectionMode="single" [(selection)]="selectedItem" (onRowSelect)="onSelect($event)">
          <ng-template pTemplate="header">
            <tr><th style="width:100px">Código</th><th>Descrição</th><th style="width:60px">Un</th><th style="width:90px">Custo</th></tr>
          </ng-template>
          <ng-template pTemplate="body" let-c>
            <tr [pSelectableRow]="c">
              <td class="font-mono text-xs">{{ c.sinapiCode }}</td>
              <td class="text-sm">{{ c.description }}</td>
              <td class="text-xs">{{ c.unit }}</td>
              <td class="text-xs font-mono text-right">{{ c.unitCost ? (c.unitCost | number:'1.2-2') : '—' }}</td>
            </tr>
          </ng-template>
        </p-table>
      </div>

      <!-- Painel direito: detalhe analítico -->
      <div class="detail-panel">
        @if (selectedItem) {
          <div class="detail-header">
            <span class="code">{{ selectedItem.sinapiCode }}</span>
            <p-tag [value]="selectedItem.origin || 'SINAPI'" [severity]="selectedItem.origin === 'PROPRIO' ? 'success' : 'info'" />
          </div>
          <h4 class="detail-title">{{ selectedItem.description }}</h4>
          <div class="detail-meta">
            <span>Unidade: <strong>{{ selectedItem.unit }}</strong></span>
            <span>Grupo: <strong>{{ selectedItem.groupName || '—' }}</strong></span>
          </div>

          @if (detailItems().length) {
            <div class="detail-section">
              <h5>Composição Analítica</h5>
              @for (group of itemGroups; track group.type) {
                @if (detailItemsByType(group.type).length) {
                  <div class="item-group">
                    <span class="group-label">{{ group.label }}</span>
                    <table class="items-table">
                      <thead><tr><th>Insumo</th><th>Un</th><th>Coef.</th><th>Preço</th><th>Total</th></tr></thead>
                      <tbody>
                        @for (item of detailItemsByType(group.type); track item.id) {
                          <tr>
                            <td class="text-sm">{{ item.description }}</td>
                            <td class="text-xs">{{ item.unit }}</td>
                            <td class="text-xs font-mono text-right">{{ item.coefficient | number:'1.4-4' }}</td>
                            <td class="text-xs font-mono text-right">{{ item.latestPrice | number:'1.2-2' }}</td>
                            <td class="text-xs font-mono text-right font-bold">{{ (item.coefficient * (item.latestPrice || 0)) | number:'1.2-2' }}</td>
                          </tr>
                        }
                      </tbody>
                      <tfoot><tr><td colspan="4" class="text-right"><strong>Subtotal {{ group.label }}:</strong></td><td class="font-mono text-right font-bold">{{ subtotalByType(group.type) | number:'1.2-2' }}</td></tr></tfoot>
                    </table>
                  </div>
                }
              }
              <div class="total-row"><span>CUSTO UNITÁRIO TOTAL:</span><strong>R$ {{ totalUnitCost() | number:'1.2-2' }}</strong></div>
            </div>
          } @else {
            <div class="empty-detail">
              <p>Composição sintética — sem detalhamento analítico carregado.</p>
              <p class="text-muted text-sm">Importe o arquivo analítico do SINAPI para ver os insumos e coeficientes.</p>
              @if (selectedItem.unitCost) {
                <div class="total-row"><span>Custo Sintético:</span><strong>R$ {{ selectedItem.unitCost | number:'1.2-2' }}</strong></div>
              }
            </div>
          }

          <div class="detail-actions mt-3">
            <p-button label="Ver Completo" icon="pi pi-external-link" size="small" [text]="true" (onClick)="goDetail(selectedItem.id)" />
            @if (selectedItem.origin !== 'SINAPI') {
              <p-button label="Editar" icon="pi pi-pencil" size="small" severity="secondary" (onClick)="goDetail(selectedItem.id)" />
            }
            <p-button label="Copiar para Próprias" icon="pi pi-copy" size="small" [text]="true" (onClick)="copyComposition(selectedItem.id)" />
          </div>
        } @else {
          <div class="empty-detail center">
            <i class="pi pi-search" style="font-size:2rem;color:var(--sp-text-muted)"></i>
            <p>Selecione uma composição ou insumo para ver o detalhamento.</p>
          </div>
        }
      </div>
    </div>

    <!-- Import Dialog -->
    <p-dialog header="Importar Planilha SINAPI" [(visible)]="importVisible" [style]="{width:'500px'}" [modal]="true">
      <p class="text-muted mb-3">Selecione o arquivo .xlsx ou o .zip completo baixado do site da Caixa. O sistema detecta automaticamente o tipo, UF, mês e se é desonerado.</p>
      <div class="flex flex-column gap-3">
        <div>
          <label class="mb-2 block font-semibold">Arquivo (.zip ou .xlsx)</label>
          <input type="file" accept=".xlsx,.xls,.zip" (change)="onFileSelect($event)" class="w-full" />
        </div>
        @if (importDetected) {
          <div class="detected-info">
            <div class="flex justify-content-between"><span>Tipo:</span><strong>{{ importType === 'materials' ? 'Insumos (Preços)' : 'Composições' }}</strong></div>
            <div class="flex justify-content-between"><span>UF:</span><strong>{{ importState }}</strong></div>
            <div class="flex justify-content-between"><span>Referência:</span><strong>{{ importMonth }}</strong></div>
            <div class="flex justify-content-between"><span>Desonerado:</span><strong>{{ importDesonerated ? 'Sim' : 'Não' }}</strong></div>
          </div>
        }
        @if (importFile && !importDetected) {
          <p class="text-orange-500">⚠️ Não foi possível detectar os parâmetros pelo nome do arquivo. Preencha manualmente:</p>
          <div class="grid">
            <div class="col-4"><label>UF</label><p-dropdown [(ngModel)]="importState" [options]="stateOpts" styleClass="w-full" /></div>
            <div class="col-4"><label>Mês Ref.</label><input pInputText [(ngModel)]="importMonth" class="w-full" placeholder="2024-12-01" /></div>
            <div class="col-4 flex align-items-end"><input type="checkbox" [(ngModel)]="importDesonerated" id="deson" /><label for="deson" class="ml-2">Desonerado</label></div>
          </div>
        }
      </div>
      <ng-template pTemplate="footer">
        @if (importing()) {
          <div class="w-full mb-2">
            <div class="progress-bar">
              <div class="progress-fill" [style.width.%]="importProgress()"></div>
            </div>
            <small class="text-muted">{{ importProgress() < 100 ? 'Enviando arquivo... ' + importProgress() + '%' : 'Processando no servidor...' }}</small>
          </div>
        }
        <p-button label="Cancelar" severity="secondary" (onClick)="importVisible = false" [disabled]="importing()" />
        <p-button label="Importar" icon="pi pi-upload" (onClick)="doImport()" [loading]="importing()" [disabled]="!importFile || (!isZipFile() && (!importState || !importMonth))" />
      </ng-template>
    </p-dialog>

    <!-- Create Dialog -->
    <p-dialog header="Nova Composição" [(visible)]="createVisible" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Código</label><input pInputText [(ngModel)]="newComp.sinapiCode" class="w-full" /></div>
        <div><label>Descrição</label><input pInputText [(ngModel)]="newComp.description" class="w-full" /></div>
        <div><label>Unidade</label><input pInputText [(ngModel)]="newComp.unit" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="createVisible = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="create()" [loading]="creating()" />
      </ng-template>
    </p-dialog>
  `,
  styles: [`
    .sinapi-layout { display: grid; grid-template-columns: 1fr 400px; gap: 1rem; height: calc(100vh - 180px); }
    .list-panel { overflow-y: auto; }
    .detail-panel { background: var(--sp-surface-card, #16213e); border: 1px solid var(--sp-border, #2a2a4a); border-radius: 10px; padding: 1.25rem; overflow-y: auto; }
    .tabs-row { display: flex; border-bottom: 1px solid var(--sp-border, #2a2a4a);
      button { padding: 0.5rem 1.25rem; background: none; border: none; color: var(--sp-text-muted); cursor: pointer; border-bottom: 2px solid transparent;
        &.active { color: var(--sp-primary, #4fc3f7); border-bottom-color: var(--sp-primary, #4fc3f7); } } }
    .detail-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.5rem; .code { font-family: monospace; font-size: 1.1rem; color: var(--sp-primary); } }
    .detail-title { margin: 0 0 0.75rem; font-size: 0.9rem; color: var(--sp-text); line-height: 1.4; }
    .detail-meta { display: flex; gap: 1.5rem; font-size: 0.8rem; color: var(--sp-text-muted); margin-bottom: 1rem; }
    .detail-section h5 { margin: 0 0 0.5rem; font-size: 0.8rem; color: var(--sp-text-muted); text-transform: uppercase; }
    .item-group { margin-bottom: 1rem; }
    .group-label { font-size: 0.7rem; font-weight: 700; color: var(--sp-text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
    .items-table { width: 100%; border-collapse: collapse; margin-top: 0.25rem; font-size: 0.75rem;
      th { text-align: left; padding: 0.3rem 0.4rem; color: var(--sp-text-muted); border-bottom: 1px solid var(--sp-border); }
      td { padding: 0.3rem 0.4rem; border-bottom: 1px solid color-mix(in srgb, var(--sp-border) 50%, transparent); }
      tfoot td { padding-top: 0.5rem; border: none; } }
    .total-row { display: flex; justify-content: space-between; padding: 0.75rem; background: color-mix(in srgb, var(--sp-primary) 10%, transparent); border-radius: 6px; margin-top: 0.75rem; font-size: 0.9rem; }
    .detail-actions { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .empty-detail { padding: 2rem 1rem; text-align: center; color: var(--sp-text-muted); &.center { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; gap: 0.75rem; } }
    .progress-bar { height: 6px; background: var(--sp-surface-hover, #2a2a4a); border-radius: 3px; overflow: hidden; }
    .progress-fill { height: 100%; background: var(--sp-primary, #4fc3f7); transition: width 0.3s ease; border-radius: 3px; }
    .detected-info { background: var(--sp-surface-hover, #1a2744); padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; }
  `]
})
export class SinapiComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private messages = inject(MessageService);

  compositions = signal<any[]>([]);
  totalRecords = signal(0);
  loading = signal(true);
  creating = signal(false);
  activeTab = 'compositions';
  searchText = '';
  filterOrigin: string | null = null;
  filterUnit: string | null = null;
  filterGroup: string | null = null;
  selectedItem: any = null;
  detailItems = signal<any[]>([]);
  groupOpts = signal<any[]>([]);
  itemGroups = [
    { type: 'MATERIAL', label: 'Materiais' },
    { type: 'LABOR', label: 'Mão de Obra' },
    { type: 'EQUIPMENT', label: 'Equipamentos' },
    { type: 'COMPOSITION', label: 'Composições Auxiliares' },
  ];
  importVisible = false;
  importType = 'materials';
  importState = '';
  importMonth = '';
  importDesonerated = false;
  importFile: File | null = null;
  importing = signal(false);
  importProgress = signal(0);
  importTypes = [{ label: 'Insumos (Preços)', value: 'materials' }, { label: 'Composições (Analítico)', value: 'compositions' }];
  stateOpts = ['AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT','PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'].map(s => ({ label: s, value: s }));
  createVisible = false;
  newComp: any = {};

  originOpts = [{ label: 'SINAPI', value: 'SINAPI' }, { label: 'PRÓPRIO', value: 'PROPRIO' }];
  unitOpts = ['M', 'M2', 'M3', 'KG', 'UN', 'H', 'VB', 'L'].map(u => ({ label: u, value: u }));

  ngOnInit() { this.search(); }

  search() { this.loadPage(0, 25); }

  onLazy(event: any) { this.loadPage(Math.floor((event.first || 0) / (event.rows || 25)), event.rows || 25); }

  loadPage(page: number, size: number) {
    this.loading.set(true);
    let url = `/compositions?page=${page}&size=${size}`;
    if (this.searchText) url += `&q=${encodeURIComponent(this.searchText)}`;
    if (this.filterOrigin) url += `&origin=${this.filterOrigin}`;
    if (this.filterUnit) url += `&unit=${this.filterUnit}`;
    this.http.get<any>(url).subscribe({
      next: res => { this.compositions.set(res.content || res); this.totalRecords.set(res.totalElements || (res.content || res).length); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  goDetail(id: string) { this.router.navigate(['/sinapi', id]); }

  onSelect(event: any) {
    this.selectedItem = event.data;
    // Load analytic detail
    this.http.get<any>(`/compositions/${this.selectedItem.id}`).subscribe({
      next: res => this.detailItems.set(res.items || []),
      error: () => this.detailItems.set([])
    });
  }

  detailItemsByType(type: string) { return this.detailItems().filter((i: any) => i.type === type); }

  subtotalByType(type: string): number {
    return this.detailItemsByType(type).reduce((sum: number, i: any) => sum + (i.coefficient || 0) * (i.latestPrice || 0), 0);
  }

  totalUnitCost(): number {
    return this.detailItems().reduce((sum: number, i: any) => sum + (i.coefficient || 0) * (i.latestPrice || 0), 0);
  }

  copyComposition(id: string) {
    this.http.post<any>(`/compositions/${id}/copy`, {}).subscribe({
      next: () => this.messages.add({ severity: 'success', summary: 'Composição copiada para próprias' }),
      error: () => this.messages.add({ severity: 'error', summary: 'Erro ao copiar' })
    });
  }

  openCreate() { this.newComp = { sinapiCode: '', description: '', unit: 'UN', origin: 'PROPRIO' }; this.createVisible = true; }

  create() {
    this.creating.set(true);
    this.http.post('/compositions', this.newComp).subscribe({
      next: () => { this.createVisible = false; this.creating.set(false); this.messages.add({ severity: 'success', summary: 'Composição criada' }); this.search(); },
      error: () => this.creating.set(false),
    });
  }

  importDetected = false;

  isZipFile() { return this.importFile?.name?.endsWith('.zip'); }

  onFileSelect(event: any) {
    this.importFile = event.target.files[0];
    this.importDetected = false;
    if (!this.importFile) return;

    // Detectar parâmetros pelo nome do arquivo
    // Formato: SINAPI_Preco_Ref_Insumos_SP_202412_NaoDesonerado.xlsx
    //          SINAPI_Custo_Ref_Composicoes_Analitico_SP_202412_NaoDesonerado.xlsx
    const name = this.importFile.name;

    // Tipo
    if (name.includes('Insumos') || name.includes('Preco_Ref')) {
      this.importType = 'materials';
    } else if (name.includes('Composicoes') || name.includes('Custo_Ref')) {
      this.importType = 'compositions';
    }

    // UF (2 letras maiúsculas antes do AAAAMM)
    const ufMatch = name.match(/_([A-Z]{2})_(\d{6})/);
    if (ufMatch) {
      this.importState = ufMatch[1];
      const yyyymm = ufMatch[2];
      this.importMonth = `${yyyymm.substring(0, 4)}-${yyyymm.substring(4, 6)}-01`;
    }

    // Desonerado
    this.importDesonerated = name.includes('Desonerado') && !name.includes('NaoDesonerado');

    this.importDetected = !!(this.importState && this.importMonth);
  }

  doImport() {
    if (!this.importFile) return;
    this.importing.set(true);
    const formData = new FormData();
    formData.append('file', this.importFile);

    const isZip = this.importFile.name.endsWith('.zip');
    let endpoint: string;

    if (isZip) {
      // ZIP: endpoint auto-detecta tudo
      endpoint = '/compositions/import/zip';
    } else {
      // XLSX individual: precisa dos parâmetros
      if (!this.importState || !this.importMonth) { this.importing.set(false); return; }
      formData.append('state', this.importState);
      formData.append('referenceMonth', this.importMonth);
      formData.append('desonerated', String(this.importDesonerated));
      endpoint = this.importType === 'materials' ? '/compositions/import/materials' : '/compositions/import/compositions';
    }

    this.http.post<any>(endpoint, formData, { reportProgress: true, observe: 'events' }).subscribe({
      next: event => {
        if (event.type === 1 && event.total) { // UploadProgress
          this.importProgress.set(Math.round(100 * event.loaded / event.total));
        }
        if (event.type === 4) { // Response
          const res = event.body;
          this.importing.set(false); this.importProgress.set(0); this.importVisible = false; this.importFile = null;
          if (isZip) {
            const m = res.materials || {}; const c = res.compositions || {};
            this.messages.add({ severity: 'success', summary: 'Importação ZIP concluída',
              detail: `${res.state} ${res.referenceMonth} | Insumos: ${m.created || 0} novos, ${m.updated || 0} atualizados | Composições: ${c.created || 0} novas`, life: 10000 });
          } else {
            this.messages.add({ severity: 'success', summary: 'Importação concluída', detail: `${res.created || 0} novos, ${res.updated || 0} atualizados`, life: 8000 });
          }
          this.search();
        }
      },
      error: (err) => { this.importing.set(false); this.importProgress.set(0); this.messages.add({ severity: 'error', summary: 'Erro na importação', detail: err?.error?.detail || 'Erro desconhecido', life: 8000 }); },
    });
  }
}
