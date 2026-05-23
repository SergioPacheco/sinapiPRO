import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Funcionários</h3>
    <p-table [value]="items()" [paginator]="true" [rows]="25" [loading]="loading()" styleClass="p-datatable-sm p-datatable-striped">
      <ng-template pTemplate="header"><tr><th style="width:100px">Código</th><th>Nome</th><th style="width:140px">Função</th><th style="width:120px">Tipo</th><th style="width:100px">Status</th></tr></ng-template>
      <ng-template pTemplate="body" let-e>
        <tr><td class="font-mono">{{ e.employeeCode }}</td><td>{{ e.name }}</td><td>{{ e.role }}</td><td>{{ e.type }}</td><td><p-tag [value]="e.status" [severity]="e.status === 'ACTIVE' ? 'success' : 'secondary'" /></td></tr>
      </ng-template>
    </p-table>
  `,
})
export class EmployeeListComponent implements OnInit {
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.http.get<any>('/registry/employees').subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
