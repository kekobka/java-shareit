package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.exception.AccessDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private long id = 0;

    public Item create(ItemDto itemDto, long userId) {
        log.info("POST /items");
        User user = userService.getById(userId);

        Item item = ItemMapper.dtoToItem(itemDto);
        item.setId(generateId());
        item.setOwner(user);

        itemRepository.create(item);
        return item;
    }

    public Item update(long itemId, UpdateItemDto itemDto, long userId) {
        log.info("PATCH /items/{}", itemId);

        Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Id: " + itemId));

        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("You didn't create this item");
        }

        updateItemDetails(item, itemDto);
        return itemRepository.update(itemId, item);
    }

    private void updateItemDetails(Item item, UpdateItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    public Item getById(long itemId) {
        log.info("GET /items/{}", itemId);

        return itemRepository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Id: " + itemId));
    }

    public List<ItemDto> allItemsFromUser(long userId) {
        log.info("GET /items HEADER -> {}", userId);

        userService.getById(userId);
        return itemRepository.getUserItems(userId);
    }

    public List<ItemDto> search(String text) {
        log.info("GET /items PARAMS -> {}", text);

        return itemRepository.search(text);
    }

    private long generateId() {
        return ++id;
    }
}