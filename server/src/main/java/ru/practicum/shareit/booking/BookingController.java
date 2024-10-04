package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public Booking getById(@PathVariable Long id, @RequestHeader(HEADER) Long userId) {
        return bookingService.getById(id, userId);
    }

    @GetMapping()
    public List<Booking> getByUser(@RequestParam(defaultValue = "ALL") BookingState state,
                                   @RequestHeader(HEADER) Long userId) {
        return bookingService.getByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                    @RequestHeader(HEADER) Long userId) {
        return bookingService.getByOwner(userId, state);
    }

    @PostMapping
    public Booking create(@RequestBody BookingDto booking, @RequestHeader(HEADER) Long userId) {
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{id}")
    public Booking approve(@PathVariable Long id, @RequestParam boolean approved,
                           @RequestHeader(HEADER) Long userId) {
        return bookingService.approve(id, approved, userId);
    }
}