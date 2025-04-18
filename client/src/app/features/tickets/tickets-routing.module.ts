// src/app/features/tickets/tickets-routing.module.ts
import { Routes } from '@angular/router';
import { MatchListComponent } from './match-list/match-list.component';
import { TicketReservationComponent } from './ticket-reservation/ticket-reservation.component';
import { OrderCartComponent } from './order-cart/order-cart.component';
import { OrderConfirmationComponent } from './order-confirmation/order-confirmation.component';
import { OrderHistoryComponent } from './order-history/order-history.component';
import { authGuard } from '../../core/guards/auth.guard';

export const TICKETS_ROUTES: Routes = [
  {
    path: '',
    component: MatchListComponent
  },
  {
    path: 'match/:id/reserve',
    component: TicketReservationComponent,
    canActivate: [authGuard]
  },
  {
    path: 'order/cart',
    component: OrderCartComponent,
    canActivate: [authGuard]
  },
  {
    path: 'order/confirmation/:orderId',
    component: OrderConfirmationComponent,
    canActivate: [authGuard]
  },
  {
    path: 'orders',
    component: OrderHistoryComponent,
    canActivate: [authGuard]
  }
];
