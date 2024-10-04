package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto itemToRequestDto(ItemRequest itemRequest, List<ItemRequestingDto> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated(),
                items
        );
    }
}