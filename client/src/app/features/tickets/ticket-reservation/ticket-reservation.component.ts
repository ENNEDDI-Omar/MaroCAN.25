import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TicketService } from '../../../core/services/ticket.service';
import { Match } from '../../../core/models/ticket/match';
import { TicketReservation } from '../../../core/models/ticket/ticket-reservation';
import { Observable, switchMap, catchError, of } from 'rxjs';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-ticket-reservation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <!-- Chargement -->
      <div *ngIf="loading" class="text-center text-xl text-gray-600">
        Chargement des détails du match...
      </div>

      <!-- Erreur -->
      <div *ngIf="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
        {{ error }}
      </div>

      <!-- Formulaire de réservation -->
      <div *ngIf="match && !loading" class="max-w-2xl mx-auto bg-white shadow-md rounded-lg p-8">
        <h2 class="text-2xl font-bold mb-6">
          Réservation de billets : {{ match.homeTeam }} vs {{ match.awayTeam }}
        </h2>

        <form [formGroup]="reservationForm!" (ngSubmit)="onReserve()" class="space-y-6">
          <!-- Sélection de la section -->
          <div>
            <label class="block text-gray-700 font-bold mb-2">
              Catégorie de billets
            </label>
            <div class="grid grid-cols-2 gap-4">
              <ng-container *ngFor="let section of sections">
                <div
                  *ngIf="match.availableTickets[section] > 0"
                  class="relative"
                  [class.border-green-500]="reservationForm.get('sectionType')?.value === section"
                >
                  <input
                    type="radio"
                    [id]="section"
                    [value]="section"
                    formControlName="sectionType"
                    class="hidden"
                  >
                  <label
                    [for]="section"
                    class="block border rounded-lg p-4 cursor-pointer transition
                           hover:bg-green-50
                           [&:has(input:checked)]:border-green-500
                           [&:has(input:checked)]:bg-green-50"
                  >
                    <div class="flex justify-between items-center">
                      <span>{{ section }}</span>
                      <span class="font-bold">
                        {{ match.availableTickets[section] }} billets disponibles
                      </span>
                    </div>
                    <span class="text-sm text-gray-500">
                      Prix : {{ match.sectionPrices[section] }} MAD
                    </span>
                  </label>
                </div>
              </ng-container>
            </div>
            <div
              *ngIf="reservationForm.get('sectionType')?.invalid && reservationForm.get('sectionType')?.touched"
              class="text-red-500 text-sm mt-1"
            >
              Veuillez sélectionner une catégorie de billets
            </div>
          </div>

          <!-- Quantité de billets -->
          <div>
            <label class="block text-gray-700 font-bold mb-2">
              Nombre de billets
            </label>
            <div class="flex items-center">
              <button
                type="button"
                (click)="decreaseQuantity()"
                class="bg-gray-200 px-3 py-1 rounded-l"
                [disabled]="reservationForm.get('quantity')?.value <= 1"
              >
                -
              </button>
              <input
                type="number"
                formControlName="quantity"
                class="w-16 text-center border py-1"
                readonly
              >
              <button
                type="button"
                (click)="increaseQuantity()"
                class="bg-gray-200 px-3 py-1 rounded-r"
                [disabled]="reservationForm.get('quantity')?.value >= 5 ||
                           reservationForm.get('quantity')?.value > (match.availableTickets[reservationForm.get('sectionType')?.value || ''] || 0)"
              >
                +
              </button>
            </div>
            <div
              *ngIf="reservationForm.get('quantity')?.invalid && reservationForm.get('quantity')?.touched"
              class="text-red-500 text-sm mt-1"
            >
              Quantité invalide (1-5 billets)
            </div>
          </div>

          <!-- Total -->
          <div class="bg-gray-100 p-4 rounded-lg">
            <div class="flex justify-between">
              <span>Total billets</span>
              <span>{{ reservationForm.get('quantity')?.value || 0 }}</span>
            </div>
            <div class="flex justify-between font-bold">
              <span>Prix total</span>
              <span>
                {{ calculateTotal() }} MAD
              </span>
            </div>
          </div>

          <!-- Bouton de réservation -->
          <button
            type="submit"
            [disabled]="reservationForm.invalid"
            class="w-full bg-green-600 text-white py-2 rounded-lg
                   hover:bg-green-700 transition
                   disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            Réserver
          </button>
        </form>
      </div>
    </div>
  `
})
export class TicketReservationComponent implements OnInit {
  match: Match | null = null;
  loading = true;
  error: string | null = null;
  reservationForm!: FormGroup;
  sections = ['CATEGORY_1', 'CATEGORY_2', 'CATEGORY_3', 'CATEGORY_4'];

  constructor(
    private route: ActivatedRoute,
    private ticketService: TicketService,
    private fb: FormBuilder,
    private router: Router,
  private cartService: CartService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadMatchDetails();
  }

  private initForm(): void {
    this.reservationForm = this.fb.group({
      sectionType: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1), Validators.max(5)]]
    });
  }

  private loadMatchDetails(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const matchId = Number(params.get('id'));
        return this.ticketService.getMatchDetails(matchId);
      }),
      catchError(err => {
        this.error = 'Impossible de charger les détails du match';
        this.loading = false;
        return of(null);
      })
    ).subscribe(match => {
      if (match) {
        this.match = match;
        this.loading = false;
      }
    });
  }

  increaseQuantity(): void {
    const sectionControl = this.reservationForm.get('sectionType');
    const quantityControl = this.reservationForm.get('quantity');

    if (sectionControl && quantityControl && this.match) {
      const currentQuantity = quantityControl.value;
      const sectionType = sectionControl.value;

      if (currentQuantity < 5 &&
        currentQuantity < this.match.availableTickets[sectionType]) {
        quantityControl.setValue(currentQuantity + 1);
      }
    }
  }

  decreaseQuantity(): void {
    const quantityControl = this.reservationForm.get('quantity');
    if (quantityControl) {
      const currentQuantity = quantityControl.value;
      if (currentQuantity > 1) {
        quantityControl.setValue(currentQuantity - 1);
      }
    }
  }

  calculateTotal(): number {
    if (!this.match || !this.reservationForm.valid) return 0;

    const sectionType = this.reservationForm.get('sectionType')?.value;
    const quantity = this.reservationForm.get('quantity')?.value || 0;

    return sectionType ? this.match.sectionPrices[sectionType] * quantity : 0;
  }

  onReserve(): void {
    if (this.reservationForm.valid && this.match) {
      const sectionType = this.reservationForm.get('sectionType')?.value;
      const quantity = this.reservationForm.get('quantity')?.value;

      if (sectionType && quantity) {
        const reservation: TicketReservation = {
          matchId: this.match.id,
          sectionType: sectionType,
          quantity: quantity
        };

        this.ticketService.reserveTickets(reservation).subscribe({
          next: (tickets) => {
            // Utiliser CartService au lieu de state
            this.cartService.setTickets(tickets);
            this.router.navigate(['/tickets/order/cart']);
          },
          error: (err) => {
            this.error = 'La réservation a échoué. Veuillez réessayer.';
          }
        });
      }
    }
  }
}
