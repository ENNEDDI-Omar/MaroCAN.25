import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TicketReservationComponent } from '../../features/tickets/ticket-reservation/ticket-reservation.component';



describe('TicketReservationComponent', () => {
  let component: TicketReservationComponent;
  let fixture: ComponentFixture<TicketReservationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketReservationComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TicketReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
