import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
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
  imports: [FormsModule, TableModule, ButtonModule, TagModule, InputTextModule, DropdownModule, DialogModule, FileUploadModule, CheckboxModule, StatusTagComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0">Composições</h2>
      <div class="flex gap-2">
        <p-button label="Importar SINAPI" icon="pi pi-upload" severity="secondary" (onClick)="importVisible = true" />
        <p-button label="Nova Composição" icon="pi pi-plus" (onClick)="openCreate()" />
      </div>
    </div>

    <div class="flex gap-2 mb-3">
      <input pInputText [(ngModel)]="searchText" placeholder="Buscar..." (keyup.enter)="search()" style="width:250px" />
      <p-dropdown [(ngModel)]="filterOrigin" [options]="originOpts" placeholder="Origem" [showClear]="true" (onChange)="search()" />
      <p-dropdown [(ngModel)]="filterUnit" [options]="unitOpts" placeholder="Unidade" [showClear]="true" (onChange)="search()" />
    </div>

    <p-table [value]="compositions()" [lazy]="true" (onLazyLoad)="onLazy($event)" [paginator]="true" [rows]="25" [totalRecords]="totalRecords()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-striped" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr><th style="width:120px">Código</th><th>Descrição</th><th style="width:80px">Unidade</th><th style="width:100px">Origem</th></tr>
      </ng-template>
      <ng-template pTemplate="body" let-c>
        <tr style="cursor:pointer" (click)="goDetail(c.id)">
          <td class="font-mono">{{ c.sinapiCode }}</td>
          <td>{{ c.description }}</td>
          <td>{{ c.unit }}</td>
          <td><p-tag [value]="c.origin" [severity]="c.origin === 'SINAPI' ? 'info' : 'success'" /></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhuma composição encontrada</td></tr></ng-template>
    </p-table>

    <!-- Import Dialog -->
    <p-dialog header="Importar Planilha SINAPI" [(visible)]="importVisible" [style]="{width:'550px'}" [modal]="true">
      <p class="text-muted mb-3">Importe a planilha Excel (.xlsx) baixada do site da Caixa Econômica Federal (SINAPI).</p>
      <div class="flex flex-column gap-3">
        <div><label>Tipo de Importação</label><p-dropdown [(ngModel)]="importType" [options]="importTypes" styleClass="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>UF</label><p-dropdown [(ngModel)]="importState" [options]="stateOpts" styleClass="w-full" placeholder="Estado" /></div>
          <div class="col-6"><label>Mês de Referência</label><input pInputText [(ngModel)]="importMonth" class="w-full" placeholder="2025-01-01" /></div>
        </div>
        <div class="flex align-items-center gap-2">
          <input type="checkbox" [(ngModel)]="importDesonerated" id="deson" />
          <label for="deson">Desonerado</label>
        </div>
        <div>
          <label class="mb-2 block">Arquivo Excel (.xlsx)</label>
          <input type="file" accept=".xlsx,.xls" (change)="onFileSelect($event)" class="w-full" />
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="importVisible = false" />
        <p-button label="Importar" icon="pi pi-upload" (onClick)="doImport()" [loading]="importing()" [disabled]="!importFile || !importState || !importMonth" />
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
})
export class SinapiComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private messages = inject(MessageService);

  compositions = signal<any[]>([]);
  totalRecords = signal(0);
  loading = signal(true);
  creating = signal(false);
  searchText = '';
  filterOrigin: string | null = null;
  filterUnit: string | null = null;
  importVisible = false;
  importType = 'materials';
  importState = '';
  importMonth = '';
  importDesonerated = false;
  importFile: File | null = null;
  importing = signal(false);
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
    if (this.searchText) url += `&search=${encodeURIComponent(this.searchText)}`;
    if (this.filterOrigin) url += `&origin=${this.filterOrigin}`;
    if (this.filterUnit) url += `&unit=${this.filterUnit}`;
    this.http.get<any>(url).subscribe({
      next: res => { this.compositions.set(res.content || res); this.totalRecords.set(res.totalElements || (res.content || res).length); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  goDetail(id: string) { this.router.navigate(['/sinapi', id]); }

  openCreate() { this.newComp = { sinapiCode: '', description: '', unit: 'UN', origin: 'PROPRIO' }; this.createVisible = true; }

  create() {
    this.creating.set(true);
    this.http.post('/compositions', this.newComp).subscribe({
      next: () => { this.createVisible = false; this.creating.set(false); this.messages.add({ severity: 'success', summary: 'Composição criada' }); this.search(); },
      error: () => this.creating.set(false),
    });
  }

  onFileSelect(event: any) { this.importFile = event.target.files[0]; }

  doImport() {
    if (!this.importFile || !this.importState || !this.importMonth) return;
    this.importing.set(true);
    const formData = new FormData();
    formData.append('file', this.importFile);
    formData.append('state', this.importState);
    formData.append('referenceMonth', this.importMonth);
    formData.append('desonerated', String(this.importDesonerated));

    const endpoint = this.importType === 'materials' ? '/compositions/import/materials' : '/compositions/import/compositions';
    this.http.post<any>(endpoint, formData).subscribe({
      next: res => {
        this.importing.set(false); this.importVisible = false; this.importFile = null;
        this.messages.add({ severity: 'success', summary: 'Importação concluída', detail: `${res.imported || res.created || 0} registros importados, ${res.skipped || 0} ignorados`, life: 8000 });
        this.search();
      },
      error: () => this.importing.set(false),
    });
  }
}
