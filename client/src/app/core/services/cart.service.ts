import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { OrderTicket } from '../models/ticket/order-ticket';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private ticketsSubject = new BehaviorSubject<OrderTicket[]>([]);
  tickets$ = this.ticketsSubject.asObservable();

  setTickets(tickets: OrderTicket[]): void {
    this.ticketsSubject.next(tickets);

    sessionStorage.setItem('cartTickets', JSON.stringify(tickets));
  }

  getTickets(): OrderTicket[] {
    // Vérifier d'abord en mémoire
    if (this.ticketsSubject.value.length > 0) {
      return this.ticketsSubject.value;
    }

    // Sinon, essayer de récupérer depuis sessionStorage
    const storedTickets = sessionStorage.getItem('cartTickets');
    if (storedTickets) {
      const tickets = JSON.parse(storedTickets);
      this.ticketsSubject.next(tickets);
      return tickets;
    }

    return [];
  }

  clearCart(): void {
    this.ticketsSubject.next([]);
    sessionStorage.removeItem('cartTickets');
  }
}
