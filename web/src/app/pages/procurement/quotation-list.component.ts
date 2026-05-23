import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-quotation-list',
  standalone: true,
  imports: [DatePipe, TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Cotações</h3>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm">
      <ng-template pTemplate="header"><tr><th style="width:80px">Nº</th><th>Descrição</th><th style="width:110px">Status</th><th style="width:100px">Fornecedores</th><th style="width:120px">Criado em</th></tr></ng-template>
      <ng-template pTemplate="body" let-q>
        <tr><td>{{ q.number }}</td><td>{{ q.description }}</td><td><p-tag [value]="q.status" /></td><td>{{ q.supplierCount }}</td><td>{{ q.createdAt | date:'dd/MM/yy' }}</td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhuma cotação</td></tr></ng-template>
    </p-table>
  `,
})
export class QuotationListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/procurement/quotations`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
