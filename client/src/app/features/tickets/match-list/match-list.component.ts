// src/app/features/tickets/match-list/match-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TicketService } from '../../../core/services/ticket.service';
import { Match } from '../../../core/models/ticket/match';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-match-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-6">Matchs disponibles - CAN 2025</h1>

      <!-- Filtres -->
      <div class="mb-6 flex flex-wrap gap-4">
        <!-- Note: Sans groupe dans le modèle, le filtre est désactivé
        <div class="text-gray-600">
          Filtrage par groupe actuellement non disponible
        </div>-->
      </div>

      <!-- Indicateur de chargement -->
      <div *ngIf="loading" class="text-center text-gray-600">
        Chargement des matchs...
      </div>

      <!-- Message d'erreur -->
      <div *ngIf="error" class="text-center text-red-500">
        {{ error }}
      </div>

      <!-- Liste des matchs -->
      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
            *ngFor="let match of paginatedMatches"
            class="bg-white shadow-md rounded-lg p-6 hover:shadow-lg transition"
        >
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-semibold">
              {{ match.homeTeam }} vs {{ match.awayTeam }}
            </h2>
            <span class="bg-green-100 text-green-800 px-2 py-1 rounded-full">
              Phase de groupes
            </span>
          </div>

          <div class="mb-4">
            <p class="flex items-center mb-2">
              <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              {{ formatDate(match.dateTime) }}
            </p>
            <p class="flex items-center">
              <svg class="w-5 h-5 mr-2 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              {{ match.stadium }}
            </p>
          </div>

          <div class="mb-4">
            <h3 class="text-lg font-medium mb-2">Billets disponibles</h3>
            <div class="grid grid-cols-2 gap-2">
              <div *ngFor="let section of ['CATEGORY_1', 'CATEGORY_2', 'CATEGORY_3', 'CATEGORY_4']"
                   class="flex justify-between items-center bg-gray-100 p-2 rounded">
                <span>{{ section }}</span>
                <span class="font-bold">
                  {{ match.availableTickets[section] || 0 }} billets
                </span>
              </div>
            </div>
          </div>

          <button
              [routerLink]="['/tickets/match', match.id, 'reserve']"
              class="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition"
          >
            Réserver des billets
          </button>
        </div>
      </div>

      <!-- Pagination -->
      <div *ngIf="totalPages > 1" class="mt-8 flex justify-center space-x-2">
        <button
            *ngFor="let page of pageArray"
            (click)="changePage(page)"
            [class.bg-blue-500]="currentPage === page"
            [class.text-white]="currentPage === page"
            class="px-4 py-2 border rounded"
        >
          {{ page + 1 }}
        </button>
      </div>

      <!-- Message si aucun match -->
      <div *ngIf="paginatedMatches.length === 0 && !loading" class="text-center py-12">
        <p class="text-xl text-gray-600">Aucun match disponible.</p>
      </div>
    </div>
  `
})
export class MatchListComponent implements OnInit {
  // Données originales
  allMatches: Match[] = [];

  // Données filtrées et paginées
  paginatedMatches: Match[] = [];

  // États
  loading = true;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 6;
  totalPages = 0;

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.loadMatches();
  }

  loadMatches(): void {
    this.loading = true;
    this.ticketService.getAllUpcomingMatches().pipe(
        catchError(err => {
          this.error = 'Impossible de charger les matchs. Veuillez réessayer.';
          this.loading = false;
          return of([]);
        })
    ).subscribe(matches => {
      this.allMatches = matches;
      this.updatePaginatedMatches();
      this.loading = false;
    });
  }

  updatePaginatedMatches(): void {
    const startIndex = this.currentPage * this.pageSize;
    this.paginatedMatches = this.allMatches.slice(
        startIndex,
        startIndex + this.pageSize
    );

    // Calculer le nombre total de pages
    this.totalPages = Math.ceil(this.allMatches.length / this.pageSize);
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.updatePaginatedMatches();
    }
  }

  // Getter pour générer les numéros de page
  get pageArray(): number[] {
    return Array(this.totalPages).fill(0).map((_, i) => i);
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
