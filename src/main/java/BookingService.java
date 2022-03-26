public class BookingService {

    public BookingService(InventoryService inventoryService, TicketingService ticketingService) {
        this.inventoryService = inventoryService;
        this.ticketingService = ticketingService;
    }

    private InventoryService inventoryService;
    private TicketingService ticketingService;

    public void book(int numberOfSeats) {
        System.out.println("Booking requested: "+numberOfSeats);
        if (inventoryService.decrementCapacity(numberOfSeats)) {
            ticketingService.printTicket(numberOfSeats);
        }
    }
}
