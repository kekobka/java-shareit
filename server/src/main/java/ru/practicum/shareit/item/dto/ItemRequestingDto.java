package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequestingDto {
    private Long id;
    private String name;
    private Long ownerId;
}