import { Component, Input, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-pdf-button',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTooltipModule],
  template: `
    <button mat-stroked-button [matTooltip]="tooltip" (click)="download()" [disabled]="loading">
      <mat-icon>{{ loading ? 'hourglass_empty' : 'picture_as_pdf' }}</mat-icon>
      {{ label }}
    </button>
  `,
  styles: `button { font-size: 13px; } mat-icon { font-size: 18px; width: 18px; height: 18px; margin-right: 4px; }`,
})
export class PdfButtonComponent {
  @Input() url = '';
  @Input() label = 'PDF';
  @Input() tooltip = 'Exportar PDF';
  @Input() filename = 'relatorio.pdf';

  loading = false;
  private readonly http = inject(HttpClient);

  download() {
    if (!this.url) return;
    this.loading = true;
    this.http.get(this.url, { responseType: 'blob' }).subscribe({
      next: blob => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = this.filename;
        a.click();
        URL.revokeObjectURL(a.href);
        this.loading = false;
      },
      error: () => this.loading = false,
    });
  }
}
