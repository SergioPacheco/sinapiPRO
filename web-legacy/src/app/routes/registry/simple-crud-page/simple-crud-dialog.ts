import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

export interface FieldConfig {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'select';
  required?: boolean;
  options?: { value: string; label: string }[];
}

interface DialogData {
  fields: FieldConfig[];
  record: any | null;
}

@Component({
  selector: 'app-simple-crud-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  template: `
    <h2 mat-dialog-title>{{ data.record ? 'Editar' : 'Novo' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="flex flex-col gap-3">
        @for (field of data.fields; track field.key) {
          @if (field.type === 'select') {
            <mat-form-field>
              <mat-label>{{ field.label }}</mat-label>
              <mat-select [formControlName]="field.key">
                @for (opt of field.options; track opt.value) {
                  <mat-option [value]="opt.value">{{ opt.label }}</mat-option>
                }
              </mat-select>
            </mat-form-field>
          } @else {
            <mat-form-field>
              <mat-label>{{ field.label }}</mat-label>
              <input matInput [formControlName]="field.key" [type]="field.type === 'number' ? 'number' : 'text'" />
            </mat-form-field>
          }
        }
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" [disabled]="form.invalid" (click)="save()">Salvar</button>
    </mat-dialog-actions>
  `,
})
export class SimpleCrudDialogComponent {
  readonly data: DialogData = inject(MAT_DIALOG_DATA);
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<SimpleCrudDialogComponent>);

  form = this.buildForm();

  private buildForm() {
    const group: any = {};
    for (const field of this.data.fields) {
      const value = this.data.record?.[field.key] ?? '';
      group[field.key] = field.required ? [value, Validators.required] : [value];
    }
    return this.fb.group(group);
  }

  save() {
    if (this.form.valid) this.dialogRef.close(this.form.value);
  }
}
