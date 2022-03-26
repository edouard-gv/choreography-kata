import org.junit.jupiter.api.Test;

public class BookingServiceTest {

    @Test
    public void thereShouldBeEnoughSeats() {
        BookingService bookingService = new BookingService(new NotificationService(), new InventoryService(5), new TicketingService());
        bookingService.book(4);
    }

    @Test
    public void thereShouldNotBeEnoughSeats() {
        BookingService bookingService = new BookingService(new NotificationService(), new InventoryService(3), new TicketingService());
        bookingService.book(4);
    }
}
