import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-material-list',
  standalone: true,
  imports: [TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Insumos SINAPI</h3>
    <p-table [value]="materials()" [paginator]="true" [rows]="25" [loading]="loading()" [totalRecords]="total()" [lazy]="true" (onLazyLoad)="load($event)" styleClass="p-datatable-sm p-datatable-striped">
      <ng-template pTemplate="header"><tr><th style="width:120px">Código</th><th>Descrição</th><th style="width:80px">Unid.</th><th style="width:100px">Origem</th></tr></ng-template>
      <ng-template pTemplate="body" let-m>
        <tr><td class="font-mono">{{ m.sinapiCode }}</td><td>{{ m.description }}</td><td>{{ m.unit }}</td><td><p-tag [value]="m.origin" severity="info" /></td></tr>
      </ng-template>
    </p-table>
  `,
})
export class MaterialListComponent implements OnInit {
  private http = inject(HttpClient);
  materials = signal<any[]>([]);
  loading = signal(true);
  total = signal(0);

  ngOnInit() { this.load({ first: 0, rows: 25 }); }

  load(event: any) {
    this.loading.set(true);
    const page = Math.floor((event.first || 0) / (event.rows || 25));
    this.http.get<any>(`/materials?page=${page}&size=${event.rows || 25}`).subscribe({
      next: res => { this.materials.set(res.content || []); this.total.set(res.totalElements || 0); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
