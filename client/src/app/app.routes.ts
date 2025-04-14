import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { LandingPageComponent } from './features/landing-page/landing-page/landing-page.component';
import { TICKETS_ROUTES } from './features/tickets/tickets-routing.module';

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
        children: TICKETS_ROUTES
      }
    ]
  },
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  { path: '**', redirectTo: '' }
];
