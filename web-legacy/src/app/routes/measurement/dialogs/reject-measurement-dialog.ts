import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-reject-measurement-dialog',
  template: `
    <h2 mat-dialog-title>Rejeitar medição</h2>
    <mat-dialog-content class="dialog-content">
      <p>Informe o motivo da rejeição.</p>
      <mat-form-field appearance="outline">
        <mat-label>Motivo</mat-label>
        <textarea matInput rows="5" [(ngModel)]="reason"></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="warn" [disabled]="!reason.trim()" (click)="confirm()">Rejeitar</button>
    </mat-dialog-actions>
  `,
  styles: `
    .dialog-content { display: grid; gap: 12px; min-width: 360px; }
    mat-form-field { width: 100%; }
  `,
  imports: [FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule],
})
export class RejectMeasurementDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<RejectMeasurementDialogComponent>);
  reason = '';

  confirm() {
    this.dialogRef.close(this.reason.trim());
  }
}
