import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-daily-log-form',
  standalone: true,
  imports: [FormsModule, InputTextModule, TextareaModule, CalendarModule, DropdownModule, ButtonModule, InputNumberModule],
  template: `
    <h3 style="margin:0 0 1rem">Novo Registro — Diário de Obra</h3>
    <div class="grid">
      <div class="col-12 md:col-4">
        <label>Data</label>
        <p-calendar [(ngModel)]="form.logDate" dateFormat="yy-mm-dd" styleClass="w-full" />
      </div>
      <div class="col-12 md:col-4">
        <label>Clima Manhã</label>
        <p-dropdown [(ngModel)]="form.weatherMorning" [options]="weatherOptions" styleClass="w-full" />
      </div>
      <div class="col-12 md:col-4">
        <label>Clima Tarde</label>
        <p-dropdown [(ngModel)]="form.weatherAfternoon" [options]="weatherOptions" styleClass="w-full" />
      </div>
      <div class="col-12 md:col-4">
        <label>Efetivo (pessoas)</label>
        <p-inputNumber [(ngModel)]="form.workerCount" styleClass="w-full" />
      </div>
      <div class="col-12">
        <label>Observações</label>
        <textarea pTextarea [(ngModel)]="form.notes" rows="4" class="w-full"></textarea>
      </div>
      <div class="col-12 flex gap-2">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" [loading]="saving()" />
        <p-button label="Cancelar" severity="secondary" (onClick)="cancel()" />
      </div>
    </div>
  `,
})
export class DailyLogFormComponent {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private router = inject(Router);
  private messages = inject(MessageService);
  saving = signal(false);

  weatherOptions = ['Bom', 'Nublado', 'Chuvoso', 'Tempestade'].map(v => ({ label: v, value: v.toUpperCase() }));
  form: any = { logDate: new Date(), weatherMorning: 'BOM', weatherAfternoon: 'BOM', workerCount: 0, notes: '' };

  save() {
    this.saving.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/daily-logs`, this.form).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Registro salvo' }); this.router.navigate(['..'], { relativeTo: this.route }); },
      error: () => this.saving.set(false),
    });
  }

  cancel() { this.router.navigate(['..'], { relativeTo: this.route }); }
}
