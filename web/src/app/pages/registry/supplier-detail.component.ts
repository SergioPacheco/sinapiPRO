import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DatePickerModule } from 'primeng/datepicker';
import { TabViewModule } from 'primeng/tabview';
import { CheckboxModule } from 'primeng/checkbox';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-supplier-detail',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DatePickerModule, TabViewModule, CheckboxModule, TagModule],
  template: `
    @if (supplier(); as s) {
      <h3 style="margin:0 0 1rem">{{ s.name }}</h3>
      <p class="text-muted">{{ s.document }} | {{ s.email }}</p>
      <p-tabView>
          <p-tabPanel header="Documentos">
            <div class="flex justify-content-end mb-2"><p-button label="Novo" icon="pi pi-plus" size="small" (onClick)="dialog = 'doc'" /></div>
            <p-table [value]="docs()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Tipo</th><th>Número</th><th>Emissão</th><th>Validade</th></tr></ng-template>
              <ng-template pTemplate="body" let-r><tr><td>{{ r.documentType }}</td><td>{{ r.number }}</td><td>{{ r.issueDate }}</td><td>{{ r.expiryDate }}</td></tr></ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhum</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
          <p-tabPanel header="Avaliações">
            <div class="flex align-items-center justify-content-between mb-2">
              <span class="text-muted">Média: <strong>{{ avgScore() | number:'1.1-1' }}</strong></span>
              <p-button label="Nova" icon="pi pi-plus" size="small" (onClick)="dialog = 'eval'" />
            </div>
            <p-table [value]="evals()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Data</th><th>Critério</th><th>Nota</th><th>Avaliador</th></tr></ng-template>
              <ng-template pTemplate="body" let-r><tr><td>{{ r.date }}</td><td>{{ r.criterion }}</td><td>{{ r.score }}</td><td>{{ r.evaluator }}</td></tr></ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhuma</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
          <p-tabPanel header="Dados Bancários">
            <div class="flex justify-content-end mb-2"><p-button label="Nova" icon="pi pi-plus" size="small" (onClick)="dialog = 'bank'" /></div>
            <p-table [value]="banks()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Banco</th><th>Agência</th><th>Conta</th><th>Pix</th><th style="width:70px">Ativa</th></tr></ng-template>
              <ng-template pTemplate="body" let-r><tr><td>{{ r.bankName }}</td><td>{{ r.agency }}</td><td>{{ r.accountNumber }}</td><td>{{ r.pixKey }}</td><td>{{ r.active ? '✓' : '' }}</td></tr></ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhuma</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
      </p-tabView>
    }

    <p-dialog header="Novo Documento" [(visible)]="showDoc" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Tipo</label><input pInputText [(ngModel)]="docForm.documentType" class="w-full" /></div>
        <div><label>Número</label><input pInputText [(ngModel)]="docForm.number" class="w-full" /></div>
        <div><label>Emissão</label><p-datePicker [(ngModel)]="docForm.issueDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Validade</label><p-datePicker [(ngModel)]="docForm.expiryDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveDoc()" /></ng-template>
    </p-dialog>

    <p-dialog header="Nova Avaliação" [(visible)]="showEval" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Critério</label><input pInputText [(ngModel)]="evalForm.criterion" class="w-full" /></div>
        <div><label>Nota (0-10)</label><p-inputNumber [(ngModel)]="evalForm.score" [min]="0" [max]="10" styleClass="w-full" /></div>
        <div><label>Avaliador</label><input pInputText [(ngModel)]="evalForm.evaluator" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveEval()" /></ng-template>
    </p-dialog>

    <p-dialog header="Nova Conta Bancária" [(visible)]="showBank" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Banco</label><input pInputText [(ngModel)]="bankForm.bankName" class="w-full" /></div>
        <div><label>Agência</label><input pInputText [(ngModel)]="bankForm.agency" class="w-full" /></div>
        <div><label>Conta</label><input pInputText [(ngModel)]="bankForm.accountNumber" class="w-full" /></div>
        <div><label>Chave Pix</label><input pInputText [(ngModel)]="bankForm.pixKey" class="w-full" /></div>
        <div><p-checkbox [(ngModel)]="bankForm.active" [binary]="true" label="Ativa" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveBank()" /></ng-template>
    </p-dialog>
  `,
})
export class SupplierDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  supplier = signal<any>(null);
  docs = signal<any[]>([]);
  evals = signal<any[]>([]);
  banks = signal<any[]>([]);
  dialog = '';
  docForm: any = { documentType: '', number: '', issueDate: null, expiryDate: null };
  evalForm: any = { criterion: '', score: 0, evaluator: '' };
  bankForm: any = { bankName: '', agency: '', accountNumber: '', pixKey: '', active: true };

  avgScore = computed(() => {
    const e = this.evals();
    return e.length ? e.reduce((s, x) => s + (x.score || 0), 0) / e.length : 0;
  });

  get showDoc() { return this.dialog === 'doc'; }
  set showDoc(v: boolean) { if (!v) this.dialog = ''; }
  get showEval() { return this.dialog === 'eval'; }
  set showEval(v: boolean) { if (!v) this.dialog = ''; }
  get showBank() { return this.dialog === 'bank'; }
  set showBank(v: boolean) { if (!v) this.dialog = ''; }

  private get suppId() { return this.route.snapshot.paramMap.get('suppId'); }

  ngOnInit() {
    this.http.get<any>(`/suppliers/${this.suppId}`).subscribe(s => this.supplier.set(s));
    this.loadAll();
  }

  loadAll() {
    this.http.get<any>(`/suppliers/${this.suppId}/documents`).subscribe(r => this.docs.set(r.content || r));
    this.http.get<any>(`/suppliers/${this.suppId}/evaluations`).subscribe(r => this.evals.set(r.content || r));
    this.http.get<any>(`/suppliers/${this.suppId}/bank-accounts`).subscribe(r => this.banks.set(r.content || r));
  }

  saveDoc() { this.http.post(`/suppliers/${this.suppId}/documents`, this.docForm).subscribe(() => { this.dialog = ''; this.loadAll(); }); }
  saveEval() { this.http.post(`/suppliers/${this.suppId}/evaluations`, this.evalForm).subscribe(() => { this.dialog = ''; this.loadAll(); }); }
  saveBank() { this.http.post(`/suppliers/${this.suppId}/bank-accounts`, this.bankForm).subscribe(() => { this.dialog = ''; this.loadAll(); }); }
}
