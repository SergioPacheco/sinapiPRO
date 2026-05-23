import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { StepsModule } from 'primeng/steps';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { TabViewModule } from 'primeng/tabview';
import { FileUploadModule } from 'primeng/fileupload';
import { TimelineModule } from 'primeng/timeline';
import { MessageService, MenuItem } from 'primeng/api';

@Component({
  selector: 'app-measurement-detail',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, TagModule, StepsModule, DialogModule, TextareaModule, InputTextModule, InputNumberModule, TabViewModule, FileUploadModule, TimelineModule],
  template: `
    @if (detail(); as d) {
      <div class="flex align-items-center justify-content-between mb-3">
        <h3 style="margin:0">Medição #{{ d.number }}</h3>
        <div class="flex gap-2">
          @if (d.status === 'DRAFT') {
            <p-button label="Serviço Extra" icon="pi pi-plus-circle" severity="info" size="small" (onClick)="showExtra = true" />
            <p-button label="Vincular Aditivo" icon="pi pi-link" severity="secondary" size="small" (onClick)="showChangeOrder = true" />
            <p-button label="Submeter" icon="pi pi-send" size="small" (onClick)="submit()" />
          }
          @if (d.status === 'SUBMITTED') {
            <p-button label="Aprovar" icon="pi pi-check" severity="success" size="small" (onClick)="approve()" />
            <p-button label="Rejeitar" icon="pi pi-times" severity="danger" size="small" (onClick)="showReject = true" />
          }
          <p-button icon="pi pi-history" severity="secondary" size="small" pTooltip="Histórico" (onClick)="loadHistory()" />
          <p-button icon="pi pi-paperclip" severity="secondary" size="small" pTooltip="Anexos" (onClick)="showAttachments = true" />
          <p-button icon="pi pi-file-pdf" severity="help" size="small" pTooltip="Boletim PDF" (onClick)="downloadPdf()" />
        </div>
      </div>
      <p-steps [model]="workflowSteps" [activeIndex]="activeStep()" [readonly]="true" styleClass="mb-4" />
      <div class="grid mb-3">
        <div class="col-6 md:col-3"><span class="text-muted">Período</span><div>{{ d.periodStart }} a {{ d.periodEnd }}</div></div>
        <div class="col-6 md:col-3"><span class="text-muted">Retenção</span><div>{{ d.retentionPct }}%</div></div>
        <div class="col-6 md:col-3"><span class="text-muted">Bruto</span><div class="currency">{{ d.grossAmount | number:'1.2-2' }}</div></div>
        <div class="col-6 md:col-3"><span class="text-muted">Líquido</span><div class="currency"><strong>{{ d.netAmount | number:'1.2-2' }}</strong></div></div>
      </div>
      <p-table [value]="d.items" styleClass="p-datatable-sm">
        <ng-template pTemplate="header"><tr><th>Descrição</th><th style="width:30px"></th><th class="text-right" style="width:100px">Qtd.</th><th class="text-right" style="width:120px">P.Unit.</th><th class="text-right" style="width:130px">Total</th></tr></ng-template>
        <ng-template pTemplate="body" let-i>
          <tr>
            <td>{{ i.description }} @if (i.extra) { <p-tag value="Extra" severity="warn" /> }</td>
            <td><i class="pi pi-calculator cursor-pointer text-primary" style="font-size:0.85rem" (click)="openMemo(i)"></i></td>
            <td class="text-right">{{ i.periodQuantity | number:'1.2-4' }}</td>
            <td class="text-right">{{ i.unitPrice | number:'1.2-2' }}</td>
            <td class="text-right"><strong>{{ i.periodAmount | number:'1.2-2' }}</strong></td>
          </tr>
        </ng-template>
      </p-table>
    }

    <!-- Reject Dialog -->
    <p-dialog header="Rejeitar Medição" [(visible)]="showReject" [style]="{width:'400px'}" [modal]="true">
      <textarea pTextarea [(ngModel)]="rejectReason" rows="4" class="w-full" placeholder="Motivo da rejeição"></textarea>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showReject = false" />
        <p-button label="Confirmar" severity="danger" (onClick)="reject()" [disabled]="!rejectReason" />
      </ng-template>
    </p-dialog>

    <!-- Extra Service Dialog -->
    <p-dialog header="Serviço Extra (não orçado)" [(visible)]="showExtra" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Descrição</label><input pInputText [(ngModel)]="extraItem.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-6"><label>Quantidade</label><p-inputNumber [(ngModel)]="extraItem.quantity" [maxFractionDigits]="4" styleClass="w-full" /></div>
          <div class="col-6"><label>Preço Unitário</label><p-inputNumber [(ngModel)]="extraItem.unitPrice" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showExtra = false" />
        <p-button label="Adicionar" icon="pi pi-check" (onClick)="addExtraItem()" [disabled]="!extraItem.description || !extraItem.quantity" />
      </ng-template>
    </p-dialog>

    <!-- Change Order Dialog -->
    <p-dialog header="Vincular Aditivo" [(visible)]="showChangeOrder" [style]="{width:'400px'}" [modal]="true">
      <div><label>ID do Aditivo (Change Order)</label><input pInputText [(ngModel)]="changeOrderId" class="w-full" placeholder="UUID do aditivo" /></div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showChangeOrder = false" />
        <p-button label="Vincular" icon="pi pi-link" (onClick)="linkChangeOrder()" [disabled]="!changeOrderId" />
      </ng-template>
    </p-dialog>

    <!-- History Dialog -->
    <p-dialog header="Histórico de Aprovações" [(visible)]="showHistory" [style]="{width:'550px'}" [modal]="true">
      <p-timeline [value]="history()" align="left">
        <ng-template pTemplate="content" let-h>
          <div><strong>{{ h.action }}</strong> <span class="text-muted text-sm">{{ h.fromStatus }} → {{ h.toStatus }}</span></div>
          @if (h.reason) { <p class="text-sm text-muted mt-1">{{ h.reason }}</p> }
          <small class="text-muted">{{ h.performedBy || 'Sistema' }} — {{ h.createdAt }}</small>
        </ng-template>
      </p-timeline>
      @if (history().length === 0) { <p class="text-center text-muted">Nenhum registro</p> }
    </p-dialog>

    <!-- Attachments Dialog -->
    <p-dialog header="Anexos / Fotos" [(visible)]="showAttachments" [style]="{width:'600px'}" [modal]="true">
      <p-fileUpload mode="basic" [auto]="true" chooseLabel="Upload" [url]="attachmentUrl()" (onUpload)="onUpload()" accept="image/*,.pdf,.doc,.docx" [maxFileSize]="10000000" />
      <p-table [value]="attachments()" styleClass="p-datatable-sm mt-3">
        <ng-template pTemplate="header"><tr><th>Arquivo</th><th style="width:120px">Tipo</th><th style="width:100px">Tamanho</th></tr></ng-template>
        <ng-template pTemplate="body" let-a><tr><td><a [href]="a.url" target="_blank">{{ a.fileName }}</a></td><td>{{ a.contentType }}</td><td>{{ a.size }}</td></tr></ng-template>
        <ng-template pTemplate="emptymessage"><tr><td colspan="3" class="text-center text-muted p-3">Nenhum anexo</td></tr></ng-template>
      </p-table>
    </p-dialog>

    <!-- Memo Dialog -->
    <p-dialog header="Memória de Cálculo" [(visible)]="showMemo" [style]="{width:'600px'}" [modal]="true">
      @if (memoItem) {
        <p class="text-muted mb-2">{{ memoItem.description }}</p>
        <table class="w-full" style="border-collapse:collapse">
          <thead><tr style="border-bottom:1px solid var(--surface-border)"><th class="text-left p-2">Descrição</th><th class="text-left p-2" style="width:140px">Fórmula</th><th class="text-right p-2" style="width:90px">Resultado</th><th style="width:30px"></th></tr></thead>
          <tbody>
            @for (line of memoLines; track $index) {
              <tr>
                <td class="p-1"><input pInputText [(ngModel)]="line.description" class="w-full" /></td>
                <td class="p-1"><input pInputText [(ngModel)]="line.formula" class="w-full font-mono" (blur)="evalFormula(line)" /></td>
                <td class="p-1 text-right font-mono">{{ line.value | number:'1.2-4' }}</td>
                <td class="p-1"><i class="pi pi-trash cursor-pointer text-red-400" (click)="memoLines.splice($index,1)"></i></td>
              </tr>
            }
          </tbody>
          <tfoot><tr style="border-top:2px solid var(--surface-border)"><td colspan="2" class="p-2"><strong>TOTAL</strong></td><td class="p-2 text-right font-mono"><strong>{{ memoTotal() | number:'1.2-4' }}</strong></td><td></td></tr></tfoot>
        </table>
        <p-button label="+ Linha" icon="pi pi-plus" size="small" [text]="true" (onClick)="memoLines.push({description:'',formula:'',value:0})" styleClass="mt-2" />
      }
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showMemo = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveMemo()" />
      </ng-template>
    </p-dialog>
  `,
})
export class MeasurementDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  detail = signal<any>(null);
  history = signal<any[]>([]);
  attachments = signal<any[]>([]);
  activeStep = signal(0);
  workflowSteps: MenuItem[] = [{ label: 'Rascunho' }, { label: 'Submetida' }, { label: 'Aprovada' }, { label: 'Paga' }];

  showReject = false; rejectReason = '';
  showExtra = false; extraItem: any = { description: '', quantity: 1, unitPrice: 0 };
  showChangeOrder = false; changeOrderId = '';
  showHistory = false;
  showAttachments = false;
  showMemo = false; memoItem: any = null; memoLines: any[] = [];

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }
  private get mid() { return this.route.snapshot.paramMap.get('mid'); }

  ngOnInit() { this.load(); }

  load() {
    this.http.get<any>(`/projects/${this.pid}/measurements/${this.mid}/detail`).subscribe(d => {
      this.detail.set(d);
      this.activeStep.set(({ DRAFT: 0, SUBMITTED: 1, APPROVED: 2, PAID: 3 } as any)[d.status] || 0);
    });
  }

  submit() { this.action('submit'); }
  approve() { this.action('approve'); }
  reject() {
    this.http.post(`/projects/${this.pid}/measurements/${this.mid}/reject`, { reason: this.rejectReason }).subscribe(() => {
      this.messages.add({ severity: 'info', summary: 'Medição rejeitada' }); this.showReject = false; this.load();
    });
  }

  addExtraItem() {
    this.http.post(`/projects/${this.pid}/measurements/${this.mid}/extra-items`, this.extraItem).subscribe(() => {
      this.showExtra = false; this.extraItem = { description: '', quantity: 1, unitPrice: 0 };
      this.messages.add({ severity: 'success', summary: 'Serviço extra adicionado' }); this.load();
    });
  }

  linkChangeOrder() {
    this.http.put(`/projects/${this.pid}/measurements/${this.mid}/change-order`, { changeOrderId: this.changeOrderId }).subscribe(() => {
      this.showChangeOrder = false; this.messages.add({ severity: 'success', summary: 'Aditivo vinculado' }); this.load();
    });
  }

  loadHistory() {
    this.http.get<any[]>(`/projects/${this.pid}/measurements/${this.mid}/history`).subscribe(h => { this.history.set(h); this.showHistory = true; });
  }

  attachmentUrl() { return `/api/v1/projects/${this.pid}/documents?entityType=MEASUREMENT&entityId=${this.mid}`; }
  onUpload() { this.loadAttachments(); this.messages.add({ severity: 'success', summary: 'Arquivo enviado' }); }
  loadAttachments() { /* placeholder — uses document API */ }

  // Memo
  openMemo(item: any) {
    this.memoItem = item;
    this.http.get<any>(`/projects/${this.pid}/measurements/${this.mid}/items/${item.id}/memo`).subscribe({
      next: m => { this.memoLines = m.lines || []; this.showMemo = true; },
      error: () => { this.memoLines = [{ description: '', formula: '', value: 0 }]; this.showMemo = true; },
    });
  }
  evalFormula(line: any) { try { line.value = Function('"use strict"; return (' + line.formula.replace(/,/g, '.').replace(/×/g, '*') + ')')(); } catch { } }
  memoTotal(): number { return this.memoLines.reduce((s: number, l: any) => s + (l.value || 0), 0); }
  saveMemo() {
    const body = { lines: this.memoLines.filter((l: any) => l.description || l.formula), result: this.memoTotal() };
    this.http.put(`/projects/${this.pid}/measurements/${this.mid}/items/${this.memoItem.id}/memo`, body).subscribe(() => {
      this.showMemo = false; this.messages.add({ severity: 'success', summary: 'Memória salva' });
    });
  }

  downloadPdf() { window.open(`/api/v1/projects/${this.pid}/measurements/${this.mid}/reports/bulletin.pdf`, '_blank'); }

  private action(name: string) {
    this.http.post(`/projects/${this.pid}/measurements/${this.mid}/${name}`, {}).subscribe(() => {
      this.messages.add({ severity: 'success', summary: `Medição ${name === 'submit' ? 'submetida' : 'aprovada'}` }); this.load();
    });
  }
}
