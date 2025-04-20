// src/app/features/tickets/order-history/order-history.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TicketService } from '../../../core/services/ticket.service';
import { TicketOrder } from '../../../core/models/ticket/ticket-order';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-6">Mes Commandes</h1>

      <!-- État de chargement -->
      <div *ngIf="loading" class="text-center text-xl text-gray-600">
        Chargement de l'historique des commandes...
      </div>

      <!-- Erreur -->
      <div *ngIf="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
        {{ error }}
      </div>

      <!-- Liste des commandes -->
      <div *ngIf="!loading && orders.length" class="space-y-6">
        <div
          *ngFor="let order of orders"
          class="bg-white shadow-md rounded-lg p-6 hover:shadow-lg transition"
        >
          <div class="flex justify-between items-center mb-4">
            <div>
              <h2 class="text-xl font-semibold">
                Commande #{{ order.orderReference || 'N/A' }}
              </h2>
              <p class="text-gray-600">
                {{ order.createdAt | date:'medium' }}
              </p>
            </div>
            <div>
              <span
                [ngClass]="{
                  'bg-green-100 text-green-800': order.paymentStatus === 'COMPLETED',
                  'bg-yellow-100 text-yellow-800': order.paymentStatus === 'PENDING',
                  'bg-red-100 text-red-800': order.paymentStatus === 'FAILED'
                }"
                class="px-3 py-1 rounded-full text-sm"
              >
                {{ getStatusLabel(order.paymentStatus) }}
              </span>
            </div>
          </div>

          <!-- Résumé des billets -->
          <div class="mb-4">
            <div class="flex justify-between">
              <span>Nombre de billets</span>
              <span>{{ order.tickets.length }}</span>
            </div>
            <div class="flex justify-between font-bold">
              <span>Total</span>
              <span>{{ order.totalAmount }} MAD</span>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex space-x-4">
            <button
              *ngIf="order.id"
              (click)="viewOrderDetails(order.id)"
              class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
            >
              Détails
            </button>
            <button
              *ngIf="order.paymentStatus === 'PENDING'"
              (click)="resumePayment(order.id)"
              class="flex-1 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition"
            >
              Payer
            </button>
          </div>
        </div>
      </div>

      <!-- Aucune commande -->
      <div
        *ngIf="!loading && !orders.length"
        class="text-center py-12 bg-gray-100 rounded-lg"
      >
        <p class="text-xl text-gray-600 mb-4">
          Vous n'avez pas encore passé de commande
        </p>
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
export class OrderHistoryComponent implements OnInit {
  orders: TicketOrder[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private ticketService: TicketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrderHistory();
  }

  private loadOrderHistory(): void {
    this.ticketService.getUserOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Impossible de charger l\'historique des commandes';
        this.loading = false;
      }
    });
  }

  getStatusLabel(status: string): string {
    const statusLabels: { [key: string]: string } = {
      'COMPLETED': 'Payé',
      'PENDING': 'En attente',
      'FAILED': 'Échoué'
    };
    return statusLabels[status] || status;
  }

  viewOrderDetails(orderId?: number): void {
    if (orderId) {
      this.router.navigate(['/tickets/order/confirmation', orderId]);
    }
  }

  resumePayment(orderId?: number): void {
    if (orderId) {
      this.ticketService.initiatePayment(orderId).subscribe({
        next: (paymentInfo) => {
          window.location.href = paymentInfo.checkoutUrl;
        },
        error: (err) => {
          this.error = 'Impossible de reprendre le paiement';
        }
      });
    }
  }
}
