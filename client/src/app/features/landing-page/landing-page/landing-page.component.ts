import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing-page.component.html',
  styleUrls: []
})
export class LandingPageComponent implements OnInit, OnDestroy {
  private backgroundImages = [
    'assets/images/Ticket1.png',
    'assets/images/Staduim.Casa.jpg',
    'assets/images/cup2.jpg',
  ];

  private currentImageIndex = 0;
  private intervalId: any;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Changer l'image de fond toutes les 5 secondes
    this.intervalId = setInterval(() => this.changeBackgroundImage(), 5000);
  }

  ngOnDestroy(): void {
    // Nettoyer l'intervalle lorsque le composant est détruit
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  navigateToLogin(): void {
    console.log('Navigation vers login');
    this.router.navigate(['/auth/login']);
  }

  navigateToRegister(): void {
    console.log('Navigation vers register');
    this.router.navigate(['/auth/register']);
  }

  private changeBackgroundImage(): void {
    this.currentImageIndex = (this.currentImageIndex + 1) % this.backgroundImages.length;
    const imgElement = document.getElementById('bgImage') as HTMLImageElement;

    // Vérifier si l'élément existe avant d'accéder à ses propriétés
    if (imgElement) {
      // Transition en douceur
      imgElement.classList.add('opacity-0');

      setTimeout(() => {
        imgElement.src = this.backgroundImages[this.currentImageIndex];
        imgElement.classList.remove('opacity-0');
      }, 500);
    }
  }
}
