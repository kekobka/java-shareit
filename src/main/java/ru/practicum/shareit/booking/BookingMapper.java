package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static BookingDto bookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking dtoToBooking(BookingDto bookingDTO, User user, Item item) {
        return new Booking(
                bookingDTO.getId(),
                bookingDTO.getStart(),
                bookingDTO.getEnd(),
                item,
                user,
                bookingDTO.getStatus()
        );
    }
}