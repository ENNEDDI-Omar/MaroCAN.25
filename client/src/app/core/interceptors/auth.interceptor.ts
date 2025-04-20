import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, finalize } from 'rxjs/operators';
import { throwError, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);


  if (req.url.includes('/auth/login') ||
    req.url.includes('/auth/register') ||
    req.url.includes('/auth/refresh')) {
    return next(req);
  }

  const token = authService.getAccessToken();

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError(error => {
      // Si erreur 401 (Unauthorized) et utilisateur connecté
      if (error.status === 401 && authService.isLoggedIn()) {
        return handleUnauthorizedError(req, next, authService, router);
      }
      return throwError(() => error);
    })
  );
};

function handleUnauthorizedError(req: any, next: any, authService: AuthService, router: Router): Observable<any> {
  // Vérifier si la requête n'est pas déjà pour le rafraîchissement du token
  if (req.url.includes('/auth/refresh')) {
    authService.logout();
    router.navigate(['/auth/login']);
    return throwError(() => new Error('Session expirée'));
  }

  // Tenter de rafraîchir le token
  return authService.refreshToken().pipe(
    switchMap(() => {
      // Répéter la requête avec le nouveau token
      const newReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${authService.getAccessToken()}`
        }
      });
      return next(newReq);
    }),
    catchError(refreshError => {
      // Si le rafraîchissement échoue, déconnecter l'utilisateur
      authService.logout();
      router.navigate(['/auth/login']);
      return throwError(() => refreshError);
    })
  );
}
