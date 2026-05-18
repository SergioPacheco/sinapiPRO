import { Component, inject, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatStepperModule } from '@angular/material/stepper';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { map } from 'rxjs';
import { PageHeader, LookupFieldComponent, SearchDialogComponent, QuickCreateDialogComponent } from '@shared';
import { NextActionService } from '@shared';
import { ProjectService } from '../services/project.service';
import { RegistryService } from '../../registry/services/registry.service';
import { Client, Employee } from '../../registry/models/registry.model';

@Component({
  selector: 'app-project-form',
  template: `
    <page-header [title]="isEdit ? 'Editar Obra' : 'Nova Obra'" [subtitle]="isEdit ? '' : 'Preencha os dados para criar a obra'" />

    @if (isEdit) {
      <!-- Edit mode: simple form -->
      <mat-card>
        <mat-card-content>
          <div class="form-grid">
            <mat-form-field><mat-label>Código</mat-label><input matInput [formControl]="stepBasic.controls.code" /></mat-form-field>
            <mat-form-field><mat-label>Nome da Obra</mat-label><input matInput [formControl]="stepBasic.controls.name" /></mat-form-field>
            <mat-form-field><mat-label>Descrição</mat-label><textarea matInput [formControl]="stepBasic.controls.description" rows="2"></textarea></mat-form-field>
            <mat-form-field><mat-label>Endereço</mat-label><input matInput [formControl]="stepBasic.controls.address" /></mat-form-field>
            <mat-form-field><mat-label>Cidade</mat-label><input matInput [formControl]="stepBasic.controls.city" /></mat-form-field>
            <mat-form-field style="width:80px"><mat-label>UF</mat-label><input matInput [formControl]="stepBasic.controls.state" /></mat-form-field>
          </div>
        </mat-card-content>
        <mat-card-actions>
          <button mat-flat-button color="primary" (click)="save()" [disabled]="stepBasic.invalid"><mat-icon>save</mat-icon> Salvar</button>
          <button mat-button (click)="cancel()">Cancelar</button>
        </mat-card-actions>
      </mat-card>
    } @else {
      <!-- Create mode: Wizard Stepper -->
      <mat-stepper linear #stepper>
        <!-- Step 1: Dados Básicos -->
        <mat-step [stepControl]="stepBasic" label="Dados da Obra">
          <div class="step-content">
            <div class="form-grid">
              <mat-form-field><mat-label>Código</mat-label><input matInput placeholder="Ex: OBR-001" [formControl]="stepBasic.controls.code" /></mat-form-field>
              <mat-form-field><mat-label>Nome da Obra</mat-label><input matInput [formControl]="stepBasic.controls.name" /></mat-form-field>
              <mat-form-field class="full-width"><mat-label>Descrição</mat-label><textarea matInput [formControl]="stepBasic.controls.description" rows="2"></textarea></mat-form-field>
              <mat-form-field><mat-label>Endereço</mat-label><input matInput [formControl]="stepBasic.controls.address" /></mat-form-field>
              <mat-form-field><mat-label>Cidade</mat-label><input matInput [formControl]="stepBasic.controls.city" /></mat-form-field>
              <mat-form-field style="width:80px"><mat-label>UF</mat-label><input matInput [formControl]="stepBasic.controls.state" /></mat-form-field>
            </div>
            <div class="step-actions">
              <button mat-flat-button color="primary" matStepperNext [disabled]="stepBasic.invalid">Próximo <mat-icon>arrow_forward</mat-icon></button>
            </div>
          </div>
        </mat-step>

        <!-- Step 2: Cliente -->
        <mat-step [stepControl]="stepClient" label="Cliente">
          <div class="step-content">
            <app-lookup-field
              label="Cliente"
              [displayValue]="clientDisplay"
              [allowCreate]="true"
              (onSearch)="searchClient()"
              (onCreate)="createClient()"
              (onClear)="clearClient()"
            />
            <mat-form-field><mat-label>CNPJ/CPF</mat-label><input matInput [formControl]="stepClient.controls.customerDocument" /></mat-form-field>
            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
              <button mat-flat-button color="primary" matStepperNext [disabled]="stepClient.invalid">Próximo <mat-icon>arrow_forward</mat-icon></button>
            </div>
          </div>
        </mat-step>

        <!-- Step 3: Equipe -->
        <mat-step label="Equipe" [optional]="true">
          <div class="step-content">
            <app-lookup-field
              label="Engenheiro Responsável"
              [displayValue]="engineerDisplay"
              [allowCreate]="true"
              (onSearch)="searchEngineer()"
              (onCreate)="createEngineer()"
              (onClear)="clearEngineer()"
            />
            <mat-form-field><mat-label>Nº ART/RRT</mat-label><input matInput [formControl]="stepTeam.controls.artNumber" /></mat-form-field>
            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
              <button mat-flat-button color="primary" matStepperNext>Próximo <mat-icon>arrow_forward</mat-icon></button>
            </div>
          </div>
        </mat-step>

        <!-- Step 4: Datas e Valores -->
        <mat-step label="Datas e Valores" [optional]="true">
          <div class="step-content">
            <div class="form-grid">
              <mat-form-field><mat-label>Data Início</mat-label><input matInput type="date" [formControl]="stepDates.controls.startDate" /></mat-form-field>
              <mat-form-field><mat-label>Previsão Término</mat-label><input matInput type="date" [formControl]="stepDates.controls.expectedEndDate" /></mat-form-field>
              <mat-form-field><mat-label>Área Total (m²)</mat-label><input matInput type="number" [formControl]="stepDates.controls.totalArea" /></mat-form-field>
              <mat-form-field><mat-label>Valor Previsto (R$)</mat-label><input matInput type="number" [formControl]="stepDates.controls.totalBudget" /></mat-form-field>
            </div>
            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
              <button mat-flat-button color="primary" matStepperNext>Próximo <mat-icon>arrow_forward</mat-icon></button>
            </div>
          </div>
        </mat-step>

        <!-- Step 5: Confirmação -->
        <mat-step label="Confirmação">
          <div class="step-content">
            <mat-card class="preview-card">
              <mat-card-title>Resumo da Obra</mat-card-title>
              <mat-card-content>
                <div class="preview-grid">
                  <div><strong>Código:</strong> {{ stepBasic.value.code }}</div>
                  <div><strong>Nome:</strong> {{ stepBasic.value.name }}</div>
                  <div><strong>Cliente:</strong> {{ clientDisplay || '—' }}</div>
                  <div><strong>Local:</strong> {{ stepBasic.value.city || '—' }}/{{ stepBasic.value.state || '—' }}</div>
                  <div><strong>Engenheiro:</strong> {{ engineerDisplay || '—' }}</div>
                  <div><strong>Início:</strong> {{ stepDates.value.startDate || '—' }}</div>
                  <div><strong>Valor:</strong> {{ stepDates.value.totalBudget ? 'R$ ' + stepDates.value.totalBudget : '—' }}</div>
                </div>
              </mat-card-content>
            </mat-card>
            <div class="step-actions">
              <button mat-button matStepperPrevious><mat-icon>arrow_back</mat-icon> Voltar</button>
              <button mat-flat-button color="primary" (click)="save()"><mat-icon>check</mat-icon> Criar Obra</button>
            </div>
          </div>
        </mat-step>
      </mat-stepper>
    }

    <!-- Quick Create Templates -->
    <ng-template #clientFormTpl>
      <div class="quick-form">
        <mat-form-field appearance="outline"><mat-label>Nome</mat-label><input matInput [(ngModel)]="newClient.name" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>CNPJ/CPF</mat-label><input matInput [(ngModel)]="newClient.document" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Email</mat-label><input matInput [(ngModel)]="newClient.email" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Telefone</mat-label><input matInput [(ngModel)]="newClient.phone" /></mat-form-field>
      </div>
    </ng-template>
    <ng-template #employeeFormTpl>
      <div class="quick-form">
        <mat-form-field appearance="outline"><mat-label>Nome</mat-label><input matInput [(ngModel)]="newEmployee.name" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Código</mat-label><input matInput [(ngModel)]="newEmployee.employeeCode" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Cargo</mat-label><input matInput [(ngModel)]="newEmployee.role" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Especialidade</mat-label><input matInput [(ngModel)]="newEmployee.specialty" /></mat-form-field>
      </div>
    </ng-template>
  `,
  styles: `
    .step-content { padding: 16px 0; max-width: 600px; }
    .form-grid { display: flex; flex-wrap: wrap; gap: 12px; }
    .form-grid mat-form-field { flex: 1 1 250px; }
    .form-grid .full-width { flex: 1 1 100%; }
    .step-actions { display: flex; gap: 12px; margin-top: 24px; }
    .preview-card { margin-bottom: 16px; }
    .preview-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .quick-form { display: flex; flex-direction: column; gap: 8px; }
    .quick-form mat-form-field { width: 100%; }
  `,
  imports: [
    FormsModule, ReactiveFormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatStepperModule, MatDialogModule, PageHeader, LookupFieldComponent,
  ],
})
export class ProjectFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ProjectService);
  private readonly registry = inject(RegistryService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly nextAction = inject(NextActionService);

  @ViewChild('clientFormTpl') clientFormTpl!: TemplateRef<any>;
  @ViewChild('employeeFormTpl') employeeFormTpl!: TemplateRef<any>;

  isEdit = false;
  projectId = '';
  clientDisplay = '';
  engineerDisplay = '';
  newClient: Partial<Client> = {};
  newEmployee: Partial<Employee> = {};

  stepBasic = this.fb.nonNullable.group({
    code: ['', Validators.required],
    name: ['', Validators.required],
    description: [''],
    address: [''],
    city: [''],
    state: [''],
  });

  stepClient = this.fb.nonNullable.group({
    customerName: ['', Validators.required],
    customerDocument: [''],
    customerId: [''],
  });

  stepTeam = this.fb.nonNullable.group({
    responsibleEngineer: [''],
    responsibleEngineerId: [''],
    artNumber: [''],
  });

  stepDates = this.fb.nonNullable.group({
    startDate: [''],
    expectedEndDate: [''],
    totalArea: [undefined as number | undefined],
    totalBudget: [undefined as number | undefined],
  });

  ngOnInit() {
    this.projectId = this.route.snapshot.paramMap.get('projectId') || '';
    this.isEdit = !!this.projectId;
    if (this.isEdit) {
      this.service.getById(this.projectId).subscribe(p => {
        this.stepBasic.patchValue(p as any);
        this.stepClient.patchValue({ customerName: p.customerName, customerDocument: p.customerDocument });
        this.clientDisplay = p.customerName;
        this.stepTeam.patchValue({ responsibleEngineer: p.responsibleEngineer, artNumber: p.artNumber });
        this.engineerDisplay = p.responsibleEngineer;
        this.stepDates.patchValue(p as any);
      });
    }
  }

  save() {
    const payload = {
      ...this.stepBasic.getRawValue(),
      ...this.stepClient.getRawValue(),
      ...this.stepTeam.getRawValue(),
      ...this.stepDates.getRawValue(),
    };
    if (this.isEdit) {
      this.service.update(this.projectId, payload).subscribe(() => this.router.navigate(['/projects', this.projectId]));
    } else {
      this.service.create(payload).subscribe(p => {
        this.router.navigate(['/projects', p.id]);
        this.nextAction.suggest('project.created', `/projects/${p.id}`);
      });
    }
  }

  cancel() { this.router.navigate(['/projects']); }

  // --- Client Lookup ---
  searchClient() {
    const ref = this.dialog.open(SearchDialogComponent, {
      data: {
        title: 'Pesquisar Cliente',
        columns: [{ key: 'name', label: 'Nome' }, { key: 'document', label: 'CNPJ/CPF' }, { key: 'city', label: 'Cidade' }],
        displayFn: (c: Client) => c.name,
        searchFn: (term: string) => this.registry.searchClients(term).pipe(map(r => r.content)),
      },
    });
    ref.afterClosed().subscribe((client: Client | undefined) => {
      if (client) {
        this.stepClient.patchValue({ customerId: client.id, customerName: client.name, customerDocument: client.document });
        this.clientDisplay = client.name;
      }
    });
  }

  createClient() {
    this.newClient = {};
    const ref = this.dialog.open(QuickCreateDialogComponent, { data: { title: 'Cadastrar Cliente', formTemplate: this.clientFormTpl } });
    ref.afterClosed().subscribe(result => {
      if (result?.action === 'save' && this.newClient.name) {
        this.registry.createClient(this.newClient).subscribe(c => {
          this.stepClient.patchValue({ customerId: c.id, customerName: c.name, customerDocument: c.document || '' });
          this.clientDisplay = c.name;
        });
      }
    });
  }

  clearClient() { this.stepClient.patchValue({ customerId: '', customerName: '', customerDocument: '' }); this.clientDisplay = ''; }

  // --- Engineer Lookup ---
  searchEngineer() {
    const ref = this.dialog.open(SearchDialogComponent, {
      data: {
        title: 'Pesquisar Engenheiro',
        columns: [{ key: 'name', label: 'Nome' }, { key: 'role', label: 'Cargo' }, { key: 'specialty', label: 'Especialidade' }],
        displayFn: (e: Employee) => e.name,
        searchFn: (term: string) => this.registry.searchEmployees(term).pipe(map(r => r.content)),
      },
    });
    ref.afterClosed().subscribe((emp: Employee | undefined) => {
      if (emp) { this.stepTeam.patchValue({ responsibleEngineerId: emp.id, responsibleEngineer: emp.name }); this.engineerDisplay = emp.name; }
    });
  }

  createEngineer() {
    this.newEmployee = { role: 'Engenheiro Civil', specialty: 'Engenharia Civil' };
    const ref = this.dialog.open(QuickCreateDialogComponent, { data: { title: 'Cadastrar Engenheiro', formTemplate: this.employeeFormTpl } });
    ref.afterClosed().subscribe(result => {
      if (result?.action === 'save' && this.newEmployee.name) {
        this.registry.createEmployee(this.newEmployee).subscribe(e => {
          this.stepTeam.patchValue({ responsibleEngineerId: e.id, responsibleEngineer: e.name }); this.engineerDisplay = e.name;
        });
      }
    });
  }

  clearEngineer() { this.stepTeam.patchValue({ responsibleEngineerId: '', responsibleEngineer: '' }); this.engineerDisplay = ''; }
}
