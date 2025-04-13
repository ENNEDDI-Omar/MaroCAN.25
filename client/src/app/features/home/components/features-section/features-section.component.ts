import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-features-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './features-section.component.html',
  styleUrls: []
})
export class FeaturesSectionComponent {
  features = [
    {
      icon: 'calendar',
      title: 'Calendrier Complet',
      description: 'Accédez au calendrier détaillé de tous les matchs avec dates, heures et lieux.'
    },
    {
      icon: 'ticket',
      title: 'Billetterie en Ligne',
      description: 'Réservez vos billets facilement pour tous les matchs de la compétition.'
    },
    {
      icon: 'stats',
      title: 'Statistiques en Direct',
      description: 'Suivez les performances des équipes et des joueurs en temps réel.'
    },
    {
      icon: 'news',
      title: 'Actualités Exclusives',
      description: 'Restez informé avec les dernières nouvelles, interviews et analyses.'
    },
    {
      icon: 'teams',
      title: 'Profils des Équipes',
      description: 'Découvrez les informations détaillées sur chaque équipe participante.'
    },
    {
      icon: 'map',
      title: 'Guide du Maroc',
      description: 'Informations touristiques pour profiter pleinement de votre séjour.'
    }
  ];
}
