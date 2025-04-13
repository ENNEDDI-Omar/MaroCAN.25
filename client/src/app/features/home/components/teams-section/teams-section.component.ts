import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-teams-section',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './teams-section.component.html',
  styleUrls: []
})
export class TeamsSectionComponent {
  // Équipes qualifiées (à compléter avec les équipes réelles)
  featuredTeams = [
    { name: 'Maroc', flag: 'assets/images/flags/morocco.png', qualified: true },
    { name: 'Sénégal', flag: 'assets/images/flags/senegal.png', qualified: true },
    { name: 'Algérie', flag: 'assets/images/flags/algeria.png', qualified: true },
    { name: 'Égypte', flag: 'assets/images/flags/egypt.png', qualified: true },
    { name: 'Nigeria', flag: 'assets/images/flags/nigeria.png', qualified: true },
    { name: 'Côte d\'Ivoire', flag: 'assets/images/flags/ivory-coast.png', qualified: true },
    { name: 'Cameroun', flag: 'assets/images/flags/cameroon.png', qualified: true },
    { name: 'Ghana', flag: 'assets/images/flags/ghana.png', qualified: true }
  ];
}
