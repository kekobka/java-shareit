package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.AccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private ItemService itemService;

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemService(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository);

        user = new User(1L, "User", "user@mail.ru");

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        comment = new Comment();
        comment.setText("Text");
        comment.setCreated(LocalDateTime.of(2024, 8, 9, 12, 0));
        comment.setId(1L);
        comment.setAuthor(user);
    }

    @Test
    void getItemById() {
        long itemId = 1L;
        Item item = new Item(1L, "Item", "Description", true, user, null);
        booking.setItem(item);
        comment.setItem(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findUpcomingBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemBookingDto result = itemService.getById(itemId, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void getItemByIdWhenBookingNull() {
        long itemId = 1L;
        Item item = new Item(1L, "Item", "Description", true, user, null);
        booking.setItem(item);
        comment.setItem(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findUpcomingBookingsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemBookingDto result = itemService.getById(itemId, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void searchItems() {
        String text = "Text";
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(itemRepository.searchAllByTextInNameOrDescription(any())).thenReturn(List.of(item));

        Collection<Item> result = itemService.search(text);
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).searchAllByTextInNameOrDescription(text);
    }

    @Test
    void searchEmptyItem() {
        String text = "";
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(itemRepository.searchAllByTextInNameOrDescription(any())).thenReturn(List.of(item));

        Collection<Item> result = itemService.search(text);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(itemRepository, times(0)).searchAllByTextInNameOrDescription(text);

    }

    @Test
    void createItem() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        Item result = itemService.create(itemDto, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);

    }

    @Test
    void createItemWithRequest() {
        ItemRequest itemRequest = new ItemRequest(1L, "123", user, Instant.now());
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setRequestId(1L);
        Item result = itemService.create(itemDto, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItemWithBadRequestId() {
        ItemRequest itemRequest = new ItemRequest(1L, "123", user, Instant.now());
        Item item = new Item(1L, "Item", "Description", true, user, itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setRequestId(2L);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.create(itemDto, user.getId());
        });

        assertEquals("Id: 2", exception.getMessage());
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void editItem() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.update(item.getId(), user.getId(), new HashMap<>());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemName() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.update(item.getId(), user.getId(), Map.of("name", "NewItem"));
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemDescription() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.update(item.getId(), user.getId(), Map.of("description", "description"));
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemAvailable() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.update(item.getId(), user.getId(), Map.of("available", false));
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemByNotOwner() {
        User owner = new User(2L, "User", "user@mail.ru");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Exception exception = assertThrows(AccessDeniedException.class, () -> {
            itemService.update(item.getId(), 1L, Map.of("name", "name"));
        });

        assertEquals("You cannot update this item", exception.getMessage());
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void commentItem() {
        Comment comment = new Comment();
        comment.setText("Text");
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        Comment result = itemService.comment(item.getId(), user.getId(), comment);
        Assertions.assertNotNull(result);
    }

    @Test
    void commentItemWithEmptyComment() {
        Comment comment = new Comment();
        comment.setText("");
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        Exception exception = assertThrows(CommentException.class, () -> {
            itemService.comment(item.getId(), user.getId(), comment);
        });

        assertEquals("Cannot be empty", exception.getMessage());

    }

    @Test
    void commentItemWithNullComment() {
        Comment comment = new Comment();
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        Exception exception = assertThrows(CommentException.class, () -> {
            itemService.comment(item.getId(), user.getId(), comment);
        });

        assertEquals("Cannot be empty", exception.getMessage());

    }

    @Test
    void commentItemWithoutBooking() {
        Comment comment = new Comment();
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);
        comment.setText("Text");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.save(any())).thenReturn(comment);

        Exception exception = assertThrows(CommentException.class, () -> {
            itemService.comment(item.getId(), user.getId(), comment);
        });

        assertEquals("You cannot leave a review on this subject", exception.getMessage());

    }
}