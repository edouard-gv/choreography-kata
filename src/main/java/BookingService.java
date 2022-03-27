import bus.Event;
import bus.MessageBus;

public class BookingService {

    private final MessageBus bus;

    public BookingService(MessageBus bus) {
        this.bus = bus;
    }

    public void book(int numberOfSeats) {
        System.out.println("Booking requested: "+numberOfSeats);
        bus.send(new Event(Event.ON_BOOKING_REQUESTER, numberOfSeats));
    }

}
