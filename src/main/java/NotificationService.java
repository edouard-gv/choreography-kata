import bus.Event;
import bus.Listener;
import bus.MessageBus;

public class NotificationService implements Listener {

    private final MessageBus bus;

    public NotificationService(MessageBus bus) {
        this.bus = bus;
        bus.subscribe(this);
    }

    @Override
    public void onMessage(Event event) {
        if (Event.NOT_ENOUGH_SEATS.equals(event.getName())){
            this.notifyUser();
        }
    }

    private void notifyUser() {
        System.out.println("User notified: not enough seats");
    }
}
