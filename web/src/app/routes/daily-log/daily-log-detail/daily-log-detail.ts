import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { PageHeader } from '@shared';
import { DailyLogService } from '../services/daily-log.service';
import { DailyLogDetail, WeatherDelay, WeatherDelaySummary } from '../models/daily-log.model';

@Component({
  selector: 'app-daily-log-detail',
  template: `
    @if (detail()) {
      <page-header title="Detalhe do Diário" [subtitle]="detail()!.logDate">
        <button mat-stroked-button (click)="back()"><mat-icon>arrow_back</mat-icon> Voltar</button>
      </page-header>

      <div class="summary-grid">
        <mat-card><strong>Mão de Obra</strong><span>{{ detail()!.labor.length }}</span></mat-card>
        <mat-card><strong>Equipamentos</strong><span>{{ detail()!.equipment.length }}</span></mat-card>
        <mat-card><strong>Ocorrências</strong><span>{{ detail()!.occurrences.length }}</span></mat-card>
        <mat-card><strong>Fotos</strong><span>{{ detail()!.photos.length }}</span></mat-card>
      </div>

      @if (weatherSummary()) {
        <div class="summary-grid">
          <mat-card><strong>Atrasos climáticos</strong><span>{{ weatherSummary()!.totalDelays }}</span></mat-card>
          <mat-card><strong>Dias perdidos</strong><span>{{ weatherSummary()!.fullDaysLost }}</span></mat-card>
          <mat-card><strong>Horas perdidas</strong><span>{{ weatherSummary()!.totalHoursLost }}</span></mat-card>
          <mat-card><strong>Últimos registros</strong><span>{{ weatherDelays().length }}</span></mat-card>
        </div>
      }

      <mat-card class="m-b-16">
        <mat-card-title>Atraso climático</mat-card-title>
        <div class="quick-form">
          <mat-form-field appearance="outline">
            <mat-label>Data</mat-label>
            <input matInput type="date" [(ngModel)]="newWeatherDelay.delayDate" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Condição</mat-label>
            <input matInput [(ngModel)]="newWeatherDelay.weatherCondition" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Horas perdidas</mat-label>
            <input matInput type="number" step="0.5" [(ngModel)]="newWeatherDelay.hoursLost" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Impacto</mat-label>
            <input matInput [(ngModel)]="newWeatherDelay.impactDescription" />
          </mat-form-field>
          <button mat-stroked-button color="primary" (click)="addWeatherDelay()">Registrar</button>
        </div>
        @for (wd of weatherDelays(); track wd.id) {
          <div class="item-row">
            <strong>{{ wd.delayDate }} - {{ wd.weatherCondition }}</strong>
            <span>{{ wd.hoursLost }}h {{ wd.fullDayLost ? '(dia completo)' : '' }}</span>
            <span>{{ wd.impactDescription || '' }}</span>
          </div>
        }
      </mat-card>

      <div class="panel-grid">
        <mat-card>
          <mat-card-title>Mão de obra</mat-card-title>
          <div class="quick-form">
            <mat-form-field appearance="outline">
              <mat-label>Nome</mat-label>
              <input matInput [(ngModel)]="newLabor.workerName" />
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Função</mat-label>
              <input matInput [(ngModel)]="newLabor.role" />
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Horas</mat-label>
              <input matInput type="number" step="0.5" [(ngModel)]="newLabor.hours" />
            </mat-form-field>
            <button mat-stroked-button color="primary" (click)="addLabor()">Adicionar</button>
          </div>
          <table mat-table [dataSource]="detail()!.labor" class="full-table">
            <ng-container matColumnDef="workerName"><th mat-header-cell *matHeaderCellDef>Nome</th><td mat-cell *matCellDef="let row">{{ row.workerName }}</td></ng-container>
            <ng-container matColumnDef="role"><th mat-header-cell *matHeaderCellDef>Função</th><td mat-cell *matCellDef="let row">{{ row.role }}</td></ng-container>
            <ng-container matColumnDef="hours"><th mat-header-cell *matHeaderCellDef>Horas</th><td mat-cell *matCellDef="let row">{{ row.hours }}</td></ng-container>
            <tr mat-header-row *matHeaderRowDef="laborColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: laborColumns;"></tr>
          </table>
        </mat-card>

        <mat-card>
          <mat-card-title>Equipamentos</mat-card-title>
          <div class="quick-form">
            <mat-form-field appearance="outline">
              <mat-label>Equipamento</mat-label>
              <input matInput [(ngModel)]="newEquipment.equipmentName" />
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Horas uso</mat-label>
              <input matInput type="number" step="0.5" [(ngModel)]="newEquipment.hoursUsed" />
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Horas ocioso</mat-label>
              <input matInput type="number" step="0.5" [(ngModel)]="newEquipment.hoursIdle" />
            </mat-form-field>
            <button mat-stroked-button color="primary" (click)="addEquipment()">Adicionar</button>
          </div>
          <table mat-table [dataSource]="detail()!.equipment" class="full-table">
            <ng-container matColumnDef="equipmentName"><th mat-header-cell *matHeaderCellDef>Equipamento</th><td mat-cell *matCellDef="let row">{{ row.equipmentName }}</td></ng-container>
            <ng-container matColumnDef="hoursUsed"><th mat-header-cell *matHeaderCellDef>Uso</th><td mat-cell *matCellDef="let row">{{ row.hoursUsed }}</td></ng-container>
            <ng-container matColumnDef="hoursIdle"><th mat-header-cell *matHeaderCellDef>Ocioso</th><td mat-cell *matCellDef="let row">{{ row.hoursIdle }}</td></ng-container>
            <tr mat-header-row *matHeaderRowDef="equipmentColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: equipmentColumns;"></tr>
          </table>
        </mat-card>
      </div>

      <div class="panel-grid">
        <mat-card>
          <mat-card-title>Ocorrências</mat-card-title>
          <div class="quick-form">
            <mat-form-field appearance="outline">
              <mat-label>Tipo</mat-label>
              <input matInput [(ngModel)]="newOccurrence.type" />
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Descrição</mat-label>
              <input matInput [(ngModel)]="newOccurrence.description" />
            </mat-form-field>
            <button mat-stroked-button color="primary" (click)="addOccurrence()">Adicionar</button>
          </div>
          @for (occ of detail()!.occurrences; track occ.id) {
            <div class="item-row">
              <strong>{{ occ.type }}</strong>
              <span>{{ occ.description }}</span>
            </div>
          }
        </mat-card>

        <mat-card>
          <mat-card-title>Fotos</mat-card-title>
          <div class="quick-form">
            <mat-form-field appearance="outline">
              <mat-label>Caminho do arquivo</mat-label>
              <input matInput [(ngModel)]="newPhoto.filePath" />
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Legenda</mat-label>
              <input matInput [(ngModel)]="newPhoto.caption" />
            </mat-form-field>
            <button mat-stroked-button color="primary" (click)="addPhoto()">Adicionar</button>
          </div>
          @for (photo of detail()!.photos; track photo.id) {
            <div class="item-row">
              <strong>{{ photo.caption || 'Foto' }}</strong>
              <span>{{ photo.filePath }}</span>
            </div>
          }
        </mat-card>
      </div>
    }
  `,
  styles: `
    .summary-grid, .panel-grid { display:grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px; }
    .panel-grid { grid-template-columns: repeat(2, 1fr); }
    .summary-grid mat-card, .panel-grid mat-card { padding: 16px; }
    .summary-grid span { font-size: 24px; font-weight: 700; }
    .full-table { width: 100%; }
    .quick-form { display:grid; grid-template-columns: repeat(4, 1fr); gap: 8px; align-items:center; margin-bottom: 10px; }
    .item-row { display:grid; gap: 4px; padding: 10px 0; border-bottom: 1px solid var(--mat-sys-outline-variant); }
    @media (max-width: 1200px) { .summary-grid, .panel-grid, .quick-form { grid-template-columns: 1fr; } }
  `,
  imports: [FormsModule, DecimalPipe, MatButtonModule, MatCardModule, MatIconModule, MatTableModule, MatFormFieldModule, MatInputModule, PageHeader],
})
export class DailyLogDetailComponent implements OnInit {
  private readonly service = inject(DailyLogService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly projectId = this.route.parent?.snapshot.paramMap.get('projectId') || '';
  private readonly id = this.route.snapshot.paramMap.get('id') || '';
  detail = signal<DailyLogDetail | null>(null);
  laborColumns = ['workerName', 'role', 'hours'];
  equipmentColumns = ['equipmentName', 'hoursUsed', 'hoursIdle'];
  weatherDelays = signal<WeatherDelay[]>([]);
  weatherSummary = signal<WeatherDelaySummary | null>(null);
  newLabor = { workerName: '', role: '', hours: 8 };
  newEquipment = { equipmentName: '', hoursUsed: 8, hoursIdle: 0 };
  newOccurrence = { type: '', description: '' };
  newPhoto = { filePath: '', caption: '' };
  newWeatherDelay = {
    delayDate: new Date().toISOString().slice(0, 10),
    weatherCondition: '',
    hoursLost: 1,
    fullDayLost: false,
    impactDescription: '',
  };

  ngOnInit() {
    this.loadDetail();
    this.service.listWeatherDelays(this.projectId).subscribe(d => this.weatherDelays.set(d));
    this.service.weatherDelaySummary(this.projectId).subscribe(s => this.weatherSummary.set(s));
  }

  back() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  addLabor() {
    if (!this.newLabor.workerName.trim() || !this.newLabor.role.trim()) return;
    this.service.addLabor(this.projectId, this.id, this.newLabor).subscribe(() => {
      this.newLabor = { workerName: '', role: '', hours: 8 };
      this.loadDetail();
    });
  }

  addEquipment() {
    if (!this.newEquipment.equipmentName.trim()) return;
    this.service.addEquipment(this.projectId, this.id, this.newEquipment).subscribe(() => {
      this.newEquipment = { equipmentName: '', hoursUsed: 8, hoursIdle: 0 };
      this.loadDetail();
    });
  }

  addOccurrence() {
    if (!this.newOccurrence.type.trim() || !this.newOccurrence.description.trim()) return;
    this.service.addOccurrence(this.projectId, this.id, this.newOccurrence).subscribe(() => {
      this.newOccurrence = { type: '', description: '' };
      this.loadDetail();
    });
  }

  addPhoto() {
    if (!this.newPhoto.filePath.trim()) return;
    this.service.addPhoto(this.projectId, this.id, this.newPhoto).subscribe(() => {
      this.newPhoto = { filePath: '', caption: '' };
      this.loadDetail();
    });
  }

  addWeatherDelay() {
    if (!this.newWeatherDelay.delayDate || !this.newWeatherDelay.weatherCondition.trim()) return;
    this.service.recordWeatherDelay(this.projectId, this.newWeatherDelay).subscribe(() => {
      this.newWeatherDelay = {
        delayDate: new Date().toISOString().slice(0, 10),
        weatherCondition: '',
        hoursLost: 1,
        fullDayLost: false,
        impactDescription: '',
      };
      this.service.listWeatherDelays(this.projectId).subscribe(d => this.weatherDelays.set(d));
      this.service.weatherDelaySummary(this.projectId).subscribe(s => this.weatherSummary.set(s));
    });
  }

  private loadDetail() {
    this.service.detail(this.projectId, this.id).subscribe(d => this.detail.set(d));
  }
}
