package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.item.exception.AccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingService {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    public Booking getById(Long id, Long userId) {
        log.info("GET /bookings/{}", id);

        findUserById(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id: " + id));
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Id: " + id);
        }
        return booking;
    }

    public List<Booking> getByUser(Long userId, BookingState state) {
        log.info("GET /bookings");

        findUserById(userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return switch (state) {
            case BookingState.CURRENT -> bookingRepository
                    .findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, now, now);
            case BookingState.PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case BookingState.FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case BookingState.WAITING ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("WAITING"));
            case BookingState.REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("REJECTED"));
            case BookingState.ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };
    }

    public List<Booking> getByOwner(Long userId, BookingState state) {
        log.info("GET /bookings/owner");

        findUserById(userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return switch (state) {
            case BookingState.CURRENT -> bookingRepository
                    .findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, now, now);
            case BookingState.PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case BookingState.FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case BookingState.WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("WAITING"));
            case BookingState.REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("REJECTED"));
            case BookingState.ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };
    }

    @Transactional
    public Booking create(BookingDto booking, Long userId) {
        log.info("POST /bookings -> {}", booking);

        booking.setBookerId(userId);
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new BookingNotFoundException("Id: " + booking.getItemId()));

        if (!item.isAvailable()) {
            throw new AvailabilityException("This item cannot be booked");
        }

        User user = findUserById(booking.getBookerId());

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Id: " + booking.getItemId());
        }
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(BookingMapper.dtoToBooking(booking, user, item));
    }

    @Transactional
    public Booking approve(Long id, boolean approved, Long userId) {
        log.info("PATCH /bookings/{}", id);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id: " + userId));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id: " + id));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new AccessDeniedException("You are not the owner of this item");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new StatusException("It is not possible to confirm a reservation that has already been confirmed.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id: " + userId));
    }
}