export interface Match {
  id: number;
  homeTeam: string;
  awayTeam: string;
  stadium: string;
  dateTime: Date;
  availableTickets: {
    [section: string]: number
  };
  sectionPrices: {
    [section: string]: number
  };
}
