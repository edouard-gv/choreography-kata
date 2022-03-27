package bus;

/**
 * A basic event with a name and one single integer value
 */
public class Event {

    public static final String ON_BOOKING_REQUESTER = "ON_BOOKING_REQUESTER";
    public static final String ENOUGH_SEATS = "ENOUGH_SEATS";
    public static final String NOT_ENOUGH_SEATS = "NOT_ENOUGH_SEATS";

    private final String name;
    private final int value;

    public Event(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {return name;}
    public int getValue() {return value;}
}