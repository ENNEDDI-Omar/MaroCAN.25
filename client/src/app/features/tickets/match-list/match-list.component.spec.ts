import { TicketReservationGuard } from '../../../core/guards/ticket-reservation.guard';
import {TestBed} from "@angular/core/testing";

describe('TicketReservationGuard', () => {
  let guard: TicketReservationGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TicketReservationGuard]
    });
    guard = TestBed.inject(TicketReservationGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
