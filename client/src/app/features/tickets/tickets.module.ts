import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatchListComponent} from "./match-list/match-list.component";
import {TicketReservationComponent} from "./ticket-reservation/ticket-reservation.component";
import {SectionSelectionComponent} from "./section-selection/section-selection.component";
import {OrderCartComponent} from "./order-cart/order-cart.component";
import {OrderConfirmationComponent} from "./order-confirmation/order-confirmation.component";
import {OrderHistoryComponent} from "./order-history/order-history.component";
import {TicketsRoutingModule} from "./tickets-routing.module";
import {SharedModule} from "../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";



@NgModule({
  declarations: [
    MatchListComponent,
    TicketReservationComponent,
    SectionSelectionComponent,
    OrderCartComponent,
    OrderConfirmationComponent,
    OrderHistoryComponent
  ],
  imports: [
    CommonModule,
    TicketsRoutingModule,
    SharedModule,
    ReactiveFormsModule
  ]
})
export class TicketsModule { }
