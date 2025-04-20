import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ticketPrice',
  standalone: true
})
export class TicketPricePipe implements PipeTransform {

  transform(price: number): string {
    return `${price.toFixed(2)} MAD`;
  }

}
