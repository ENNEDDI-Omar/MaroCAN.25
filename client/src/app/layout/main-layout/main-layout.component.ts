import { Component } from '@angular/core';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: []
})
export class MainLayoutComponent {
  showSidebar = true;

  toggleSidebar(): void {
    this.showSidebar = !this.showSidebar;
  }
}
