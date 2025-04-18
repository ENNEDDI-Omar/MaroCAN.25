export interface TicketReservation {
  matchId: number;
  sectionType: 'CATEGORY_1' | 'CATEGORY_2' | 'CATEGORY_3' | 'CATEGORY_4';
  quantity: number;
}
