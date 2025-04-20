import { TicketStatusPipe } from './ticket-status.pipe';

describe('TicketStatusPipe', () => {
  it('create an instance', () => {
    const pipe = new TicketStatusPipe();
    expect(pipe).toBeTruthy();
  });
});
