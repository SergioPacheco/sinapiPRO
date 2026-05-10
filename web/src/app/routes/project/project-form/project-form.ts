import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { PageHeader } from '@shared';
import { ProjectService } from '../services/project.service';

@Component({
  selector: 'app-project-form',
  template: `
    <page-header [title]="isEdit ? 'Editar Obra' : 'Nova Obra'" />
    <mat-card>
      <mat-card-content>
        <div class="form-grid">
          <mat-form-field><mat-label>Código</mat-label><input matInput [(ngModel)]="form.code" [disabled]="isEdit" required /></mat-form-field>
          <mat-form-field><mat-label>Nome da Obra</mat-label><input matInput [(ngModel)]="form.name" required /></mat-form-field>
          <mat-form-field><mat-label>Cliente</mat-label><input matInput [(ngModel)]="form.customerName" required /></mat-form-field>
          <mat-form-field><mat-label>CNPJ/CPF</mat-label><input matInput [(ngModel)]="form.customerDocument" /></mat-form-field>
          <mat-form-field><mat-label>Endereço</mat-label><input matInput [(ngModel)]="form.address" /></mat-form-field>
          <mat-form-field><mat-label>Cidade</mat-label><input matInput [(ngModel)]="form.city" /></mat-form-field>
          <mat-form-field style="width:80px"><mat-label>UF</mat-label><input matInput [(ngModel)]="form.state" maxlength="2" /></mat-form-field>
          <mat-form-field><mat-label>Engenheiro Responsável</mat-label><input matInput [(ngModel)]="form.responsibleEngineer" /></mat-form-field>
          <mat-form-field><mat-label>Nº ART/RRT</mat-label><input matInput [(ngModel)]="form.artNumber" /></mat-form-field>
          <mat-form-field><mat-label>Data Início</mat-label><input matInput type="date" [(ngModel)]="form.startDate" /></mat-form-field>
          <mat-form-field><mat-label>Previsão Término</mat-label><input matInput type="date" [(ngModel)]="form.expectedEndDate" /></mat-form-field>
          <mat-form-field><mat-label>Área Total (m²)</mat-label><input matInput type="number" [(ngModel)]="form.totalArea" /></mat-form-field>
          <mat-form-field><mat-label>Valor Previsto (R$)</mat-label><input matInput type="number" [(ngModel)]="form.totalBudget" /></mat-form-field>
          <mat-form-field class="full-width"><mat-label>Descrição</mat-label><textarea matInput [(ngModel)]="form.description" rows="3"></textarea></mat-form-field>
        </div>
      </mat-card-content>
      <mat-card-actions>
        <button mat-flat-button color="primary" (click)="save()" [disabled]="!form.code || !form.name || !form.customerName">
          <mat-icon>save</mat-icon> Salvar
        </button>
        <button mat-button (click)="cancel()">Cancelar</button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: `
    .form-grid { display: flex; flex-wrap: wrap; gap: 12px; }
    .form-grid mat-form-field { flex: 1 1 250px; }
    .form-grid .full-width { flex: 1 1 100%; }
  `,
  imports: [FormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule, PageHeader],
})
export class ProjectFormComponent implements OnInit {
  private readonly service = inject(ProjectService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  isEdit = false;
  projectId = '';
  form: any = {};

  ngOnInit() {
    this.projectId = this.route.snapshot.paramMap.get('projectId') || '';
    this.isEdit = !!this.projectId;
    if (this.isEdit) {
      this.service.getById(this.projectId).subscribe(p => this.form = { ...p });
    }
  }

  save() {
    if (this.isEdit) {
      this.service.update(this.projectId, this.form).subscribe(() => this.router.navigate(['/projects', this.projectId]));
    } else {
      this.service.create(this.form).subscribe(p => this.router.navigate(['/projects', p.id]));
    }
  }

  cancel() { this.router.navigate(['/projects']); }
}
