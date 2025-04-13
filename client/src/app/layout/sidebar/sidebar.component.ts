import { Component } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: []
})
export class SidebarComponent {

  currentYear = new Date().getFullYear();

  sidebarLinks = [
    { icon: 'home', label: 'Tableau de bord', route: '/home' },
    { icon: 'calendar', label: 'Calendrier', route: '/calendar' },
    { icon: 'ticket', label: 'Mes billets', route: '/my-tickets' },
    { icon: 'team', label: 'Équipes favorites', route: '/favorites' },
    { icon: 'settings', label: 'Paramètres', route: '/settings' }
  ];
}
