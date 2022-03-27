package bus;

import java.util.ArrayList;
import java.util.List;

public class MessageBus {
    private List<Listener> subs = new ArrayList<>();

    public void subscribe(Listener l) {
        this.subs.add(l);
    }

    public void send(Event msg) {
        for (Listener l : subs) {
            l.onMessage(msg);
        }
    }
}