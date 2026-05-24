import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FileUploadModule } from 'primeng/fileupload';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, FileUploadModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Documentos</h2>
      <p-button label="Upload" icon="pi pi-upload" size="small" (onClick)="showUpload = true" />
    </div>

    <p-table [value]="documents()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th>Nome</th>
          <th style="width:80px">Tipo</th>
          <th style="width:80px">Tamanho</th>
          <th style="width:100px">Data</th>
          <th style="width:80px">Versão</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-d>
        <tr>
          <td><i class="pi pi-file mr-2" style="color:var(--sp-primary)"></i>{{ d.name }}</td>
          <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ d.contentType }}</td>
          <td style="font-size:0.8rem">{{ formatSize(d.size) }}</td>
          <td style="font-size:0.8rem">{{ d.createdAt?.slice(0, 10) }}</td>
          <td class="text-center">v{{ d.version || 1 }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum documento</td></tr></ng-template>
    </p-table>

    <!-- Upload -->
    <p-dialog header="Upload de Documento" [(visible)]="showUpload" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Nome</label><input pInputText [(ngModel)]="uploadName" class="w-full" placeholder="Nome do documento" /></div>
        <div><label>Arquivo</label><input type="file" (change)="onFileSelect($event)" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showUpload = false" />
        <p-button label="Enviar" icon="pi pi-upload" (onClick)="upload()" [disabled]="!uploadFile" />
      </ng-template>
    </p-dialog>
  `,
})
export class DocumentListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  documents = signal<any[]>([]);
  loading = signal(true);
  showUpload = false;
  uploadName = '';
  uploadFile: File | null = null;

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/documents`).subscribe({ next: r => { this.documents.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  onFileSelect(event: any) { this.uploadFile = event.target.files[0]; if (!this.uploadName) this.uploadName = this.uploadFile?.name || ''; }

  upload() {
    if (!this.uploadFile) return;
    const fd = new FormData();
    fd.append('file', this.uploadFile);
    fd.append('name', this.uploadName);
    this.http.post(`/projects/${this.pid}/documents`, fd).subscribe({
      next: () => { this.showUpload = false; this.uploadFile = null; this.uploadName = ''; this.messages.add({ severity: 'success', summary: 'Documento enviado' }); this.ngOnInit(); },
    });
  }

  formatSize(bytes: number): string {
    if (!bytes) return '—';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  }
}
