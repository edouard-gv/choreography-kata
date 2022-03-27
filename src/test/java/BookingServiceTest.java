import bus.MessageBus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingServiceTest {

    @Test
    public void thereShouldBeEnoughSeats() {
        MessageBus bus = new MessageBus();
        InventoryService inventoryService = new InventoryService(5, bus);
        TicketingService ticketingService = new TicketingService(bus);
        BookingService bookingService = new BookingService(bus);
        bookingService.book(4);
        assertEquals(1, inventoryService.getSeatsLeft());
    }

    @Test
    public void thereShouldNotBeEnoughSeats() {
        MessageBus bus = new MessageBus();
        InventoryService inventoryService = new InventoryService(5, bus);
        TicketingService ticketingService = new TicketingService(bus);
        BookingService bookingService = new BookingService(bus);
        bookingService.book(7);
        assertEquals(5, inventoryService.getSeatsLeft());
    }
}
