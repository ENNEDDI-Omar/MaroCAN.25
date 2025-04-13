import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
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

    // Pour remplacer le store, utilisez des BehaviorSubject
    private currentUserSubject = new BehaviorSubject<User | null>(null);
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private errorSubject = new BehaviorSubject<string | null>(null);

    // Exposez les observables pour les composants
    currentUser$ = this.currentUserSubject.asObservable();
    loading$ = this.loadingSubject.asObservable();
    error$ = this.errorSubject.asObservable();

    constructor(private http: HttpClient, private router: Router) {
        // Charger l'utilisateur depuis le localStorage au démarrage
        const userStr = localStorage.getItem('user');
        if (userStr) {
            this.currentUserSubject.next(JSON.parse(userStr));
        }
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

    logout(): void {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        this.currentUserSubject.next(null);
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
            email: tokenPayload.sub,
            username: tokenPayload.username || tokenPayload.sub.split('@')[0]
        };
    }
}
