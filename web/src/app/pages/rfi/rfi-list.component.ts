import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { TagModule } from 'primeng/tag';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-rfi-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, TextareaModule, TagModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">RFI — Solicitações de Informação</h3>
      <p-button label="Nova RFI" icon="pi pi-plus" size="small" (onClick)="showCreate = true" />
    </div>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
      <ng-template pTemplate="header"><tr><th style="width:70px">#</th><th>Assunto</th><th>Responsável</th><th style="width:120px">Status</th><th style="width:110px">Data</th><th style="width:80px"></th></tr></ng-template>
      <ng-template pTemplate="body" let-r>
        <tr>
          <td>{{ r.number }}</td><td>{{ r.subject }}</td><td>{{ r.assignedTo }}</td>
          <td><sp-status [status]="r.status" /></td><td>{{ r.createdAt }}</td>
          <td>@if (r.status === 'OPEN') { <p-button icon="pi pi-reply" [text]="true" title="Responder" (onClick)="openAnswer(r)" /> }</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhuma RFI</td></tr></ng-template>
    </p-table>

    <p-dialog header="Nova RFI" [(visible)]="showCreate" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Assunto</label><input pInputText [(ngModel)]="form.subject" class="w-full" /></div>
        <div><label>Pergunta</label><textarea pTextarea [(ngModel)]="form.question" rows="4" class="w-full"></textarea></div>
        <div><label>Responsável</label><input pInputText [(ngModel)]="form.assignedTo" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Criar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>

    <p-dialog header="Responder RFI" [(visible)]="showAnswer" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Resposta</label><textarea pTextarea [(ngModel)]="answerText" rows="4" class="w-full"></textarea></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Enviar Resposta" icon="pi pi-check" (onClick)="submitAnswer()" /></ng-template>
    </p-dialog>
  `,
})
export class RfiListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);
  showCreate = false;
  showAnswer = false;
  form: any = { subject: '', question: '', assignedTo: '' };
  answerText = '';
  private selectedRfi: any = null;

  ngOnInit() { this.load(); }

  private get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }

  load() {
    this.http.get<any>(`/projects/${this.projectId}/rfis`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() {
    this.http.post(`/projects/${this.projectId}/rfis`, this.form).subscribe(() => {
      this.showCreate = false; this.form = { subject: '', question: '', assignedTo: '' }; this.load();
    });
  }

  openAnswer(r: any) { this.selectedRfi = r; this.answerText = ''; this.showAnswer = true; }

  submitAnswer() {
    this.http.post(`/projects/${this.projectId}/rfis/${this.selectedRfi.id}/answer`, { answer: this.answerText }).subscribe(() => {
      this.showAnswer = false; this.load();
    });
  }
}
