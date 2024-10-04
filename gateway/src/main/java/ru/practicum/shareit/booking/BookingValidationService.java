package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadDateException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class BookingValidationService {
    public void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        if (start == null || end == null) {
            throw new BadDateException("Cannot be empty");
        }

        if (start.isBefore(now)) {
            throw new BadDateException("The start time should not be in the past");
        }

        if (end.isBefore(now)) {
            throw new BadDateException("The end time should not be in the past");
        }

        if (!start.isBefore(end)) {
            throw new BadDateException("The start date of the booking must be before the end date of the booking");
        }
    }
}