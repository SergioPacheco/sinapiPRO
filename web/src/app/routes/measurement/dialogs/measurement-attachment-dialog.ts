import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface MeasurementAttachmentResult {
  file: File;
  title: string;
}

@Component({
  selector: 'app-measurement-attachment-dialog',
  template: `
    <h2 mat-dialog-title>Novo anexo</h2>
    <mat-dialog-content class="dialog-content">
      <mat-form-field appearance="outline">
        <mat-label>Título</mat-label>
        <input matInput [(ngModel)]="title" />
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Arquivo</mat-label>
        <input matInput type="file" (change)="onFileChange($event)" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" [disabled]="!title.trim() || !file" (click)="confirm()">
        Enviar
      </button>
    </mat-dialog-actions>
  `,
  styles: `
    .dialog-content { display: grid; gap: 12px; min-width: 360px; }
    mat-form-field { width: 100%; }
  `,
  imports: [FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule],
})
export class MeasurementAttachmentDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<MeasurementAttachmentDialogComponent>);

  title = '';
  file: File | null = null;

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.file = input.files?.[0] || null;
  }

  confirm() {
    if (!this.file) return;
    this.dialogRef.close({ file: this.file, title: this.title.trim() } satisfies MeasurementAttachmentResult);
  }
}
