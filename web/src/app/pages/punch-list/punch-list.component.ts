import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { TagModule } from 'primeng/tag';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-punch-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, SelectModule, TagModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Punch List</h3>
      <p-button label="Novo Item" icon="pi pi-plus" size="small" (onClick)="showCreate = true" />
    </div>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
      <ng-template pTemplate="header"><tr><th>Descrição</th><th>Local</th><th>Responsável</th><th style="width:100px">Prioridade</th><th style="width:120px">Status</th><th style="width:80px"></th></tr></ng-template>
      <ng-template pTemplate="body" let-r>
        <tr>
          <td>{{ r.description }}</td><td>{{ r.location }}</td><td>{{ r.assignedTo }}</td>
          <td><p-tag [value]="r.priority" [severity]="prioritySev(r.priority)" /></td>
          <td><sp-status [status]="r.status" /></td>
          <td>@if (r.status !== 'CLOSED') { <p-button icon="pi pi-check" [text]="true" severity="success" title="Fechar" (onClick)="close(r)" /> }</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhum item</td></tr></ng-template>
    </p-table>

    <p-dialog header="Novo Item" [(visible)]="showCreate" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Descrição</label><input pInputText [(ngModel)]="form.description" class="w-full" /></div>
        <div><label>Local</label><input pInputText [(ngModel)]="form.location" class="w-full" /></div>
        <div><label>Responsável</label><input pInputText [(ngModel)]="form.assignedTo" class="w-full" /></div>
        <div><label>Prioridade</label><p-select [(ngModel)]="form.priority" [options]="priorities" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Criar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>
  `,
})
export class PunchListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);
  showCreate = false;
  form: any = { description: '', location: '', assignedTo: '', priority: 'MEDIUM' };
  priorities = ['HIGH', 'MEDIUM', 'LOW'];

  ngOnInit() { this.load(); }
  private get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  load() {
    this.http.get<any>(`/projects/${this.projectId}/punch-list`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() {
    this.http.post(`/projects/${this.projectId}/punch-list`, this.form).subscribe(() => {
      this.showCreate = false; this.form = { description: '', location: '', assignedTo: '', priority: 'MEDIUM' }; this.load();
    });
  }

  close(r: any) {
    this.http.patch(`/projects/${this.projectId}/punch-list/${r.id}/close`, {}).subscribe(() => this.load());
  }

  prioritySev(p: string) { return ({ HIGH: 'danger', MEDIUM: 'warn', LOW: 'info' } as any)[p] || 'secondary'; }
}
