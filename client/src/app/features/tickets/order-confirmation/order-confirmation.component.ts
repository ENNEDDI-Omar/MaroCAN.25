// src/app/features/tickets/order-confirmation/order-confirmation.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketService } from '../../../core/services/ticket.service';
import { TicketOrder } from '../../../core/models/ticket/ticket-order';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <!-- État de chargement -->
      <div *ngIf="loading" class="text-center text-xl text-gray-600">
        Chargement de la confirmation de commande...
      </div>

      <!-- Erreur -->
      <div *ngIf="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
        {{ error }}
      </div>

      <!-- Confirmation de commande -->
      <div *ngIf="order && !loading" class="max-w-2xl mx-auto">
        <div class="bg-white shadow-lg rounded-lg p-8">
          <div class="text-center mb-6">
            <svg class="mx-auto mb-4 w-16 h-16 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h1 class="text-3xl font-bold text-gray-800">Commande confirmée</h1>
            <p class="text-gray-600 mt-2">Merci pour votre achat !</p>
          </div>

          <!-- Détails de la commande -->
          <div class="bg-gray-100 rounded-lg p-6 mb-6">
            <div class="flex justify-between mb-4">
              <span class="font-semibold">Référence de commande</span>
              <span>{{ order.orderReference || 'N/A' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="font-semibold">Date</span>
              <span>{{ order.createdAt ? (order.createdAt | date:'medium') : 'Date non disponible' }}</span>
            </div>
          </div>

          <!-- Liste des billets -->
          <div class="space-y-4 mb-6">
            <h2 class="text-xl font-bold mb-4">Détails des billets</h2>
            <div *ngFor="let ticket of order.tickets" class="bg-white border rounded-lg p-4">
              <div class="flex justify-between items-center">
                <div>
                  <h3 class="font-semibold">{{ ticket.matchTitle }}</h3>
                  <p class="text-gray-600">{{ ticket.sectionType }}</p>
                </div>
                <div class="text-right">
                  <span class="font-bold">{{ ticket.price }} MAD</span>
                  <p class="text-sm text-gray-500">Code: {{ ticket.ticketCode }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Récapitulatif -->
          <div class="bg-gray-100 rounded-lg p-6">
            <div class="flex justify-between mb-4">
              <span>Nombre de billets</span>
              <span>{{ order.tickets.length }}</span>
            </div>
            <div class="flex justify-between font-bold text-xl">
              <span>Total</span>
              <span>{{ order.totalAmount }} MAD</span>
            </div>
          </div>

          <!-- Actions -->
          <div class="mt-6 flex space-x-4">
            <button
              (click)="downloadTickets()"
              class="flex-1 bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition"
            >
              Télécharger les billets
            </button>
            <button
              routerLink="/tickets/orders"
              class="flex-1 bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 transition"
            >
              Mes commandes
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class OrderConfirmationComponent implements OnInit {
  order: TicketOrder | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private ticketService: TicketService,
    private router: Router
  ) {}


  ngOnInit(): void {
    // Vérifier d'abord la présence d'un session_id dans les paramètres de l'URL
    this.route.queryParams.subscribe(params => {
      const sessionId = params['session_id'];

      if (sessionId) {
        // Si un session_id est présent, confirmer le paiement
        this.confirmPayment(sessionId);
      } else {
        // Sinon, essayer de charger la commande depuis l'ID
        const orderId = Number(this.route.snapshot.paramMap.get('orderId'));
        if (orderId) {
          this.loadOrderConfirmation(orderId);  // Utiliser le nom de méthode correct
        } else {
          // Ni session_id ni orderId trouvé, rediriger vers la liste des commandes
          this.router.navigate(['/tickets/orders']);
        }
      }
    });
  }

  private confirmPayment(sessionId: string): void {
    this.loading = true;
    this.ticketService.completePayment(sessionId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
        // Mettre à jour l'URL pour inclure l'ID de commande
        this.router.navigate(
            ['/tickets/order/confirmation', order.id],
            { replaceUrl: true }
        );
      },
      error: (err) => {
        this.error = 'Impossible de confirmer le paiement: ' + err.message;
        this.loading = false;
      }
    });
  }

  private loadOrderConfirmation(orderId: number): void {
    this.ticketService.getOrderDetails(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Impossible de charger les détails de la commande';
        this.loading = false;
        this.router.navigate(['/tickets']);
      }
    });
  }

  downloadTickets(): void {
    if (this.order?.id) {
      this.ticketService.downloadTicketsPDF(this.order.id).subscribe({
        next: (pdfBlob) => {
          // Créer une URL pour le blob
          const url = window.URL.createObjectURL(pdfBlob);

          // Créer un lien temporaire et déclencher le téléchargement
          const link = document.createElement('a');
          link.href = url;
          link.download = `billets_${this.order?.orderReference || 'commande'}.pdf`;
          document.body.appendChild(link);
          link.click();

          // Nettoyer
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Erreur lors du téléchargement', err);
          this.error = 'Impossible de télécharger les billets. Veuillez réessayer plus tard.';
        }
      });
    }
  }
}
