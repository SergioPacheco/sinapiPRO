import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [RouterLink, FormsModule, TableModule, ButtonModule, InputTextModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Clientes</h3>
      <div class="flex gap-2">
        <span class="p-input-icon-left"><i class="pi pi-search"></i><input pInputText [(ngModel)]="filterValue" (input)="dt.filterGlobal(filterValue, 'contains')" placeholder="Buscar..." style="width:220px" /></span>
        <p-button label="Novo" icon="pi pi-plus" size="small" routerLink="/registry/clients/new" />
      </div>
    </div>
    <p-table #dt [value]="items()" [paginator]="true" [rows]="20" [loading]="loading()" styleClass="p-datatable-sm" [rowHover]="true" [globalFilterFields]="['name','document','email','city']">
      <ng-template pTemplate="header"><tr><th pSortableColumn="name">Nome <p-sortIcon field="name" /></th><th style="width:140px">Documento</th><th>Email</th><th style="width:120px">Telefone</th><th style="width:130px">Cidade</th><th style="width:50px"></th></tr></ng-template>
      <ng-template pTemplate="body" let-c>
        <tr style="cursor:pointer" (click)="edit(c)">
          <td class="font-semibold">{{ c.name }}</td><td class="font-mono">{{ c.document }}</td><td>{{ c.email }}</td><td>{{ c.phone }}</td><td>{{ c.city }}/{{ c.state }}</td>
          <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="edit(c); $event.stopPropagation()" /></td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-4">Nenhum cliente encontrado</td></tr></ng-template>
    </p-table>
  `,
})
export class ClientListComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  items = signal<any[]>([]);
  loading = signal(true);
  total = signal(0);
  search = '';
  filterValue = '';

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.http.get<any>(`/registry/clients?page=0&size=500`).subscribe({
      next: res => { this.items.set(res.content || []); this.total.set(res.totalElements || 0); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  doSearch() { this.load(); }

  edit(c: any) { this.router.navigate(['/registry/clients', c.id]); }
}
