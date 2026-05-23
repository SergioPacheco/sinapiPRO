import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [DecimalPipe, TableModule, ButtonModule, FileUploadModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Documentos</h3>
      <p-fileUpload mode="basic" chooseLabel="Upload" [auto]="true" (onUpload)="onUpload()" [url]="uploadUrl" name="file" accept="*/*" [maxFileSize]="50000000" />
    </div>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
      <ng-template pTemplate="header"><tr><th>Arquivo</th><th>Tipo Entidade</th><th style="width:100px">Tamanho</th><th style="width:110px">Upload</th><th style="width:70px">Versão</th></tr></ng-template>
      <ng-template pTemplate="body" let-d>
        <tr>
          <td><i class="pi pi-file mr-2"></i>{{ d.fileName }}</td>
          <td>{{ d.entityType }}</td>
          <td>{{ d.fileSize | number:'1.0-0' }} KB</td>
          <td>{{ d.uploadedAt }}</td>
          <td>v{{ d.version }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhum documento</td></tr></ng-template>
    </p-table>
  `,
})
export class DocumentListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);

  get projectId() { return this.route.parent?.snapshot.paramMap.get('id'); }
  get uploadUrl() { return `/api/v1/projects/${this.projectId}/documents`; }

  ngOnInit() { this.load(); }

  load() {
    this.http.get<any>(`/projects/${this.projectId}/documents`).subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  onUpload() { this.load(); }
}
