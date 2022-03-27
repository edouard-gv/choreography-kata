import bus.Event;
import bus.Listener;
import bus.MessageBus;

public class InventoryService implements Listener {

    int seatsLeft;
    MessageBus bus;

    public InventoryService(int numberOfSeats, MessageBus bus) {
        this.seatsLeft = numberOfSeats;
        this.bus = bus;
        bus.subscribe(this);
    }

    public void onTicketBooked(int numberOfSeatsRequested) {
        if (numberOfSeatsRequested > this.seatsLeft) {
            bus.send(new Event(Event.NOT_ENOUGH_SEATS, 0));
            return;
        }
        this.seatsLeft -= numberOfSeatsRequested;
        System.out.println("Remaining number of seats: "+this.seatsLeft);
        bus.send(new Event(Event.ENOUGH_SEATS, numberOfSeatsRequested));
    }

    public Object getSeatsLeft() {
        return this.seatsLeft;
    }

    @Override
    public void onMessage(Event event) {
        if (Event.ON_BOOKING_REQUESTER.equals(event.getName())) {
            this.onTicketBooked(event.getValue());
        }
    }
}
