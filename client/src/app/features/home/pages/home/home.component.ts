import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/auth/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: []
})
export class HomeComponent implements OnInit {
  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      console.log('Utilisateur connecté:', this.authService.getUser());
    } else {
      console.log('Utilisateur non connecté');
    }
  }
}
