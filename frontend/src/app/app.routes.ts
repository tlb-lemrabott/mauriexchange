import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/shell.component').then(m => m.ShellComponent),
    children: [
      { path: '', pathMatch: 'full', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
      { path: 'live-rates', loadComponent: () => import('./features/live-rates/live-rates.component').then(m => m.LiveRatesComponent) },
      { path: 'converter', loadComponent: () => import('./features/converter/converter.component').then(m => m.ConverterComponent) },
      { path: 'historical', loadComponent: () => import('./features/historical/historical.component').then(m => m.HistoricalComponent) },
      { path: 'offices', loadComponent: () => import('./features/offices/offices.component').then(m => m.OfficesComponent) },
      { path: 'about', loadComponent: () => import('./features/about/about.component').then(m => m.AboutComponent) },
    ]
  },
  { path: '**', redirectTo: '' }
];
