import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'ticketStatus'
})
export class TicketStatusPipe implements PipeTransform {
  transform(status: string): string {
    switch(status) {
      case 'AVAILABLE':
        return 'Disponible';
      case 'RESERVED':
        return 'Réservé';
      case 'SOLD':
        return 'Vendu';
      case 'CANCELLED':
        return 'Annulé';
      default:
        return status;
    }
  }
}
