import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface UpdateBaseDateResult {
  referenceDate: string;
  state: string;
}

@Component({
  selector: 'app-base-date-dialog',
  template: `
    <h2 mat-dialog-title>Atualizar data base</h2>
    <mat-dialog-content class="dialog-content">
      <mat-form-field appearance="outline">
        <mat-label>Referência</mat-label>
        <input matInput type="month" [(ngModel)]="referenceMonth" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>UF</mat-label>
        <input matInput maxlength="2" [(ngModel)]="state" placeholder="Ex: SP" />
      </mat-form-field>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" [disabled]="!referenceMonth || !state" (click)="confirm()">
        Atualizar
      </button>
    </mat-dialog-actions>
  `,
  styles: `
    .dialog-content { display: grid; gap: 16px; padding-top: 8px; min-width: 320px; }
    mat-form-field { width: 100%; }
  `,
  imports: [FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule],
})
export class BaseDateDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<BaseDateDialogComponent>);

  referenceMonth = '';
  state = '';

  confirm() {
    const referenceDate = `${this.referenceMonth}-01`;
    this.dialogRef.close({
      referenceDate,
      state: this.state.toUpperCase(),
    } satisfies UpdateBaseDateResult);
  }
}
