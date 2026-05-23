import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [RouterLink, TableModule, ButtonModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Clientes</h3>
      <p-button label="Novo Cliente" icon="pi pi-plus" size="small" routerLink="/registry/clients/new" />
    </div>
    <p-table [value]="items()" [paginator]="true" [rows]="25" [loading]="loading()" [totalRecords]="total()" [lazy]="true" (onLazyLoad)="load($event)" styleClass="p-datatable-sm p-datatable-striped">
      <ng-template pTemplate="header"><tr><th>Nome</th><th style="width:140px">Documento</th><th>Email</th><th style="width:130px">Telefone</th><th style="width:120px">Cidade</th><th style="width:60px">UF</th></tr></ng-template>
      <ng-template pTemplate="body" let-c>
        <tr><td>{{ c.name }}</td><td class="font-mono">{{ c.document }}</td><td>{{ c.email }}</td><td>{{ c.phone }}</td><td>{{ c.city }}</td><td>{{ c.state }}</td></tr>
      </ng-template>
    </p-table>
  `,
})
export class ClientListComponent implements OnInit {
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);
  total = signal(0);

  ngOnInit() { this.load({ first: 0, rows: 25 }); }

  load(event: any) {
    this.loading.set(true);
    const page = Math.floor((event.first || 0) / (event.rows || 25));
    this.http.get<any>(`/registry/clients?page=${page}&size=${event.rows || 25}`).subscribe({
      next: res => { this.items.set(res.content || []); this.total.set(res.totalElements || 0); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
