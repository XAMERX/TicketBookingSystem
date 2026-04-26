package booking;

public interface IEventRepository {

    boolean isSoldOut(String eventId);
    void saveBooking(String eventId);

}
