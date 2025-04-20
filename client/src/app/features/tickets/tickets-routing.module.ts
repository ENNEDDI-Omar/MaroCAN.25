import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { authGuard } from '../../core/guards/auth.guard';
import { TicketReservationGuard } from '../../core/guards/ticket-reservation.guard';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./match-list/match-list.component').then(m => m.MatchListComponent)
  },
  {
    path: 'match/:id/reserve',
    loadComponent: () => import('./ticket-reservation/ticket-reservation.component').then(m => m.TicketReservationComponent),
    canActivate: [TicketReservationGuard]
  },
  {
    path: 'order/cart',
    loadComponent: () => import('./order-cart/order-cart.component').then(m => m.OrderCartComponent),
    canActivate: [authGuard]
  },
  {
    path: 'order/confirmation/:orderId',
    loadComponent: () => import('./order-confirmation/order-confirmation.component').then(m => m.OrderConfirmationComponent),
    canActivate: [authGuard]
  },
  {
    path: 'orders',
    loadComponent: () => import('./order-history/order-history.component').then(m => m.OrderHistoryComponent),
    canActivate: [authGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TicketsRoutingModule { }
