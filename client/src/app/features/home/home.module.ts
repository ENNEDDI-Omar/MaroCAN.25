import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './pages/home/home.component';

import { HeroSectionComponent } from './components/hero-section/hero-section.component';
import { FeaturesSectionComponent } from './components/features-section/features-section.component';
import { AboutSectionComponent } from './components/about-section/about-section.component';
import { TeamsSectionComponent } from './components/teams-section/teams-section.component';
import { ActualitiesSectionComponent } from './components/actualities-section/actualities-section.component';
import { ContactSectionComponent } from './components/contact-section/contact-section.component';
import {LayoutModule} from "../../layout/layout.module";

@NgModule({
  declarations: [
    HomeComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    LayoutModule,
    HeroSectionComponent,
    FeaturesSectionComponent,
    AboutSectionComponent,
    TeamsSectionComponent,
    ActualitiesSectionComponent,
    ContactSectionComponent
  ]
})
export class HomeModule { }
