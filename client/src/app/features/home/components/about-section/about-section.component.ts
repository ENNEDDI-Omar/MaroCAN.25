import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-about-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about-section.component.html',
  styleUrls: []
})
export class AboutSectionComponent {
  // Dates clés de l'événement
  keyDates = [
    { date: '10 janvier 2025', event: 'Cérémonie d\'ouverture et match inaugural' },
    { date: '10-28 janvier 2025', event: 'Phase de groupes' },
    { date: '31 janvier - 2 février 2025', event: 'Huitièmes de finale' },
    { date: '5-6 février 2025', event: 'Quarts de finale' },
    { date: '8-9 février 2025', event: 'Demi-finales' },
    { date: '10 février 2025', event: 'Finale de la CAN 2025' }
  ];
}
