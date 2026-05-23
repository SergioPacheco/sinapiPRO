import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { EquipmentService } from '../services/equipment.service';

@Component({
  selector: 'app-equipment-form',
  templateUrl: './equipment-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, PageHeader],
})
export class EquipmentFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(EquipmentService);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);

  isEdit = false;
  private id = '';

  form = this.fb.nonNullable.group({
    code: ['', Validators.required],
    name: ['', Validators.required],
    type: ['OWNED' as string, Validators.required],
    status: ['AVAILABLE' as string, Validators.required],
    hourlyRate: [0, [Validators.required, Validators.min(0)]],
  });

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.isEdit = true;
      this.service.getById(this.id).subscribe(e => this.form.patchValue(e));
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.router.navigate(['/equipment']);
  }
}
