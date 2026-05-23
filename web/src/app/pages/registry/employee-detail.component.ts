import { Component, inject, OnInit, signal } from '@angular/core';
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

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DatePickerModule, TabViewModule],
  template: `
    @if (employee(); as e) {
      <h3 style="margin:0 0 1rem">{{ e.name }}</h3>
      <p class="text-muted">{{ e.role }} | {{ e.document }}</p>
      <p-tabView>
          <p-tabPanel header="Treinamentos">
            <div class="flex justify-content-end mb-2"><p-button label="Novo" icon="pi pi-plus" size="small" (onClick)="dialog = 'training'" /></div>
            <p-table [value]="trainings()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Treinamento</th><th>Norma</th><th>Conclusão</th><th>Validade</th></tr></ng-template>
              <ng-template pTemplate="body" let-r><tr><td>{{ r.trainingName }}</td><td>{{ r.regulatoryStandard }}</td><td>{{ r.completionDate }}</td><td>{{ r.expiryDate }}</td></tr></ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhum</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
          <p-tabPanel header="EPIs">
            <div class="flex justify-content-end mb-2"><p-button label="Novo" icon="pi pi-plus" size="small" (onClick)="dialog = 'epi'" /></div>
            <p-table [value]="epis()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Descrição</th><th>CA</th><th>Entrega</th><th>Validade</th><th>Qtd</th></tr></ng-template>
              <ng-template pTemplate="body" let-r><tr><td>{{ r.epiDescription }}</td><td>{{ r.caNumber }}</td><td>{{ r.deliveryDate }}</td><td>{{ r.expiryDate }}</td><td>{{ r.quantity }}</td></tr></ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhum</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
          <p-tabPanel header="Exames Médicos">
            <div class="flex justify-content-end mb-2"><p-button label="Novo" icon="pi pi-plus" size="small" (onClick)="dialog = 'medical'" /></div>
            <p-table [value]="medicals()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Tipo</th><th>Data</th><th>Validade</th><th>Médico</th><th>Resultado</th></tr></ng-template>
              <ng-template pTemplate="body" let-r><tr><td>{{ r.examType }}</td><td>{{ r.examDate }}</td><td>{{ r.expiryDate }}</td><td>{{ r.physician }}</td><td>{{ r.result }}</td></tr></ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center text-muted p-3">Nenhum</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
      </p-tabView>
    }

    <p-dialog header="Novo Treinamento" [(visible)]="showTraining" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Treinamento</label><input pInputText [(ngModel)]="tForm.trainingName" class="w-full" /></div>
        <div><label>Norma Regulamentadora</label><input pInputText [(ngModel)]="tForm.regulatoryStandard" class="w-full" /></div>
        <div><label>Conclusão</label><p-datePicker [(ngModel)]="tForm.completionDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Validade</label><p-datePicker [(ngModel)]="tForm.expiryDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveTraining()" /></ng-template>
    </p-dialog>

    <p-dialog header="Novo EPI" [(visible)]="showEpi" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Descrição</label><input pInputText [(ngModel)]="eForm.epiDescription" class="w-full" /></div>
        <div><label>Número CA</label><input pInputText [(ngModel)]="eForm.caNumber" class="w-full" /></div>
        <div><label>Entrega</label><p-datePicker [(ngModel)]="eForm.deliveryDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Validade</label><p-datePicker [(ngModel)]="eForm.expiryDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Quantidade</label><p-inputNumber [(ngModel)]="eForm.quantity" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveEpi()" /></ng-template>
    </p-dialog>

    <p-dialog header="Novo Exame Médico" [(visible)]="showMedical" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Tipo</label><input pInputText [(ngModel)]="mForm.examType" class="w-full" /></div>
        <div><label>Data</label><p-datePicker [(ngModel)]="mForm.examDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Validade</label><p-datePicker [(ngModel)]="mForm.expiryDate" dateFormat="yy-mm-dd" styleClass="w-full" /></div>
        <div><label>Médico</label><input pInputText [(ngModel)]="mForm.physician" class="w-full" /></div>
        <div><label>Resultado</label><input pInputText [(ngModel)]="mForm.result" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveMedical()" /></ng-template>
    </p-dialog>
  `,
})
export class EmployeeDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  employee = signal<any>(null);
  trainings = signal<any[]>([]);
  epis = signal<any[]>([]);
  medicals = signal<any[]>([]);
  dialog = '';
  tForm: any = { trainingName: '', regulatoryStandard: '', completionDate: null, expiryDate: null };
  eForm: any = { epiDescription: '', caNumber: '', deliveryDate: null, expiryDate: null, quantity: 1 };
  mForm: any = { examType: '', examDate: null, expiryDate: null, physician: '', result: '' };

  get showTraining() { return this.dialog === 'training'; }
  set showTraining(v: boolean) { if (!v) this.dialog = ''; }
  get showEpi() { return this.dialog === 'epi'; }
  set showEpi(v: boolean) { if (!v) this.dialog = ''; }
  get showMedical() { return this.dialog === 'medical'; }
  set showMedical(v: boolean) { if (!v) this.dialog = ''; }

  private get empId() { return this.route.snapshot.paramMap.get('empId'); }

  ngOnInit() {
    this.http.get<any>(`/registry/employees/${this.empId}`).subscribe(e => this.employee.set(e));
    this.loadAll();
  }

  loadAll() {
    this.http.get<any>(`/registry/employees/${this.empId}/trainings`).subscribe(r => this.trainings.set(r.content || r));
    this.http.get<any>(`/registry/employees/${this.empId}/epi-deliveries`).subscribe(r => this.epis.set(r.content || r));
    this.http.get<any>(`/registry/employees/${this.empId}/medical-exams`).subscribe(r => this.medicals.set(r.content || r));
  }

  saveTraining() {
    this.http.post(`/registry/employees/${this.empId}/trainings`, this.tForm).subscribe(() => { this.dialog = ''; this.loadAll(); });
  }
  saveEpi() {
    this.http.post(`/registry/employees/${this.empId}/epi-deliveries`, this.eForm).subscribe(() => { this.dialog = ''; this.loadAll(); });
  }
  saveMedical() {
    this.http.post(`/registry/employees/${this.empId}/medical-exams`, this.mForm).subscribe(() => { this.dialog = ''; this.loadAll(); });
  }
}
