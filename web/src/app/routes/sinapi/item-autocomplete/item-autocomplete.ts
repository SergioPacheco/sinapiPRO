import { Component, inject, input, output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { toSignal } from '@angular/core/rxjs-interop';
import { debounceTime, filter, switchMap } from 'rxjs';

export type ItemType = 'MATERIAL' | 'LABOR' | 'EQUIPMENT' | 'COMPOSITION';

export interface ItemSearchResult {
  id: string;
  code: string;
  description: string;
  unit: string;
  type: ItemType;
  latestPrice: number | null;
}

@Component({
  selector: 'app-item-autocomplete',
  standalone: true,
  imports: [ReactiveFormsModule, MatAutocompleteModule, MatFormFieldModule, MatInputModule],
  template: `
    <mat-form-field appearance="outline" class="full-width">
      <mat-label>Buscar insumo (código ou descrição)</mat-label>
      <input matInput [formControl]="searchControl" [matAutocomplete]="auto" />
      <mat-autocomplete #auto="matAutocomplete" (optionSelected)="onSelect($event)">
        @for (item of results(); track item.id) {
          <mat-option [value]="item">
            <span class="option-code">{{ item.code }}</span> — {{ item.description }}
            <span class="option-unit">({{ item.unit }})</span>
          </mat-option>
        }
      </mat-autocomplete>
    </mat-form-field>
  `,
  styles: `
    .full-width { width: 100%; }
    .option-code { font-weight: 600; font-size: 12px; }
    .option-unit { color: var(--mat-sys-on-surface-variant); font-size: 12px; }
  `,
})
export class ItemAutocompleteComponent {
  readonly typeFilter = input<ItemType>();
  readonly itemSelected = output<ItemSearchResult>();

  private readonly http = inject(HttpClient);
  readonly searchControl = new FormControl('');

  readonly results = toSignal(
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      filter((v): v is string => typeof v === 'string' && v.length >= 3),
      switchMap(q => {
        const params: any = { q };
        const tf = this.typeFilter();
        if (tf) params.type = tf;
        return this.http.get<ItemSearchResult[]>('/api/v1/compositions/items/search', { params });
      })
    ),
    { initialValue: [] }
  );

  onSelect(event: any) {
    const item = event.option.value as ItemSearchResult;
    this.itemSelected.emit(item);
    this.searchControl.reset();
  }

  displayFn(item: ItemSearchResult | string): string {
    return typeof item === 'string' ? item : item?.description || '';
  }
}
