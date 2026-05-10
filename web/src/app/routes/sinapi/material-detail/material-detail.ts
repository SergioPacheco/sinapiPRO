import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-material-detail',
  templateUrl: './material-detail.html',
  imports: [
    FormsModule, DecimalPipe, MatButtonModule, MatCardModule, MatChipsModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule,
    MatTableModule, MatSlideToggleModule, PageHeader,
  ],
})
export class MaterialDetailComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  material: any = null;
  priceColumns = ['state', 'referenceMonth', 'price', 'desonerated'];

  // Price form
  showPriceForm = false;
  newPrice = { state: '', referenceMonth: '', price: 0, desonerated: false };

  states = ['AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT','PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'];

  ngOnInit() { this.load(); }

  load() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get(`/materials/${id}`).subscribe(res => this.material = res);
  }

  edit() { this.router.navigate(['edit'], { relativeTo: this.route }); }

  delete() {
    if (confirm('Excluir este insumo?')) {
      this.http.delete(`/materials/${this.material.id}`).subscribe(() => this.router.navigate(['/sinapi/materials']));
    }
  }

  addPrice() {
    const body = { ...this.newPrice, referenceMonth: this.newPrice.referenceMonth + '-01' };
    this.http.post(`/materials/${this.material.id}/prices`, body).subscribe({
      next: (res) => { this.material = res; this.showPriceForm = false; this.newPrice = { state: '', referenceMonth: '', price: 0, desonerated: false }; },
      error: () => {},
    });
  }
}
