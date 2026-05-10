import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-composition-detail',
  template: `
    <page-header></page-header>
    @if (comp) {
      <mat-card>
        <mat-card-header>
          <mat-card-title>{{ comp.sinapiCode }} — {{ comp.description }}</mat-card-title>
          <mat-card-subtitle>
            {{ comp.unit }} | {{ comp.groupName }}
            <mat-chip-set><mat-chip [highlighted]="comp.origin === 'PROPRIO'">{{ comp.origin }}</mat-chip></mat-chip-set>
          </mat-card-subtitle>
        </mat-card-header>
        <mat-card-actions>
          @if (comp.editable) {
            <button mat-button color="primary" (click)="edit()"><mat-icon>edit</mat-icon> Editar</button>
            <button mat-button color="warn" (click)="delete()"><mat-icon>delete</mat-icon> Excluir</button>
          }
        </mat-card-actions>
        <mat-card-content>
          <h3>Insumos da Composição</h3>
          @if (!comp.items || comp.items.length === 0) {
            <p>Nenhum insumo vinculado.</p>
          } @else {
            <table mat-table [dataSource]="comp.items">
              <ng-container matColumnDef="materialCode"><th mat-header-cell *matHeaderCellDef>Código</th><td mat-cell *matCellDef="let i">{{ i.materialCode }}</td></ng-container>
              <ng-container matColumnDef="materialDescription"><th mat-header-cell *matHeaderCellDef>Descrição</th><td mat-cell *matCellDef="let i">{{ i.materialDescription }}</td></ng-container>
              <ng-container matColumnDef="materialUnit"><th mat-header-cell *matHeaderCellDef>Unidade</th><td mat-cell *matCellDef="let i">{{ i.materialUnit }}</td></ng-container>
              <ng-container matColumnDef="coefficient"><th mat-header-cell *matHeaderCellDef>Coeficiente</th><td mat-cell *matCellDef="let i">{{ i.coefficient }}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="itemColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: itemColumns;"></tr>
            </table>
          }
        </mat-card-content>
      </mat-card>
    }
  `,
  imports: [MatButtonModule, MatCardModule, MatChipsModule, MatIconModule, MatTableModule, PageHeader],
})
export class CompositionDetailComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  comp: any = null;
  itemColumns = ['materialCode', 'materialDescription', 'materialUnit', 'coefficient'];

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get(`/compositions/${id}`).subscribe(res => this.comp = res);
  }

  edit() { this.router.navigate(['edit'], { relativeTo: this.route }); }

  delete() {
    if (confirm('Excluir esta composição?')) {
      this.http.delete(`/compositions/${this.comp.id}`).subscribe(() => this.router.navigate(['/sinapi/compositions']));
    }
  }
}
