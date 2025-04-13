// src/app/features/home/components/hero-section/hero-section.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-hero-section',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './hero-section.component.html',
  styleUrls: []
})
export class HeroSectionComponent implements OnInit {
  // Images du slider
  backgroundImages = [
    'assets/images/Staduim.Casa.jpg',
    'assets/images/cup2.jpg',
    'assets/images/Ticket1.png'
  ];

  currentImageIndex = 0;
  private intervalId: any;

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    // Démarrer le slider d'images
    this.startImageSlider();
  }

  ngOnDestroy(): void {
    // Nettoyer l'intervalle quand le composant est détruit
    this.clearImageSlider();
  }

  private startImageSlider(): void {
    this.intervalId = setInterval(() => {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.backgroundImages.length;
      const heroElement = document.querySelector('.hero-background') as HTMLElement;
      if (heroElement) {
        heroElement.style.backgroundImage = `url(${this.backgroundImages[this.currentImageIndex]})`;
      }
    }, 5000);
  }

  private clearImageSlider(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }
}

// HTML pour le composant
// src/app/features/home/components/hero-section/hero-section.component.html
