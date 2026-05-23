import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Fornecedores</h3>
    <p-table [value]="items()" [paginator]="true" [rows]="25" [loading]="loading()" styleClass="p-datatable-sm p-datatable-striped">
      <ng-template pTemplate="header"><tr><th>Nome</th><th style="width:140px">Documento</th><th>Email</th><th style="width:130px">Telefone</th><th style="width:120px">Cidade</th><th style="width:80px">Ativo</th></tr></ng-template>
      <ng-template pTemplate="body" let-s>
        <tr><td>{{ s.name }}</td><td class="font-mono">{{ s.document }}</td><td>{{ s.email }}</td><td>{{ s.phone }}</td><td>{{ s.city }}</td><td><p-tag [value]="s.active ? 'Sim' : 'Não'" [severity]="s.active ? 'success' : 'secondary'" /></td></tr>
      </ng-template>
    </p-table>
  `,
})
export class SupplierListComponent implements OnInit {
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.http.get<any>('/suppliers').subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
