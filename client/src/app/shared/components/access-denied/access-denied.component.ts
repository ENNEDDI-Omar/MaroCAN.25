import { Component } from '@angular/core';
import {CommonModule} from "@angular/common";
import {RouterModule} from "@angular/router";

// access-denied.component.ts
@Component({
  selector: 'app-access-denied',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
      <div class="text-center p-8 bg-white rounded-lg shadow-md">
        <h1 class="text-3xl font-bold text-red-600 mb-4">Accès refusé</h1>
        <p class="text-lg text-gray-700 mb-6">
          Vous n'avez pas les permissions nécessaires pour accéder à cette page.
        </p>
        <button
          routerLink="/home"
          class="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition"
        >
          Retour à l'accueil
        </button>
      </div>
    </div>
  `
})
export class AccessDeniedComponent {}
