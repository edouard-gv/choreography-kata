import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingServiceTest {

    @Test
    public void thereShouldBeEnoughSeats() {
        InventoryService inventoryService = new InventoryService(5);
        TicketingService ticketingService = new TicketingService();
        BookingService bookingService = new BookingService(new NotificationService(), inventoryService, ticketingService);
        bookingService.book(4);
        assertEquals(1, inventoryService.getSeatsLeft());
    }

    @Test
    public void thereShouldNotBeEnoughSeats() {
        InventoryService inventoryService = new InventoryService(3);
        BookingService bookingService = new BookingService(new NotificationService(), inventoryService, new TicketingService());
        bookingService.book(4);
        assertEquals(3, inventoryService.getSeatsLeft());
    }
}
