import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-settings-list',
  templateUrl: './settings-list.html',
  imports: [MatCardModule, PageHeader],
})
export class SettingsListComponent {}
