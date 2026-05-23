import { Component, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { SimpleCrudDialogComponent, FieldConfig } from './simple-crud-dialog';
import { RegistryService } from '../services/registry.service';

@Component({
  selector: 'app-simple-crud-page',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatDialogModule, MtxGridModule, PageHeader],
  template: `
    <page-header [title]="title()" [subtitle]="subtitle()">
      <button mat-flat-button color="primary" (click)="openDialog()">
        <mat-icon>add</mat-icon> Novo
      </button>
    </page-header>
    <mtx-grid [columns]="gridColumns()" [data]="list()" [loading]="isLoading()"
      [rowStriped]="true" [pageOnFront]="true" [showPaginator]="list().length > 10"
      [pageSize]="20" />
  `,
})
export class SimpleCrudPageComponent implements OnInit {
  title = input.required<string>();
  subtitle = input<string>('');
  columns = input.required<MtxGridColumn[]>();
  fields = input.required<FieldConfig[]>();
  apiPath = input.required<string>();

  private readonly service = inject(RegistryService);
  private readonly dialog = inject(MatDialog);
  private readonly mtxDialog = inject(MtxDialog);

  list = signal<any[]>([]);
  isLoading = signal(true);

  gridColumns = signal<MtxGridColumn[]>([]);

  ngOnInit() {
    const actionCol: MtxGridColumn = {
      header: 'Ações', field: 'actions', width: '100px', pinned: 'right', type: 'button',
      buttons: [
        { type: 'icon', icon: 'edit', tooltip: 'Editar', click: (row: any) => this.openDialog(row) },
        { type: 'icon', icon: 'delete', tooltip: 'Excluir', color: 'warn', click: (row: any) => this.confirmDelete(row) },
      ],
    };
    this.gridColumns.set([...this.columns(), actionCol]);
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.service.listGeneric(this.apiPath()).subscribe({
      next: data => { this.list.set(data); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  openDialog(row?: any) {
    const ref = this.dialog.open(SimpleCrudDialogComponent, {
      width: '500px',
      data: { fields: this.fields(), record: row || null },
    });
    ref.afterClosed().subscribe(result => {
      if (!result) return;
      const obs = row
        ? this.service.updateGeneric(this.apiPath(), row.id, result)
        : this.service.createGeneric(this.apiPath(), result);
      obs.subscribe(() => this.loadData());
    });
  }

  confirmDelete(row: any) {
    this.mtxDialog.confirm('Confirmar exclusão', `Excluir "${row.name || row.code || row.symbol}"?`, () =>
      this.service.deleteGeneric(this.apiPath(), row.id).subscribe(() => this.loadData())
    );
  }
}
