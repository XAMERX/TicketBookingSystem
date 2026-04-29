package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BookingManagerTest {

    private IPaymentGateway paymentGateway;
    private INotificationService notificationService;
    private IEventRepository eventRepository;
    private BookingManager bookingManager;

    @BeforeEach
    void setUp() {
        paymentGateway = mock(IPaymentGateway.class);
        notificationService = mock(INotificationService.class);
        eventRepository = mock(IEventRepository.class);
        bookingManager = new BookingManager(paymentGateway, notificationService, eventRepository);
    }

    @Test
    void book_validInputAndAvailableEvent_processesBookingSuccessfully() {
        String eventId = "EVT-100";
        double amount = 150.0;
        String transactionId = "TXN-123";

        when(eventRepository.isSoldOut(eventId)).thenReturn(false);
        when(paymentGateway.processPayment(amount)).thenReturn(transactionId);

        bookingManager.book(eventId, amount);

        verify(eventRepository, times(1)).isSoldOut(eventId);
        verify(paymentGateway, times(1)).processPayment(amount);
        verify(eventRepository, times(1)).saveBooking(eventId);
        verify(notificationService, times(1))
                .sendConfirmation("Booking successful. Transaction ID: " + transactionId);
        verifyNoMoreInteractions(paymentGateway, notificationService, eventRepository);
    }

    @Test
    void book_nullEventId_doesNotCallAnyDependency() {
        bookingManager.book(null, 150.0);

        verifyNoInteractions(paymentGateway, notificationService, eventRepository);
    }

    @Test
    void book_emptyEventId_doesNotCallAnyDependency() {
        bookingManager.book("", 150.0);

        verifyNoInteractions(paymentGateway, notificationService, eventRepository);
    }

    @Test
    void book_nonPositiveAmount_doesNotCallAnyDependency() {
        bookingManager.book("EVT-100", 0.0);
        bookingManager.book("EVT-100", -20.0);

        verifyNoInteractions(paymentGateway, notificationService, eventRepository);
    }

    @Test
    void book_soldOutEvent_stopsAfterAvailabilityCheck() {
        String eventId = "EVT-200";
        double amount = 200.0;

        when(eventRepository.isSoldOut(eventId)).thenReturn(true);

        bookingManager.book(eventId, amount);

        verify(eventRepository, times(1)).isSoldOut(eventId);
        verify(paymentGateway, never()).processPayment(amount);
        verify(eventRepository, never()).saveBooking(eventId);
        verify(notificationService, never()).sendConfirmation("Booking successful. Transaction ID: TXN-456");
        verifyNoMoreInteractions(paymentGateway, notificationService, eventRepository);
    }
}
