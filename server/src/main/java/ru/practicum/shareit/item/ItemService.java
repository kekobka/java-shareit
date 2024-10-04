package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.AccessDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public Item create(ItemDto itemDto, long userId) {
        log.info("POST /items");

        User user = findUserById(userId);

        ItemRequest itemRequest = null;
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Не найдено запроса предмета с id: " + requestId));
        }
        Item item = ItemMapper.dtoToItem(itemDto, user, itemRequest);

        return itemRepository.save(item);
    }

    @Transactional
    public Item update(Long itemId, Long userId, Map<String, Object> request) {
        log.info("PATCH /items/{}", request);

        Item item = findItemById(itemId);

        if (item.getOwner().getId() != userId) {
            throw new AccessDeniedException("You cannot update this item");
        }

        request.forEach((key, value) -> {
            switch (key) {
                case "name":
                    item.setName((String) value);
                    break;
                case "description":
                    item.setDescription((String) value);
                    break;
                case "available":
                    item.setAvailable((boolean) value);
                    break;
            }
        });


        return itemRepository.save(item);
    }

    public ItemBookingDto getById(Long id, Long userId) {
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemId(id);
        BookingDto lastBooking = lastBookings.isEmpty() ? null : BookingMapper
                .bookingToDto(lastBookings.getFirst());

        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookingsByItemId(id);
        BookingDto nextBooking = upcomingBookings.isEmpty() ? null : BookingMapper
                .bookingToDto(upcomingBookings.getFirst());

        List<CommentDto> comments = commentRepository.findAllByItemId(id).stream()
                .map(CommentMapper::commentToDto).collect(Collectors.toList());

        return ItemMapper.toItemBookingDto(findItemById(id), lastBooking, nextBooking, userId, comments);
    }

    @Transactional
    public Comment comment(Long itemId, Long userId, Comment comment) {
        Item item = findItemById(itemId);
        User user = findUserById(userId);
        List<Booking> endedBookings = bookingRepository.findLastBookingsByItemId(itemId);
        String text = comment.getText();
        if (text == null || text.isEmpty()) {
            throw new CommentException("Cannot be empty");
        }
        for (Booking booking : endedBookings) {
            if (booking.getBooker().getId() == userId) {
                return commentRepository.save(new Comment(0L, text, item, user, LocalDateTime.now()));
            }
        }
        throw new CommentException("You cannot leave a review on this subject");
    }


    public List<ItemBookingDto> allItemsFromUser(long userId) {
        log.info("GET /items HEADER -> {}", userId);

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> getById(item.getId(), userId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        log.info("GET /items PARAMS -> {}", text);

        if (text.isEmpty()) return Collections.emptyList();
        return itemRepository.searchAllByTextInNameOrDescription(text);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Id: " + userId));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Id: " + itemId));
    }
}