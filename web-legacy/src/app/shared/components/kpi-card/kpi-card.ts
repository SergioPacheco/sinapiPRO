import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

export interface KpiConfig {
  icon: string;
  label: string;
  value: string;
  trend?: number;
  progress?: number;
  footer?: string;
  route?: string;
  queryParams?: Record<string, string>;
}

@Component({
  selector: 'app-kpi-card',
  standalone: true,
  imports: [RouterLink, MatIconModule],
  template: `
    <div class="kpi-card" [class.clickable]="config().route" [routerLink]="config().route || null" [queryParams]="config().queryParams || {}">
      <span class="kpi-label">{{ config().label }}</span>
      <div class="kpi-main">
        <span class="kpi-value">{{ config().value }}</span>
        @if (config().trend) {
          <span class="kpi-trend" [class.positive]="config().trend! > 0" [class.negative]="config().trend! < 0">
            {{ config().trend! > 0 ? '↑' : '↓' }} {{ config().trend! > 0 ? '+' : '' }}{{ config().trend }}%
          </span>
        }
      </div>
      @if (config().progress != null) {
        <div class="kpi-bar"><div class="kpi-bar-fill" [style.width.%]="config().progress"></div></div>
      }
      @if (config().footer) {
        <span class="kpi-footer">{{ config().footer }}</span>
      }
    </div>
  `,
  styles: `
    .kpi-card {
      background: var(--mat-sys-surface-container);
      border: 1px solid var(--mat-sys-outline-variant);
      border-radius: 12px;
      padding: 20px;
      min-width: 200px;
      flex: 1;
    }
    .kpi-card.clickable { cursor: pointer; transition: box-shadow 0.2s; }
    .kpi-card.clickable:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
    .kpi-label { font-size: 12px; color: var(--mat-sys-on-surface-variant); text-transform: uppercase; letter-spacing: 0.5px; }
    .kpi-main { display: flex; align-items: baseline; gap: 8px; margin-top: 4px; }
    .kpi-value { font-size: 28px; font-weight: 700; color: var(--mat-sys-on-surface); }
    .kpi-trend { font-size: 13px; font-weight: 600; }
    .kpi-trend.positive { color: #4caf50; }
    .kpi-trend.negative { color: #f44336; }
    .kpi-bar { height: 4px; background: var(--mat-sys-surface-container-highest); border-radius: 2px; margin-top: 12px; }
    .kpi-bar-fill { height: 100%; background: var(--mat-sys-primary); border-radius: 2px; transition: width 0.3s; }
    .kpi-footer { font-size: 11px; color: var(--mat-sys-on-surface-variant); margin-top: 6px; display: block; }
  `,
})
export class KpiCardComponent {
  config = input.required<KpiConfig>();
}
