import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [DecimalPipe, TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Estoque</h3>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm">
      <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:100px">Qtd.</th><th style="width:80px">Unid.</th><th style="width:100px">Estoque Mín.</th></tr></ng-template>
      <ng-template pTemplate="body" let-i>
        <tr>
          <td>{{ i.description }}</td>
          <td [class.text-red-500]="i.quantity <= i.minStock">{{ i.quantity | number:'1.2-2' }}</td>
          <td>{{ i.unit }}</td>
          <td>{{ i.minStock | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Estoque vazio</td></tr></ng-template>
    </p-table>
  `,
})
export class InventoryListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/stock`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
