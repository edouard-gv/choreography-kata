import bus.Event;
import bus.Listener;
import bus.MessageBus;

public class TicketingService implements Listener {
    private final MessageBus bus;

    public TicketingService(MessageBus bus) {
        this.bus = bus;
        bus.subscribe(this);
    }

    public void onTicketBooked(int numberOfSeats) {
        System.out.println("Ticket printed");
    }

    @Override
    public void onMessage(Event event) {
        if (Event.ENOUGH_SEATS.equals(event.getName())) {
            this.onTicketBooked(event.getValue());
        }
    }
}


