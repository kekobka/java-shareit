package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingServiceTest {
    private BookingService bookingService;

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Item ownedItem;

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingService = new BookingService(bookingRepository, itemRepository, userRepository);
        user = new User(1L, "User", "user@mail.ru");
        item = new Item(1L, "Item", "Description", true, null, null);
        item.setOwner(new User(2L, "User2", "user2@mail.ru"));
        ownedItem = new Item(2L, "Item2", "Description2", true, user, null);
    }

    @Test
    void getById() {
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(bookingId, user.getId());
        Assertions.assertNotNull(result);
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
    }

    @Test
    void getByIdByOwner() {
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        Mockito.when(userRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        user.setId(2L);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getById(bookingId, 1L);
        });

        assertEquals("Id: 1", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(ArgumentMatchers.any());
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getByUser(BookingState state) throws Exception {
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(ArgumentMatchers.eq(user.getId())))
                .thenReturn(Collections.emptyList());

        List<Booking> result = bookingService.getByUser(user.getId(), BookingState.valueOf(state.name()));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getByOwner(BookingState state) throws Exception {
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ArgumentMatchers.eq(user.getId()), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ArgumentMatchers.eq(user.getId())))
                .thenReturn(Collections.emptyList());

        List<Booking> result = bookingService.getByOwner(user.getId(), BookingState.valueOf(state.name()));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void createBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(booking);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2L);
        bookingDto.setBookerId(user.getId());
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        bookingDto.setEnd(bookingDto.getStart().plusMinutes(30));

        Booking result = bookingService.create(bookingDto, user.getId());

        Assertions.assertNotNull(result);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void createBookingWithUnavailableItem() {
        item.setAvailable(false);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(booking);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2L);
        bookingDto.setBookerId(user.getId());
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        bookingDto.setEnd(bookingDto.getStart().plusMinutes(30));

        Exception exception = assertThrows(AvailabilityException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });

        assertEquals("This item cannot be booked", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.times(0)).save(ArgumentMatchers.any());
    }

    @Test
    void approveBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(ownedItem);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(ownedItem.getId())).thenReturn(Optional.of(ownedItem));
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(booking);

        Booking result = bookingService.approve(booking.getId(), true, user.getId());

        Assertions.assertNotNull(result);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(ArgumentMatchers.any());

    }

    @Test
    void approveApprovedBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(ownedItem);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(ownedItem.getId())).thenReturn(Optional.of(ownedItem));
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(booking);

        Exception exception = assertThrows(StatusException.class, () -> {
            bookingService.approve(booking.getId(), true, user.getId());
        });

        assertEquals("It is not possible to confirm a reservation that has already been confirmed.", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.times(0)).save(ArgumentMatchers.any());

    }
}