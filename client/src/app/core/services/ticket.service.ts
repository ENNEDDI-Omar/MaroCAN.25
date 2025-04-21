import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import { Match } from '../models/ticket/match';
import { TicketReservation } from '../models/ticket/ticket-reservation';
import { OrderTicket } from '../models/ticket/order-ticket';
import { TicketOrder } from '../models/ticket/ticket-order';
import { environment } from '../../../environments/environment';
import {catchError} from "rxjs/operators";


const API_ENDPOINTS = {
  // Endpoints relatifs aux tickets
  TICKETS_BASE: '/tickets',
  MATCHES: '/tickets/matches',
  MATCH_DETAILS: '/tickets/matches/',
  RESERVE: '/tickets/reserve',

  // Endpoints relatifs aux commandes
  ORDERS_BASE: '/orders',
  CHECKOUT: '/orders/checkout',
  COMPLETE: '/orders/complete',
  ORDER_DETAILS: '/orders/',
  TICKETS_PDF: '/orders/{orderId}/tickets/pdf'
};

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  constructor(private http: HttpClient) {}

  getUpcomingMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${environment.apiUrl}${API_ENDPOINTS.MATCHES}`);
  }

  getMatchDetails(matchId: number): Observable<Match> {
    return this.http.get<Match>(`${environment.apiUrl}${API_ENDPOINTS.MATCH_DETAILS}${matchId}`);
  }

  reserveTickets(reservation: TicketReservation): Observable<OrderTicket[]> {
    return this.http.post<OrderTicket[]>(`${environment.apiUrl}${API_ENDPOINTS.RESERVE}`, reservation);
  }

  // Méthodes pour les commandes et paiements
  createOrder(orderData: {
    ticketIds: number[];
    paymentInfo?: string
  }): Observable<TicketOrder> {
    return this.http.post<TicketOrder>(
        `${environment.apiUrl}${API_ENDPOINTS.CHECKOUT}`,
        {
          ticketIds: orderData.ticketIds,
          paymentInfo: orderData.paymentInfo || 'ONLINE_PAYMENT'
        }
    ).pipe(
        catchError(error => {
          console.error('Erreur de création de commande', error);
          return throwError(() => new Error('Impossible de créer la commande'));
        })
    );
  }

  initiatePayment(orderId: number): Observable<{sessionId: string, checkoutUrl: string}> {
    return this.http.post<{sessionId: string, checkoutUrl: string}>(
      `${environment.apiUrl}${API_ENDPOINTS.CHECKOUT}`, { orderId }
    );
  }

  completePayment(sessionId: string): Observable<TicketOrder> {
    return this.http.get<TicketOrder>(
        `${environment.apiUrl}/orders/complete?sessionId=${sessionId}`
    ).pipe(
        catchError(error => {
          console.error('Erreur de confirmation de paiement', error);
          return throwError(() => new Error('Impossible de confirmer le paiement'));
        })
    );
  }

  getUserOrders(): Observable<TicketOrder[]> {
    return this.http.get<TicketOrder[]>(`${environment.apiUrl}${API_ENDPOINTS.ORDERS_BASE}`);
  }

  getOrderDetails(orderId: number): Observable<TicketOrder> {
    return this.http.get<TicketOrder>(`${environment.apiUrl}${API_ENDPOINTS.ORDER_DETAILS}${orderId}`);
  }

  downloadTicketsPDF(orderId: number): Observable<Blob> {
    return this.http.get(`${environment.apiUrl}/orders/${orderId}/tickets/pdf`, {
      responseType: 'blob'
    }).pipe(
        catchError(error => {
          console.error('Erreur lors du téléchargement des billets', error);
          return throwError(() => new Error('Impossible de télécharger les billets'));
        })
    );
  }
}
