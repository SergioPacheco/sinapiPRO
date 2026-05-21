import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { PageHeader } from '@shared';
import { RegistryService } from '../services/registry.service';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    PageHeader,
  ],
  templateUrl: './client-form.html',
})
export class ClientFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);
  private readonly service = inject(RegistryService);

  isEditMode = signal(false);
  clientId = signal<string | null>(null);
  loading = signal(false);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    document: ['', Validators.required],
    email: ['', Validators.email],
    phone: [''],
    address: [''],
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.clientId.set(id);
      this.loadClient(id);
    }
  }

  private loadClient(id: string) {
    this.loading.set(true);
    this.service.getClient(id).subscribe({
      next: (client) => {
        this.form.patchValue(client);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  submit() {
    if (this.form.invalid) return;
    const data = this.form.getRawValue();
    const request$ = this.isEditMode()
      ? this.service.updateClient(this.clientId()!, data)
      : this.service.createClient(data);
    request$.subscribe(() => this.router.navigate(['/registry/clients']));
  }
}
