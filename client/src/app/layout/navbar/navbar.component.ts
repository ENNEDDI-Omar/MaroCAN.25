import { Component } from '@angular/core';
import {AuthService} from "../../core/auth/services/auth.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: []
})
export class NavbarComponent {

  constructor(public authService: AuthService) {}

  navItems = [
    { label: 'Accueil', route: '/home' },
    { label: 'Matchs', route: '/matches' },
    { label: 'Équipes', route: '/teams' },
    { label: 'Actualités', route: '/news' },
    { label: 'Billetterie', route: '/tickets' }
  ];
}
