public class BookingService {

    private NotificationService notificationService;
    private InventoryService inventoryService;
    private TicketingService ticketingService;

    public BookingService(NotificationService notificationService, InventoryService inventoryService, TicketingService ticketingService) {
        this.notificationService = notificationService;
        this.inventoryService = inventoryService;
        this.ticketingService = ticketingService;
    }


    public void book(int numberOfSeats) {
        System.out.println("Booking requested: "+numberOfSeats);
        if (inventoryService.decrementCapacity(numberOfSeats)) {
            ticketingService.printTicket(numberOfSeats);
        }
        else {
            notificationService.notifyCustomer();
        }
    }
}
