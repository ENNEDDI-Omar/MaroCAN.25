import { Routes } from '@angular/router';
import { TicketsComponent } from './pages/tickets/tickets.component';
import { authGuard } from '../../core/guards/auth.guard';

export const TICKETS_ROUTES: Routes = [
  {
    path: '',
    component: TicketsComponent,
    canActivate: [authGuard],
    title: 'Billets CAN 2025'
  }
];
