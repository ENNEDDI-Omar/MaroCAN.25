// src/app/core/guards/ticket-reservation.guard.ts
import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TicketReservationGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    // Vérifier si l'utilisateur est connecté
    if (!this.authService.isLoggedIn()) {
      // Rediriger vers la page de connexion
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: state.url }
      });
      return of(false);
    }

    // Autres vérifications spécifiques à la réservation de billets
    const matchId = route.paramMap.get('id');

    // Vous pouvez ajouter d'autres logiques ici
    // Par exemple, vérifier la disponibilité des billets

    return of(true);
  }
}
