import { Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { PageHeader } from '@shared';
import { Project, ProjectService } from '../../project/services/project.service';
import { Employee } from '../models/registry.model';
import { RegistryService } from '../services/registry.service';

@Component({
  selector: 'app-team-form',
  templateUrl: './team-form.html',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    PageHeader,
  ],
})
export class TeamFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly registryService = inject(RegistryService);
  private readonly projectService = inject(ProjectService);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);

  isEdit = false;
  private id = '';
  projects: Project[] = [];
  employees: Employee[] = [];

  readonly form = this.fb.group({
    name: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(100)]),
    description: this.fb.nonNullable.control(''),
    projectId: this.fb.control<string | null>(null),
    members: this.fb.array([]),
  });

  get members(): FormArray {
    return this.form.controls.members;
  }

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    this.isEdit = !!this.id;

    this.projectService.list(0, 200).subscribe(response => this.projects = response.content);
    this.registryService.listEmployees(undefined, 0, 200).subscribe(response => this.employees = response.content);

    if (this.isEdit) {
      this.registryService.getTeam(this.id).subscribe(team => {
        this.form.patchValue({
          name: team.name,
          description: team.description,
          projectId: team.projectId,
        });
        team.members.forEach(member => this.addMember(member.employeeId, member.role));
      });
    } else {
      this.addMember();
    }
  }

  addMember(employeeId = '', role = '') {
    this.members.push(this.fb.group({
      employeeId: this.fb.nonNullable.control(employeeId, Validators.required),
      role: this.fb.nonNullable.control(role, Validators.required),
    }));
  }

  removeMember(index: number) {
    this.members.removeAt(index);
  }

  employeeLabel(employeeId: string) {
    const employee = this.employees.find(item => item.id === employeeId);
    return employee ? `${employee.employeeCode} - ${employee.name}` : 'Selecione';
  }

  fillRole(index: number) {
    const row = this.members.at(index);
    const employeeId = row.get('employeeId')?.value;
    const employee = this.employees.find(item => item.id === employeeId);
    if (employee && !row.get('role')?.value) {
      row.get('role')?.setValue(employee.role);
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const payload = {
      ...this.form.getRawValue(),
      members: this.members.getRawValue(),
    };
    const request$ = this.isEdit
      ? this.registryService.updateTeam(this.id, payload)
      : this.registryService.createTeam(payload);
    request$.subscribe(() => this.router.navigate(['/registry/teams']));
  }
}
