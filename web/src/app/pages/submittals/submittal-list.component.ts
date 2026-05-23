import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-submittal-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, TextareaModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Submittals</h3>
      <p-button label="Novo Submittal" icon="pi pi-plus" size="small" (onClick)="showCreate = true" />
    </div>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
      <ng-template pTemplate="header"><tr><th style="width:70px">#</th><th>Título</th><th>Tipo</th><th style="width:120px">Status</th><th style="width:110px">Data</th><th style="width:120px"></th></tr></ng-template>
      <ng-template pTemplate="body" let-r>
        <tr>
          <td>{{ r.number }}</td><td>{{ r.title }}</td><td>{{ r.type }}</td>
          <td><sp-status [status]="r.status" /></td><td>{{ r.submittedDate }}</td>
          <td>
            @if (r.status === 'PENDING') {
              <p-button icon="pi pi-check" [text]="true" severity="success" title="Aprovar" (onClick)="action(r,'APPROVED')" />
              <p-button icon="pi pi-times" [text]="true" severity="danger" title="Rejeitar" (onClick)="action(r,'REJECTED')" />
            }
          </td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhum submittal</td></tr></ng-template>
    </p-table>

    <p-dialog header="Novo Submittal" [(visible)]="showCreate" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Número</label><input pInputText [(ngModel)]="form.number" class="w-full" /></div>
        <div><label>Título</label><input pInputText [(ngModel)]="form.title" class="w-full" /></div>
        <div><label>Tipo</label><input pInputText [(ngModel)]="form.type" class="w-full" /></div>
        <div><label>Descrição</label><textarea pTextarea [(ngModel)]="form.description" rows="3" class="w-full"></textarea></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Criar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>
  `,
})
export class SubmittalListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);
  showCreate = false;
  form: any = { number: '', title: '', type: '', description: '' };

  ngOnInit() { this.load(); }
  private get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  load() {
    this.http.get<any>(`/projects/${this.projectId}/submittals`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() {
    this.http.post(`/projects/${this.projectId}/submittals`, this.form).subscribe(() => {
      this.showCreate = false; this.form = { number: '', title: '', type: '', description: '' }; this.load();
    });
  }

  action(r: any, status: string) {
    this.http.patch(`/projects/${this.projectId}/submittals/${r.id}`, { status }).subscribe(() => this.load());
  }
}
