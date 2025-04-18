import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { ticketReservationGuard } from './ticket-reservation.guard';

describe('ticketReservationGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => ticketReservationGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
