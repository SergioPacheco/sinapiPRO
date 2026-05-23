import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import { HotToastService } from '@ngxpert/hot-toast';
import { PageHeader } from '@shared';

export interface GlobalSettings {
  state: string;
  referenceMonth: string;
  desonerated: boolean;
}

@Component({
  selector: 'app-settings-list',
  template: `
    <page-header title="Configurações" subtitle="Configurações globais do sistema" />
    <mat-card>
      <mat-card-header><mat-card-title>Referência de Preços</mat-card-title></mat-card-header>
      <mat-card-content>
        <p>Defina o estado e mês de referência padrão para exibição de preços SINAPI em todo o sistema.</p>
        <div class="filter-bar">
          <mat-form-field appearance="outline" class="filter-select">
            <mat-label>Estado Padrão</mat-label>
            <mat-select [(ngModel)]="settings.state">
              @for (uf of states; track uf) { <mat-option [value]="uf">{{ uf }}</mat-option> }
            </mat-select>
          </mat-form-field>
          <mat-form-field appearance="outline" class="filter-month">
            <mat-label>Mês de Referência</mat-label>
            <input matInput type="month" [(ngModel)]="monthDisplay" />
          </mat-form-field>
          <mat-slide-toggle [(ngModel)]="settings.desonerated">Desonerado</mat-slide-toggle>
        </div>
      </mat-card-content>
      <mat-card-actions>
        <button mat-flat-button color="primary" (click)="save()"><mat-icon>save</mat-icon> Salvar</button>
      </mat-card-actions>
    </mat-card>
  `,
  imports: [FormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatSlideToggleModule, MatIconModule, PageHeader],
})
export class SettingsListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly toast = inject(HotToastService);

  settings: GlobalSettings = { state: 'SP', referenceMonth: '2024-12-01', desonerated: false };
  monthDisplay = '2024-12';
  states = ['AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT','PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'];

  ngOnInit() {
    this.http.get<GlobalSettings>('/settings').subscribe(s => {
      this.settings = s;
      this.monthDisplay = s.referenceMonth.substring(0, 7);
    });
  }

  save() {
    this.settings.referenceMonth = this.monthDisplay + '-01';
    this.http.put<GlobalSettings>('/settings', this.settings).subscribe({
      next: () => this.toast.success('Configurações salvas!'),
      error: () => this.toast.error('Erro ao salvar'),
    });
  }
}
