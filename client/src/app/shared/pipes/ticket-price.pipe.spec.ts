import { TicketPricePipe } from './ticket-price.pipe';

describe('TicketPricePipe', () => {
  it('create an instance', () => {
    const pipe = new TicketPricePipe();
    expect(pipe).toBeTruthy();
  });
});
