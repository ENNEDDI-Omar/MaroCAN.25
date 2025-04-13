import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {AuthService} from "../../core/auth/services/auth.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: []
})
export class HeaderComponent {
  isMenuOpen = false;

  constructor(public authService: AuthService, private router: Router) {}

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
