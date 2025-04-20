// src/app/features/tickets/order-cart/order-cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TicketService } from '../../../core/services/ticket.service';
import { OrderTicket } from '../../../core/models/ticket/order-ticket';

@Component({
  selector: 'app-order-cart',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-6">Votre Panier</h1>

      <!-- Liste des billets réservés -->
      <div *ngIf="tickets.length" class="grid md:grid-cols-2 gap-6">
        <!-- Colonne Billets -->
        <div class="space-y-4">
          <div *ngFor="let ticket of tickets; let i = index" class="bg-white shadow-md rounded-lg p-6">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-semibold">{{ ticket.matchTitle }}</h2>
              <span class="badge bg-green-100 text-green-800 px-2 py-1 rounded-full">
                {{ ticket.sectionType }}
              </span>
            </div>
            <div class="flex justify-between">
              <span>Prix</span>
              <span class="font-bold">{{ ticket.price }} MAD</span>
            </div>
            <button
              (click)="removeTicket(i)"
              class="mt-4 w-full bg-red-500 text-white py-2 rounded-lg hover:bg-red-600"
            >
              Supprimer
            </button>
          </div>
        </div>

        <!-- Colonne Récapitulatif -->
        <div class="bg-white shadow-md rounded-lg p-6">
          <h3 class="text-2xl font-bold mb-6">Récapitulatif</h3>

          <div class="space-y-4">
            <div class="flex justify-between">
              <span>Nombre de billets</span>
              <span>{{ tickets.length }}</span>
            </div>
            <div class="flex justify-between">
              <span>Total</span>
              <span class="font-bold text-xl">{{ calculateTotal() }} MAD</span>
            </div>
          </div>

          <div class="mt-6">
            <button
              (click)="proceedToPayment()"
              [disabled]="!tickets.length"
              class="w-full bg-green-600 text-white py-3 rounded-lg
                     hover:bg-green-700 transition
                     disabled:bg-gray-300 disabled:cursor-not-allowed"
            >
              Procéder au paiement
            </button>
          </div>
        </div>
      </div>

      <!-- Panier vide -->
      <div *ngIf="!tickets.length" class="text-center py-12">
        <p class="text-xl text-gray-600 mb-4">Votre panier est vide</p>
        <button
          routerLink="/tickets"
          class="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700"
        >
          Réserver des billets
        </button>
      </div>
    </div>
  `
})
export class OrderCartComponent implements OnInit {
  tickets: OrderTicket[] = [];

  constructor(
    private router: Router,
    private ticketService: TicketService
  ) {}

  ngOnInit(): void {
    // Récupérer les tickets depuis l'état de navigation
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      this.tickets = navigation.extras.state['tickets'] || [];
    }
  }

  removeTicket(index: number): void {
    this.tickets.splice(index, 1);
  }

  calculateTotal(): number {
    return this.tickets.reduce((total, ticket) => total + ticket.price, 0);
  }

  proceedToPayment(): void {
    if (this.tickets.length) {
      // Créer une commande avec les tickets
      this.ticketService.createOrder(this.tickets).subscribe({
        next: (order) => {
          // Naviguer vers la page de confirmation avec l'ID de commande
          this.router.navigate(['/tickets/order/confirmation', order.id]);
        },
        error: (err) => {
          // Gestion des erreurs
          console.error('Erreur lors de la création de la commande', err);
        }
      });
    }
  }
}
