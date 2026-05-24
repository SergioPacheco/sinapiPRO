import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DatePipe, DecimalPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [DatePipe, DecimalPipe, TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Notas Fiscais</h3>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm">
      <ng-template pTemplate="header"><tr><th style="width:80px">Nº</th><th>Descrição</th><th style="width:120px">Valor</th><th style="width:110px">Emissão</th><th style="width:110px">Vencimento</th><th style="width:100px">Status</th></tr></ng-template>
      <ng-template pTemplate="body" let-i>
        <tr><td>{{ i.number }}</td><td>{{ i.description }}</td><td class="currency">{{ i.amount | number:'1.2-2' }}</td><td>{{ i.issueDate | date:'dd/MM/yy' }}</td><td>{{ i.dueDate | date:'dd/MM/yy' }}</td><td><p-tag [value]="i.status" /></td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhuma nota fiscal</td></tr></ng-template>
    </p-table>
  `,
})
export class InvoiceListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/finance/receivables`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
