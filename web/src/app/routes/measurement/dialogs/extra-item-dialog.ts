import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface ExtraMeasurementItemResult {
  description: string;
  quantity: number;
  unitPrice: number;
  contractorName?: string;
}

@Component({
  selector: 'app-extra-item-dialog',
  template: `
    <h2 mat-dialog-title>Serviço extra</h2>
    <mat-dialog-content class="dialog-content">
      <mat-form-field appearance="outline">
        <mat-label>Descrição</mat-label>
        <input matInput [(ngModel)]="model.description" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Quantidade</mat-label>
        <input matInput type="number" step="0.0001" [(ngModel)]="model.quantity" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Preço unitário</mat-label>
        <input matInput type="number" step="0.0001" [(ngModel)]="model.unitPrice" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Empreiteiro</mat-label>
        <input matInput [(ngModel)]="model.contractorName" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" [disabled]="!model.description.trim() || !model.quantity || !model.unitPrice" (click)="confirm()">
        Salvar
      </button>
    </mat-dialog-actions>
  `,
  styles: `
    .dialog-content { display: grid; gap: 12px; min-width: 420px; }
    mat-form-field { width: 100%; }
  `,
  imports: [FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule],
})
export class ExtraItemDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<ExtraItemDialogComponent>);
  model: ExtraMeasurementItemResult = { description: '', quantity: 0, unitPrice: 0, contractorName: '' };

  confirm() {
    this.dialogRef.close(this.model);
  }
}
