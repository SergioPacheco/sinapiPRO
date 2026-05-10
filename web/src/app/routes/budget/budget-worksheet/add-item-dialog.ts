import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';

interface Composition { id: string; sinapiCode: string; description: string; unit: string; origin: string; }

@Component({
  selector: 'app-add-item-dialog',
  template: `
    <h2 mat-dialog-title>Adicionar Composição ao Orçamento</h2>
    <mat-dialog-content>
      <div class="search-row">
        <mat-form-field appearance="outline" style="flex:1">
          <mat-label>Buscar composição</mat-label>
          <input matInput [(ngModel)]="search" (keyup.enter)="doSearch()" placeholder="Código ou descrição" />
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
        <button mat-flat-button color="primary" (click)="doSearch()">Buscar</button>
      </div>

      @if (results.length > 0) {
        <table mat-table [dataSource]="results" class="results-table">
          <ng-container matColumnDef="code"><th mat-header-cell *matHeaderCellDef>Código</th><td mat-cell *matCellDef="let c">{{ c.sinapiCode }}</td></ng-container>
          <ng-container matColumnDef="description"><th mat-header-cell *matHeaderCellDef>Descrição</th><td mat-cell *matCellDef="let c" class="desc-cell">{{ c.description }}</td></ng-container>
          <ng-container matColumnDef="unit"><th mat-header-cell *matHeaderCellDef>Un</th><td mat-cell *matCellDef="let c">{{ c.unit }}</td></ng-container>
          <ng-container matColumnDef="select"><th mat-header-cell *matHeaderCellDef></th><td mat-cell *matCellDef="let c">
            <button mat-icon-button color="primary" (click)="select(c)"><mat-icon>add_circle</mat-icon></button>
          </td></ng-container>
          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns;"></tr>
        </table>
      }

      @if (selected) {
        <div class="selected-section">
          <h4>Selecionado: {{ selected.sinapiCode }} — {{ selected.description }}</h4>
          <mat-form-field appearance="outline">
            <mat-label>Quantidade ({{ selected.unit }})</mat-label>
            <input matInput type="number" [(ngModel)]="quantity" min="0.01" step="0.01" />
          </mat-form-field>
        </div>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" [disabled]="!selected || !quantity" (click)="confirm()">
        Adicionar ao Orçamento
      </button>
    </mat-dialog-actions>
  `,
  styles: `
    .search-row { display: flex; gap: 8px; align-items: center; margin-bottom: 8px; }
    .results-table { width: 100%; max-height: 300px; overflow-y: auto; }
    .desc-cell { max-width: 350px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 12px; }
    .selected-section { margin-top: 16px; padding: 12px; background: #e8f5e9; border-radius: 8px; }
    .selected-section h4 { margin: 0 0 8px; font-size: 13px; }
  `,
  imports: [FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatIconModule, MatTableModule],
})
export class AddItemDialogComponent {
  private readonly http = inject(HttpClient);
  private readonly dialogRef = inject(MatDialogRef<AddItemDialogComponent>);

  search = '';
  results: Composition[] = [];
  selected: Composition | null = null;
  quantity = 0;
  columns = ['code', 'description', 'unit', 'select'];

  doSearch() {
    if (!this.search.trim()) return;
    const params = new HttpParams().set('q', this.search).set('size', '10');
    this.http.get<any>('/compositions', { params }).subscribe(res => this.results = res.content);
  }

  select(comp: Composition) {
    this.selected = comp;
    this.quantity = 1;
  }

  confirm() {
    if (this.selected && this.quantity > 0) {
      this.dialogRef.close({ compositionId: this.selected.id, quantity: this.quantity });
    }
  }
}
