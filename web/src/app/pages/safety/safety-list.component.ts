import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-safety-list',
  standalone: true,
  imports: [DatePipe, TableModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Inspeções de Segurança</h3>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm">
      <ng-template pTemplate="header"><tr><th style="width:110px">Data</th><th>Inspetor</th><th style="width:120px">Tipo</th><th style="width:100px">Status</th><th style="width:80px">Nota</th></tr></ng-template>
      <ng-template pTemplate="body" let-i>
        <tr><td>{{ i.date | date:'dd/MM/yy' }}</td><td>{{ i.inspector }}</td><td>{{ i.type }}</td><td><p-tag [value]="i.status" /></td><td>{{ i.score }}</td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhuma inspeção</td></tr></ng-template>
    </p-table>
  `,
})
export class SafetyListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any>(`/projects/${id}/safety-inspections`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
