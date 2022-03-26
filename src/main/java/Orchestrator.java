public class Orchestrator {
    private final InventoryService inventoryService;
    private final TicketingService ticketingService;

    public Orchestrator(InventoryService inventoryService, TicketingService ticketingService) {
        this.inventoryService = inventoryService;
        this.ticketingService = ticketingService;
    }

    void onTicketBooked(int numberOfSeats) {
        if (inventoryService.onTicketBooked(numberOfSeats)) {
            ticketingService.onTicketBooked(numberOfSeats);
        }
    }
}
