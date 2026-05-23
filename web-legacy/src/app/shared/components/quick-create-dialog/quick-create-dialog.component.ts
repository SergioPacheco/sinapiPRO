import { Component, Inject, TemplateRef } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { NgTemplateOutlet } from '@angular/common';

export interface QuickCreateDialogData {
  title: string;
  formTemplate: TemplateRef<any>;
}

@Component({
  selector: 'app-quick-create-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatIconModule, NgTemplateOutlet],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>add_circle_outline</mat-icon>
      {{ data.title }}
    </h2>
    <mat-dialog-content>
      <ng-container *ngTemplateOutlet="data.formTemplate" />
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" (click)="save()">Salvar e selecionar</button>
    </mat-dialog-actions>
  `,
  styles: `
    h2 { display: flex; align-items: center; gap: 8px; }
    h2 mat-icon { color: var(--mat-sys-primary); }
    mat-dialog-content { min-width: 360px; padding-top: 8px; }
  `,
})
export class QuickCreateDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: QuickCreateDialogData,
    private dialogRef: MatDialogRef<QuickCreateDialogComponent>,
  ) {}

  save() {
    // Parent component should listen to the close event and handle save logic
    this.dialogRef.close({ action: 'save' });
  }
}
