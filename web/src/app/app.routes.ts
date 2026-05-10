import { Routes } from '@angular/router';
import { authGuard } from '@core';
import { AdminLayout } from '@theme/admin-layout/admin-layout';
import { AuthLayout } from '@theme/auth-layout/auth-layout';
import { Dashboard } from './routes/dashboard/dashboard';
import { Error403 } from './routes/sessions/error-403';
import { Error404 } from './routes/sessions/error-404';
import { Error500 } from './routes/sessions/error-500';
import { Login } from './routes/sessions/login/login';
import { Register } from './routes/sessions/register/register';

export const routes: Routes = [
  {
    path: '',
    component: AdminLayout,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: '403', component: Error403 },
      { path: '404', component: Error404 },
      { path: '500', component: Error500 },
      {
        path: 'projects',
        loadChildren: () => import('./routes/project/project.routes').then(m => m.routes),
      },
      {
        path: 'sinapi',
        loadChildren: () => import('./routes/sinapi/sinapi.routes').then(m => m.routes),
      },
      {
        path: 'equipment',
        loadChildren: () => import('./routes/equipment/equipment.routes').then(m => m.routes),
      },
      {
        path: 'analytics',
        loadChildren: () => import('./routes/analytics/analytics.routes').then(m => m.routes),
      },
      {
        path: 'suppliers',
        loadChildren: () => import('./routes/supplier/supplier.routes').then(m => m.routes),
      },
      {
        path: 'safety',
        loadChildren: () => import('./routes/safety/safety.routes').then(m => m.routes),
      },
      {
        path: 'settings',
        loadChildren: () => import('./routes/settings/settings.routes').then(m => m.routes),
      },
      {
        path: 'profile',
        loadChildren: () => import('./routes/profile/profile.routes').then(m => m.routes),
      },
      {
        path: 'commercial',
        loadChildren: () => import('./routes/commercial/commercial.routes').then(m => m.routes),
      },
      {
        path: 'after-sales',
        loadChildren: () => import('./routes/aftersales/aftersales.routes').then(m => m.routes),
      },
      {
        path: 'registry',
        loadChildren: () => import('./routes/registry/registry.routes').then(m => m.routes),
      },
    ],
  },
  {
    path: 'auth',
    component: AuthLayout,
    children: [
      { path: 'login', component: Login },
      { path: 'register', component: Register },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
