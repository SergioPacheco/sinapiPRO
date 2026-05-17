import { Component, Inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Observable, Subject, debounceTime, switchMap, of } from 'rxjs';

export interface SearchDialogColumn {
  key: string;
  label: string;
}

export interface SearchDialogData {
  title: string;
  columns: SearchDialogColumn[];
  displayFn: (item: any) => string;
  searchFn: (term: string) => Observable<any[]>;
}

@Component({
  selector: 'app-search-dialog',
  standalone: true,
  imports: [
    FormsModule, MatDialogModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatTableModule, MatProgressBarModule,
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>search</mat-icon>
      {{ data.title }}
    </h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="search-input">
        <mat-label>Buscar</mat-label>
        <input matInput [(ngModel)]="searchTerm" (ngModelChange)="onSearch($event)" placeholder="Digite para pesquisar..." autofocus />
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>

      @if (loading) { <mat-progress-bar mode="indeterminate" /> }

      <table mat-table [dataSource]="results" class="search-table">
        @for (col of data.columns; track col.key) {
          <ng-container [matColumnDef]="col.key">
            <th mat-header-cell *matHeaderCellDef>{{ col.label }}</th>
            <td mat-cell *matCellDef="let row">{{ row[col.key] }}</td>
          </ng-container>
        }
        <tr mat-header-row *matHeaderRowDef="columnKeys"></tr>
        <tr mat-row *matRowDef="let row; columns: columnKeys" (click)="select(row)" class="clickable-row"></tr>
      </table>

      @if (!loading && results.length === 0 && searchTerm) {
        <p class="no-results">Nenhum resultado encontrado.</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
    </mat-dialog-actions>
  `,
  styles: `
    h2 { display: flex; align-items: center; gap: 8px; }
    h2 mat-icon { color: var(--mat-sys-primary); }
    mat-dialog-content { min-width: 480px; max-height: 400px; }
    .search-input { width: 100%; }
    .search-table { width: 100%; }
    .clickable-row { cursor: pointer; }
    .clickable-row:hover { background: rgba(255,255,255,.04); }
    .no-results { text-align: center; color: var(--mat-sys-on-surface-variant); padding: 24px 0; }
  `,
})
export class SearchDialogComponent implements OnInit {
  searchTerm = '';
  results: any[] = [];
  loading = false;
  columnKeys: string[] = [];

  private search$ = new Subject<string>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: SearchDialogData,
    private dialogRef: MatDialogRef<SearchDialogComponent>,
  ) {}

  ngOnInit() {
    this.columnKeys = this.data.columns.map(c => c.key);
    this.search$.pipe(
      debounceTime(300),
      switchMap(term => {
        if (!term || term.length < 2) return of([]);
        this.loading = true;
        return this.data.searchFn(term);
      }),
    ).subscribe(results => {
      this.results = results;
      this.loading = false;
    });
  }

  onSearch(term: string) {
    this.search$.next(term);
  }

  select(item: any) {
    this.dialogRef.close(item);
  }
}
