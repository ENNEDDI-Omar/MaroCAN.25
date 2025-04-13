import { Component } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: []
})
export class NavbarComponent {
  navItems = [
    { label: 'Accueil', route: '/home' },
    { label: 'Matchs', route: '/matches' },
    { label: 'Équipes', route: '/teams' },
    { label: 'Actualités', route: '/news' },
    { label: 'Billetterie', route: '/tickets' }
  ];
}
