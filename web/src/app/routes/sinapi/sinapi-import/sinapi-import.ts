import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { PageHeader } from '@shared';

interface ImportResult {
  type: string;
  created: number;
  updated: number;
  errors: number;
  errorMessages: string[];
}

@Component({
  selector: 'app-sinapi-import',
  templateUrl: './sinapi-import.html',
  imports: [
    FormsModule, MatButtonModule, MatFormFieldModule, MatSelectModule,
    MatInputModule, MatIconModule, MatProgressBarModule, MatCardModule,
    MatSlideToggleModule, PageHeader,
  ],
})
export class SinapiImportComponent {
  private readonly http = inject(HttpClient);

  states = [
    'AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT',
    'PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'
  ];

  selectedState = '';
  referenceMonth = '';
  importType: 'materials' | 'compositions' = 'materials';
  desonerated = false;
  selectedFile: File | null = null;
  isLoading = false;
  result: ImportResult | null = null;

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
  }

  import() {
    if (!this.selectedFile || !this.selectedState || !this.referenceMonth) return;

    this.isLoading = true;
    this.result = null;

    const formData = new FormData();
    formData.append('file', this.selectedFile);
    formData.append('state', this.selectedState);
    formData.append('referenceMonth', this.referenceMonth + '-01');
    formData.append('desonerated', String(this.desonerated));

    const url = `/compositions/import/${this.importType}`;
    this.http.post<ImportResult>(url, formData).subscribe({
      next: res => { this.result = res; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }
}
