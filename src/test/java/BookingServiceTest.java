import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingServiceTest {

    @Test
    public void thereShouldBeEnoughSeats() {
        InventoryService inventoryService = new InventoryService(5);
        TicketingService ticketingService = new TicketingService();
        NotificationService notificationService = new NotificationService();
        Orchestrator orchestrator = new Orchestrator(inventoryService, ticketingService, notificationService);
        BookingService bookingService = new BookingService(orchestrator);
        bookingService.book(4);
        assertEquals(1, inventoryService.getSeatsLeft());
    }

    @Test
    public void thereShouldNotBeEnoughSeats() {
        InventoryService inventoryService = new InventoryService(3);
        TicketingService ticketingService = new TicketingService();
        NotificationService notificationService = new NotificationService();
        Orchestrator orchestrator = new Orchestrator(inventoryService, ticketingService, notificationService);
        BookingService bookingService = new BookingService(orchestrator);
        bookingService.book(4);
        assertEquals(3, inventoryService.getSeatsLeft());
    }
}
