package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestBody @Valid ItemDto itemDto,
                          @RequestHeader(HEADER) long userId) {
        return ItemMapper.itemToDto(itemService.create(itemDto, userId));
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestBody @Valid  UpdateItemDto itemDto,
                          @RequestHeader(HEADER) long userId) {
        return ItemMapper.itemToDto(itemService.update(itemId, itemDto, userId));
    }

    @GetMapping("{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        return ItemMapper.itemToDto(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemDto> allItemsFromUser(@RequestHeader(HEADER) long userId) {
        return itemService.allItemsFromUser(userId);
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}