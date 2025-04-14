import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../../../environments/environment";

// Types correspondant aux DTOs du backend
export interface MatchResponse {
  id: number;
  dateTime: string;
  stadium: string;
  homeTeam: string;
  awayTeam: string;
  phase: string;
  group: string;
  matchScore: number;
  availableTickets: { [key: string]: number };
  matchTitle: string;
  sectionPrices: { [key: string]: number };
}

export interface TicketReservationRequest {
  matchId: number;
  sectionType: string;
  quantity: number;
}

export interface TicketPurchaseRequest {
  ticketIds: number[];
  paymentInfo: string;
}

export interface TicketResponse {
  id: number;
  ticketCode: string;
  matchId: number;
  matchTitle: string;
  matchDateTime: string;
  sectionType: string;
  price: number;
  status: string;
  reservationTime?: string;
  expirationTime?: string;
}

export interface OrderResponse {
  id: number;
  orderReference: string;
  createdAt: string;
  paymentStatus: string;
  totalAmount: number;
  paymentReference?: string;
  paymentDate?: string;
  tickets: TicketResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;

  constructor(private http: HttpClient) {}

  getUpcomingMatches(): Observable<MatchResponse[]> {
    return this.http.get<MatchResponse[]>(`${this.apiUrl}/matches`);
  }

  getMatchById(id: number): Observable<MatchResponse> {
    return this.http.get<MatchResponse>(`${this.apiUrl}/matches/${id}`);
  }

  reserveTickets(request: TicketReservationRequest): Observable<TicketResponse[]> {
    return this.http.post<TicketResponse[]>(`${this.apiUrl}/reserve`, request);
  }

  purchaseTickets(request: TicketPurchaseRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.apiUrl}/purchase`, request);
  }

  getUserOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.apiUrl}/orders`);
  }

  getOrderById(id: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.apiUrl}/orders/${id}`);
  }

  cancelReservation(ticketIds: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/cancel-reservation`, ticketIds);
  }
}
