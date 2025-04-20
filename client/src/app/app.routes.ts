import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { LandingPageComponent } from './features/landing-page/landing-page/landing-page.component';
import { authGuard } from './core/guards/auth.guard';
import {RoleGuard} from "./core/guards/role.guard";

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'home',
        loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule)
      },
      {
        path: 'tickets',
        canActivate: [authGuard, RoleGuard],
        data: { roles: ['USER', 'ADMIN'] },
        loadChildren: () => import('./features/tickets/tickets.module').then(m => m.TicketsModule)
      }
    ]
  },
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { path: '**', redirectTo: '' }
];
