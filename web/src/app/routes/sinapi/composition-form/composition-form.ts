import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { PageHeader } from '@shared';

interface ItemForm { materialCode: string; coefficient: number; }

@Component({
  selector: 'app-composition-form',
  template: `
    <page-header></page-header>
    <mat-card>
      <mat-card-header>
        <mat-card-title>{{ isEdit ? 'Editar' : 'Nova' }} Composição Própria</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <div class="form-grid">
          @if (!isEdit) {
            <mat-form-field><mat-label>Código</mat-label><input matInput [(ngModel)]="form.code" required /></mat-form-field>
          }
          <mat-form-field><mat-label>Descrição</mat-label><input matInput [(ngModel)]="form.description" required /></mat-form-field>
          <mat-form-field><mat-label>Unidade</mat-label><input matInput [(ngModel)]="form.unit" required /></mat-form-field>
          <mat-form-field><mat-label>Grupo</mat-label><input matInput [(ngModel)]="form.groupName" /></mat-form-field>
        </div>
        <h3>Insumos</h3>
        @for (item of items; track $index) {
          <div class="item-row">
            <mat-form-field><mat-label>Código Insumo</mat-label><input matInput [(ngModel)]="item.materialCode" /></mat-form-field>
            <mat-form-field><mat-label>Coeficiente</mat-label><input matInput type="number" [(ngModel)]="item.coefficient" /></mat-form-field>
            <button mat-icon-button color="warn" (click)="removeItem($index)"><mat-icon>remove_circle</mat-icon></button>
          </div>
        }
        <button mat-button (click)="addItem()"><mat-icon>add</mat-icon> Adicionar Insumo</button>
      </mat-card-content>
      <mat-card-actions>
        <button mat-flat-button color="primary" (click)="save()" [disabled]="!form.description || !form.unit">Salvar</button>
        <button mat-button (click)="cancel()">Cancelar</button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: `
    .form-grid { display: flex; flex-direction: column; gap: 8px; max-width: 600px; }
    .item-row { display: flex; gap: 8px; align-items: center; }
  `,
  imports: [FormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule, PageHeader],
})
export class CompositionFormComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  isEdit = false;
  compId = '';
  form = { code: '', description: '', unit: '', groupName: '' };
  items: ItemForm[] = [];

  ngOnInit() {
    this.compId = this.route.snapshot.paramMap.get('id') || '';
    this.isEdit = !!this.compId;
    if (this.isEdit) {
      this.http.get<any>(`/compositions/${this.compId}`).subscribe(c => {
        this.form = { code: c.sinapiCode, description: c.description, unit: c.unit, groupName: c.groupName || '' };
        this.items = (c.items || []).map((i: any) => ({ materialCode: i.materialCode, coefficient: i.coefficient }));
      });
    }
  }

  addItem() { this.items.push({ materialCode: '', coefficient: 1 }); }
  removeItem(i: number) { this.items.splice(i, 1); }

  save() {
    const validItems = this.items.filter(i => i.materialCode && i.coefficient);
    if (this.isEdit) {
      this.http.put(`/compositions/${this.compId}`, { ...this.form, items: validItems })
          .subscribe(() => this.router.navigate(['/sinapi/compositions', this.compId]));
    } else {
      this.http.post<any>('/compositions', { ...this.form, items: validItems })
          .subscribe(c => this.router.navigate(['/sinapi/compositions', c.id]));
    }
  }

  cancel() { this.router.navigate(['/sinapi/compositions']); }
}
