package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static Item dtoToItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }

    public static ItemDto itemToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }

    public static ItemBookingDto toItemBookingDto(Item item, BookingDto lastBooking, BookingDto nextBooking, Long userId, List<CommentDto> comments) {

        long itemOwnerId = item.getOwner().getId();

        return new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                itemOwnerId == userId ? lastBooking : null,
                itemOwnerId == userId ? nextBooking : null,
                comments
        );
    }

    public static ItemRequestingDto itemToRequestingDto(Item item) {
        return new ItemRequestingDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}