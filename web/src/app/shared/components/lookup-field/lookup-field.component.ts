import { Component, EventEmitter, Input, Output, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-lookup-field',
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatIconModule, MatButtonModule, MatTooltipModule],
  template: `
    <mat-form-field class="lookup-field" appearance="outline">
      <mat-label>{{ label }}</mat-label>
      <input matInput [value]="displayValue" readonly [placeholder]="placeholder" (click)="onSearch.emit()" />
      <div matSuffix class="lookup-actions">
        @if (value && allowClear) {
          <button mat-icon-button matTooltip="Limpar" (click)="clear($event)">
            <mat-icon>close</mat-icon>
          </button>
        }
        <button mat-icon-button matTooltip="Pesquisar" (click)="onSearch.emit()">
          <mat-icon>search</mat-icon>
        </button>
        @if (allowCreate) {
          <button mat-icon-button matTooltip="Cadastrar novo" (click)="onCreate.emit()">
            <mat-icon>add</mat-icon>
          </button>
        }
      </div>
    </mat-form-field>
  `,
  styles: `
    .lookup-field { width: 100%; }
    .lookup-actions { display: flex; align-items: center; }
    .lookup-actions button { width: 32px; height: 32px; }
    .lookup-actions mat-icon { font-size: 18px; width: 18px; height: 18px; }
  `,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => LookupFieldComponent),
    multi: true,
  }],
})
export class LookupFieldComponent implements ControlValueAccessor {
  @Input() label = '';
  @Input() placeholder = 'Selecione...';
  @Input() displayValue = '';
  @Input() allowCreate = true;
  @Input() allowClear = true;

  @Output() onSearch = new EventEmitter<void>();
  @Output() onCreate = new EventEmitter<void>();
  @Output() onClear = new EventEmitter<void>();

  value: any = null;
  private onChange: (v: any) => void = () => {};
  private onTouched: () => void = () => {};

  writeValue(val: any) {
    this.value = val;
  }

  registerOnChange(fn: any) { this.onChange = fn; }
  registerOnTouched(fn: any) { this.onTouched = fn; }

  select(val: any, display: string) {
    this.value = val;
    this.displayValue = display;
    this.onChange(val);
    this.onTouched();
  }

  clear(event: Event) {
    event.stopPropagation();
    this.value = null;
    this.displayValue = '';
    this.onChange(null);
    this.onClear.emit();
  }
}
