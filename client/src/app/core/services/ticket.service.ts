import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/ticket/match';
import { TicketReservation } from '../models/ticket/ticket-reservation';
import { OrderTicket } from '../models/ticket/order-ticket';
import { TicketOrder } from '../models/ticket/ticket-order';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;

  constructor(private http: HttpClient) {}

  getUpcomingMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiUrl}/matches`);
  }

  getMatchDetails(matchId: number): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/matches/${matchId}`);
  }

  reserveTickets(reservation: TicketReservation): Observable<OrderTicket[]> {
    return this.http.post<OrderTicket[]>(`${this.apiUrl}/reserve`, reservation);
  }

  createOrder(tickets: OrderTicket[]): Observable<TicketOrder> {
    return this.http.post<TicketOrder>(`${this.apiUrl}/orders`, { tickets });
  }

  getUserOrders(): Observable<TicketOrder[]> {
    return this.http.get<TicketOrder[]>(`${this.apiUrl}/orders`);
  }

  initiatePayment(orderId: number): Observable<{sessionId: string, checkoutUrl: string}> {
    return this.http.post<{sessionId: string, checkoutUrl: string}>(`${this.apiUrl}/orders/checkout`, { orderId });
  }


  getOrderDetails(orderId: number): Observable<TicketOrder> {
    return this.http.get<TicketOrder>(`${this.apiUrl}/orders/${orderId}`);
  }

  downloadTicketsPDF(orderId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/orders/${orderId}/tickets/pdf`, {
      responseType: 'blob'
    });
  }
}
