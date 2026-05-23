import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface BudgetMemoLine {
  description: string;
  formula: string;
  value: number;
}

@Component({
  selector: 'app-budget-memo-dialog',
  template: `
    <h2 mat-dialog-title>Memória de cálculo</h2>
    <mat-dialog-content class="dialog-content">
      <mat-form-field appearance="outline">
        <mat-label>Descrição</mat-label>
        <input matInput [(ngModel)]="line.description" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Fórmula</mat-label>
        <input matInput [(ngModel)]="line.formula" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Valor</mat-label>
        <input matInput type="number" step="0.0001" [(ngModel)]="line.value" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" [disabled]="!line.description.trim() || !line.formula.trim()" (click)="confirm()">
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
export class BudgetMemoDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<BudgetMemoDialogComponent>);
  private readonly data = inject<BudgetMemoLine | null>(MAT_DIALOG_DATA, { optional: true });
  line: BudgetMemoLine = this.data ? { ...this.data } : { description: '', formula: '', value: 0 };

  confirm() {
    this.dialogRef.close(this.line);
  }
}
