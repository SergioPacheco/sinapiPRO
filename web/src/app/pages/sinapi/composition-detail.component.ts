import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-composition-detail',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, TagModule, ButtonModule, DialogModule, InputTextModule, AutoCompleteModule],
  template: `
    @if (comp(); as c) {
      <div class="flex align-items-center justify-content-between mb-3">
        <div>
          <h3 style="margin:0">{{ c.sinapiCode }} — {{ c.description }}</h3>
          <span class="text-muted">Unidade: {{ c.unit }} | Origem: {{ c.origin }}</span>
          @if (totalCost() > 0) { <span class="ml-3"><strong>Custo Total: {{ totalCost() | number:'1.2-2' }}</strong></span> }
        </div>
        <div class="flex gap-2">
          @if (c.origin === 'PROPRIO') { <p-button label="Editar" icon="pi pi-pencil" severity="secondary" (onClick)="openEdit()" /> }
          <p-button label="Copiar para Próprias" icon="pi pi-copy" (onClick)="copy()" [loading]="copying()" />
        </div>
      </div>
      @for (group of groups; track group.type) {
        @if (itemsByType(group.type).length) {
          <h4>{{ group.label }}</h4>
          <p-table [value]="itemsByType(group.type)" styleClass="p-datatable-sm">
            <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:80px">Unid.</th><th style="width:120px">Coeficiente</th><th style="width:120px">Preço</th></tr></ng-template>
            <ng-template pTemplate="body" let-item><tr><td>{{ item.description }}</td><td>{{ item.unit }}</td><td>{{ item.coefficient | number:'1.4-4' }}</td><td>{{ item.latestPrice ? (item.latestPrice | number:'1.2-2') : '—' }}</td></tr></ng-template>
          </p-table>
        }
      }
    }

    <!-- Edit Dialog -->
    <p-dialog header="Editar Composição" [(visible)]="editVisible" [style]="{width:'600px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Descrição</label><input pInputText [(ngModel)]="editForm.description" class="w-full" /></div>
        <div><label>Unidade</label><input pInputText [(ngModel)]="editForm.unit" class="w-full" /></div>
        <h5>Itens</h5>
        @for (item of editForm.items; track $index) {
          <div class="grid mb-1">
            <div class="col-6"><p-autoComplete [(ngModel)]="item.description" [suggestions]="itemSuggestions()" (completeMethod)="searchItems($event)" field="description" styleClass="w-full" /></div>
            <div class="col-3"><input pInputText [(ngModel)]="item.coefficient" placeholder="Coef." class="w-full" /></div>
            <div class="col-2"><input pInputText [(ngModel)]="item.unit" placeholder="Un." class="w-full" /></div>
            <div class="col-1"><p-button icon="pi pi-trash" severity="danger" [text]="true" (onClick)="editForm.items.splice($index,1)" /></div>
          </div>
        }
        <p-button label="Adicionar Item" icon="pi pi-plus" size="small" [text]="true" (onClick)="editForm.items.push({description:'',coefficient:1,unit:'UN'})" />
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="editVisible = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveEdit()" [loading]="savingEdit()" />
      </ng-template>
    </p-dialog>
  `,
})
export class CompositionDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  comp = signal<any>(null);
  copying = signal(false);
  editVisible = false;
  editForm: any = {};
  savingEdit = signal(false);
  itemSuggestions = signal<any[]>([]);

  groups = [
    { type: 'MATERIAL', label: 'Materiais' },
    { type: 'LABOR', label: 'Mão de Obra' },
    { type: 'EQUIPMENT', label: 'Equipamentos' },
    { type: 'COMPOSITION', label: 'Composições Auxiliares' },
  ];

  totalCost = computed(() => {
    const items = this.comp()?.items || [];
    return items.reduce((sum: number, i: any) => sum + ((i.coefficient || 0) * (i.latestPrice || 0)), 0);
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get(`/compositions/${id}`).subscribe(res => this.comp.set(res));
  }

  itemsByType(type: string) { return (this.comp()?.items || []).filter((i: any) => i.type === type); }

  copy() {
    this.copying.set(true);
    const id = this.route.snapshot.paramMap.get('id');
    this.http.post<any>(`/compositions/${id}/copy`, {}).subscribe({
      next: res => { this.copying.set(false); this.messages.add({ severity: 'success', summary: 'Composição copiada' }); this.router.navigate(['/sinapi', res.id || id]); },
      error: () => this.copying.set(false),
    });
  }

  openEdit() {
    const c = this.comp();
    this.editForm = { description: c.description, unit: c.unit, items: (c.items || []).map((i: any) => ({ ...i })) };
    this.editVisible = true;
  }

  searchItems(event: any) {
    this.http.get<any[]>(`/compositions/items/search?q=${encodeURIComponent(event.query)}`).subscribe(res => this.itemSuggestions.set(res));
  }

  saveEdit() {
    this.savingEdit.set(true);
    const id = this.route.snapshot.paramMap.get('id');
    this.http.put(`/compositions/${id}`, this.editForm).subscribe({
      next: () => { this.editVisible = false; this.savingEdit.set(false); this.messages.add({ severity: 'success', summary: 'Composição atualizada' }); this.ngOnInit(); },
      error: () => this.savingEdit.set(false),
    });
  }
}
