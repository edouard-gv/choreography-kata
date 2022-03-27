public class Orchestrator {
    private final InventoryService inventoryService;
    private final TicketingService ticketingService;
    private NotificationService notificationService;

    public Orchestrator(InventoryService inventoryService, TicketingService ticketingService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.ticketingService = ticketingService;
        this.notificationService = notificationService;
    }

    void onTicketBooked(int numberOfSeats) {
        if (inventoryService.onTicketBooked(numberOfSeats)) {
            ticketingService.onTicketBooked(numberOfSeats);
        }
        else {
            notificationService.notifyUser("Not enough seats");
        }
    }
}
