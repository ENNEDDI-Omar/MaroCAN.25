import {OrderTicket} from "./order-ticket";

export interface TicketOrder {
  id?: number;
  orderReference?: string;
  tickets: OrderTicket[];
  totalAmount: number;
  paymentStatus: 'PENDING' | 'COMPLETED' | 'FAILED';
  createdAt?: string | Date;
  checkoutUrl?: string;
}
