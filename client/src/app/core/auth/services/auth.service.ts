import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, BehaviorSubject, throwError, finalize} from 'rxjs';
import { tap, catchError, switchMap} from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User
} from '../../../features/auth/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;


  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private errorSubject = new BehaviorSubject<string | null>(null);
  private refreshingToken = false;
  private refreshTokenTimeout: any;

  currentUser$ = this.currentUserSubject.asObservable();
  loading$ = this.loadingSubject.asObservable();
  error$ = this.errorSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Charger l'utilisateur depuis le localStorage au démarrage
    const userStr = localStorage.getItem('user');
    if (userStr) {
      this.currentUserSubject.next(JSON.parse(userStr));
    }

    // Démarrer le timer de rafraîchissement du token
    this.startRefreshTokenTimer();
  }

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginRequest)
      .pipe(
        tap({
          next: (response) => {
            this.saveTokens(response.accessToken, response.refreshToken);
            const user = this.getUserFromToken(response.accessToken);
            this.saveUser(user);
            this.currentUserSubject.next(user);
            this.loadingSubject.next(false);
            this.startRefreshTokenTimer();
            this.router.navigate(['/home']);
          },
          error: (error) => {
            const errorMessage = error.error?.message || 'Échec de connexion. Veuillez réessayer.';
            this.errorSubject.next(errorMessage);
            this.loadingSubject.next(false);
          }
        })
      );
  }

  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);

    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, registerRequest)
      .pipe(
        tap({
          next: (response) => {
            this.saveTokens(response.accessToken, response.refreshToken);
            const user = this.getUserFromToken(response.accessToken);
            this.saveUser(user);
            this.currentUserSubject.next(user);
            this.loadingSubject.next(false);
            this.startRefreshTokenTimer();
            this.router.navigate(['/home']);
          },
          error: (error) => {
            const errorMessage = error.error?.message || 'Échec d\'inscription. Veuillez réessayer.';
            this.errorSubject.next(errorMessage);
            this.loadingSubject.next(false);
          }
        })
      );
  }

  refreshToken(): Observable<AuthResponse> {
    if (this.refreshingToken) {
      return throwError(() => new Error('Refresh already in progress'));
    }

    const refreshToken = this.getRefreshToken();

    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    this.refreshingToken = true;

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken })
      .pipe(
        tap(response => {
          this.saveTokens(response.accessToken, response.refreshToken);
          const user = this.getUserFromToken(response.accessToken);
          this.saveUser(user);
          this.currentUserSubject.next(user);
          this.startRefreshTokenTimer();
        }),
        catchError(error => {
          this.logout();
          return throwError(() => error);
        }),
        finalize(() => {
          this.refreshingToken = false;
        })
      );
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.stopRefreshTokenTimer();
    this.router.navigate(['/auth/login']);
  }

  clearError(): void {
    this.errorSubject.next(null);
  }

  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  saveUser(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  getUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  getUserFromToken(token: string): User {
    const tokenPayload = JSON.parse(atob(token.split('.')[1]));

    return {
      username: tokenPayload.username || tokenPayload.sub,
      email: tokenPayload.email || tokenPayload.sub,
      roles: tokenPayload.roles || []
    };
  }

  hasRole(role: string): boolean {
    const user = this.getUser();
    return user?.roles?.includes(role) || false;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getUser();
    return user?.roles?.some(role => roles.includes(role)) || false;
  }

  private startRefreshTokenTimer() {
    // Arrêter le timer existant
    this.stopRefreshTokenTimer();

    // Obtenir le token d'accès
    const accessToken = this.getAccessToken();
    if (!accessToken) {
      return;
    }

    try {
      // Décoder le token pour obtenir sa date d'expiration
      const tokenPayload = JSON.parse(atob(accessToken.split('.')[1]));
      const expires = new Date(tokenPayload.exp * 1000);

      // Définir le délai à 5 minutes avant l'expiration
      const timeout = expires.getTime() - Date.now() - (5 * 60 * 1000);

      // Ne pas planifier si le token expire dans moins de 30 secondes
      if (timeout <= 30000) {
        return;
      }

      // Planifier le rafraîchissement
      this.refreshTokenTimeout = setTimeout(() => {
        this.refreshToken().subscribe();
      }, timeout);
    } catch (e) {
      console.error('Erreur lors du décodage du token', e);
    }
  }

  private stopRefreshTokenTimer() {
    if (this.refreshTokenTimeout) {
      clearTimeout(this.refreshTokenTimeout);
    }
  }
}
