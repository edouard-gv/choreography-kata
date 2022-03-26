import org.junit.jupiter.api.Test;

public class BookingServiceTest {

    @Test
    public void nominalBooking() {
        BookingService bookingService = new BookingService(new InventoryService(5), new TicketingService());
        bookingService.book(4);
    }
}
