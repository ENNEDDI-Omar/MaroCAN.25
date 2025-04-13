import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-actualities-section',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './actualities-section.component.html',
  styleUrls: []
})
export class ActualitiesSectionComponent {

  latestNews = [
    {
      id: 1,
      title: 'Le Maroc finalise les préparatifs pour la CAN 2025',
      summary: 'Les travaux de rénovation des stades et infrastructures sont en phase finale pour accueillir le tournoi continental.',
      imageUrl: 'assets/images/news/stadium-renovation.jpg',
      date: '15 décembre 2024'
    },
    {
      id: 2,
      title: 'Les billets pour le match d\'ouverture disponibles',
      summary: 'La vente de billets pour le match d\'ouverture entre le Maroc et une autre équipe africaine a commencé.',
      imageUrl: 'assets/images/news/tickets-sale.jpg',
      date: '10 décembre 2024'
    },
    {
      id: 3,
      title: 'La liste des équipes qualifiées complète',
      summary: 'Les 24 équipes qui participeront à la Coupe d\'Afrique des Nations 2025 sont désormais connues après les éliminatoires.',
      imageUrl: 'assets/images/news/qualified-teams.jpg',
      date: '5 décembre 2024'
    }
  ];

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' });
  }
}
