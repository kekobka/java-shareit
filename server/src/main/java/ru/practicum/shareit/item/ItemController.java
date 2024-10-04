package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(HEADER) Long userId, @RequestBody Map<String, Object> params) {
        return ItemMapper.itemToDto(itemService.update(itemId, userId, params));
    }

    @GetMapping("{itemId}")
    public ItemBookingDto getById(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId) {
        return itemService.getById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@PathVariable Long itemId,
                              @RequestHeader(HEADER) Long userId, @RequestBody Comment comment) {
        return CommentMapper.commentToDto(itemService.comment(itemId, userId, comment));
    }

    @GetMapping
    public List<ItemBookingDto> allItemsFromUser(@RequestHeader(HEADER) long userId) {
        return itemService.allItemsFromUser(userId);
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text)
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}