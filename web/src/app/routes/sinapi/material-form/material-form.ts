import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-material-form',
  template: `
    <page-header></page-header>
    <mat-card>
      <mat-card-header>
        <mat-card-title>{{ isEdit ? 'Editar' : 'Novo' }} Insumo Próprio</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="form-grid">
          @if (!isEdit) {
            <mat-form-field><mat-label>Código</mat-label><input matInput [(ngModel)]="form.code" required /></mat-form-field>
          }
          <mat-form-field><mat-label>Descrição</mat-label><input matInput [(ngModel)]="form.description" required /></mat-form-field>
          <mat-form-field><mat-label>Unidade</mat-label><input matInput [(ngModel)]="form.unit" required /></mat-form-field>
        </div>
      </mat-card-content>
      <mat-card-actions>
        <button mat-flat-button color="primary" (click)="save()" [disabled]="!form.description || !form.unit">Salvar</button>
        <button mat-button (click)="cancel()">Cancelar</button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: `.form-grid { display: flex; flex-direction: column; gap: 8px; max-width: 600px; }`,
  imports: [FormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, PageHeader],
})
export class MaterialFormComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  isEdit = false;
  materialId = '';
  form = { code: '', description: '', unit: '' };

  ngOnInit() {
    this.materialId = this.route.snapshot.paramMap.get('id') || '';
    this.isEdit = !!this.materialId;
    if (this.isEdit) {
      this.http.get<any>(`/materials/${this.materialId}`).subscribe(m => {
        this.form = { code: m.sinapiCode, description: m.description, unit: m.unit };
      });
    }
  }

  save() {
    if (this.isEdit) {
      this.http.put(`/materials/${this.materialId}`, { description: this.form.description, unit: this.form.unit })
          .subscribe(() => this.router.navigate(['/sinapi/materials', this.materialId]));
    } else {
      this.http.post<any>('/materials', this.form)
          .subscribe(m => this.router.navigate(['/sinapi/materials', m.id]));
    }
  }

  cancel() { this.router.navigate(['/sinapi/materials']); }
}
