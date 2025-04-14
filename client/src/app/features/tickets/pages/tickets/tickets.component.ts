import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { TicketService,
  MatchResponse,
  TicketReservationRequest,
  TicketPurchaseRequest,
  OrderResponse
} from '../../services/ticket.service';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mx-auto p-6">
      <h1 class="text-3xl font-bold mb-6">Billets CAN 2025</h1>

      <!-- Section des matchs -->
      <section class="mb-8">
        <h2 class="text-2xl font-semibold mb-4">Matchs à venir</h2>
        <div *ngIf="matches?.length; else noMatches" class="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div *ngFor="let match of matches" class="bg-white shadow-md rounded-lg p-4">
            <h3 class="text-xl font-bold">{{ match.matchTitle }}</h3>
            <p class="text-gray-600">{{ match.dateTime | date:'medium' }}</p>
            <p>Stade : {{ match.stadium }}</p>

            <div class="mt-4">
              <h4 class="font-semibold mb-2">Disponibilité des billets</h4>
              <div *ngFor="let section of getSections(match)" class="flex justify-between items-center mb-2">
                <span>{{ section.type }}: {{ section.count }} billets</span>
                <button
                  (click)="reserveTickets(match.id, section.type)"
                  class="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
                >
                  Réserver
                </button>
              </div>
            </div>
          </div>
        </div>
        <ng-template #noMatches>
          <p class="text-gray-500">Aucun match disponible actuellement.</p>
        </ng-template>
      </section>

      <!-- Section des réservations -->
      <section *ngIf="reservedTickets?.length" class="mb-8">
        <h2 class="text-2xl font-semibold mb-4">Billets réservés</h2>
        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div *ngFor="let ticket of reservedTickets" class="bg-white shadow-md rounded-lg p-4">
            <h3 class="text-xl font-bold">{{ ticket.matchTitle }}</h3>
            <p>Section : {{ ticket.sectionType }}</p>
            <p>Code : {{ ticket.ticketCode }}</p>
            <div class="flex space-x-2 mt-4">
              <button
                (click)="purchaseTickets([ticket.id])"
                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
              >
                Acheter
              </button>
              <button
                (click)="cancelReservation([ticket.id])"
                class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
              >
                Annuler
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Section des commandes -->
      <section *ngIf="orders?.length" class="mb-8">
        <h2 class="text-2xl font-semibold mb-4">Mes commandes</h2>
        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div *ngFor="let order of orders" class="bg-white shadow-md rounded-lg p-4">
            <h3 class="text-xl font-bold">Commande #{{ order.orderReference }}</h3>
            <p>Statut : {{ order.paymentStatus }}</p>
            <p>Montant : {{ order.totalAmount | currency:'EUR' }}</p>
            <p>Date : {{ order.createdAt | date:'medium' }}</p>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: []
})
export class TicketsComponent implements OnInit {
  matches: MatchResponse[] = [];
  reservedTickets: any[] = [];
  orders: OrderResponse[] = [];

  constructor(private ticketService: TicketService) {}

  ngOnInit() {
    this.loadUpcomingMatches();
    this.loadUserOrders();
  }

  loadUpcomingMatches() {
    this.ticketService.getUpcomingMatches().subscribe({
      next: (matches) => {
        this.matches = matches;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des matchs', error);
      }
    });
  }

  getSections(match: MatchResponse) {
    return Object.entries(match.availableTickets).map(([type, count]) => ({
      type,
      count
    }));
  }

  reserveTickets(matchId: number, sectionType: string) {
    const request: TicketReservationRequest = {
      matchId,
      sectionType,
      quantity: 1
    };

    this.ticketService.reserveTickets(request).subscribe({
      next: (tickets) => {
        this.reservedTickets = tickets;
        console.log('Billets réservés', tickets);
      },
      error: (error) => {
        console.error('Erreur de réservation', error);
      }
    });
  }

  purchaseTickets(ticketIds: number[]) {
    const request: TicketPurchaseRequest = {
      ticketIds,
      paymentInfo: 'PAIEMENT_SIMULÉ'
    };

    this.ticketService.purchaseTickets(request).subscribe({
      next: (order) => {
        console.log('Commande effectuée', order);
        this.loadUserOrders();
      },
      error: (error) => {
        console.error('Erreur d\'achat', error);
      }
    });
  }

  cancelReservation(ticketIds: number[]) {
    this.ticketService.cancelReservation(ticketIds).subscribe({
      next: () => {
        console.log('Réservation annulée');
        this.reservedTickets = this.reservedTickets.filter(
          ticket => !ticketIds.includes(ticket.id)
        );
      },
      error: (error) => {
        console.error('Erreur d\'annulation', error);
      }
    });
  }

  loadUserOrders() {
    this.ticketService.getUserOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des commandes', error);
      }
    });
  }
}
