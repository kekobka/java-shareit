package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getItemById() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        Mockito.when(itemService.getById(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new ItemBookingDto());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/" + itemId)
                        .header(REQUEST_HEADER, userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
        Mockito.verify(itemService, Mockito.times(1)).getById(itemId, userId);
    }

    @Test
    void getAllItemsByUser() throws Exception {
        long userId = 1L;

        Mockito.when(itemService.allItemsFromUser(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(REQUEST_HEADER, userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
        Mockito.verify(itemService, Mockito.times(1)).allItemsFromUser(userId);
    }

    @Test
    void searchItems() throws Exception {
        long userId = 1L;
        String text = "text";

        Mockito.when(itemService.search(ArgumentMatchers.any())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", text)
                        .header(REQUEST_HEADER, userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
        Mockito.verify(itemService, Mockito.times(1)).search(text);
    }

    @Test
    void createItem() throws Exception {
        long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Name");
        itemDto.setAvailable(true);
        itemDto.setDescription("Description");

        ObjectMapper objectMapper = new ObjectMapper();

        String itemJson = objectMapper.writeValueAsString(itemDto);
        System.out.println(itemJson);
        Mockito.when(itemService.create(itemDto, 1)).thenReturn(new Item());

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header(REQUEST_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));

        Mockito.verify(itemService, Mockito.times(1)).create(itemDto, userId);
    }

    @Test
    void commentItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        Comment comment = new Comment();
        comment.setText("Text");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String commentJson = objectMapper.writeValueAsString(comment);

        Mockito.when(itemService.comment(itemId, userId, comment)).thenReturn(new Comment());

        mockMvc.perform(MockMvcRequestBuilders.post("/items/" + itemId + "/comment")
                        .header(REQUEST_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
        Mockito.verify(itemService, Mockito.times(1)).comment(itemId, userId, comment);
    }

    @Test
    void editItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        Map<String, Object> updates = new HashMap<>();

        Mockito.when(itemService.update(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new Item());

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/" + itemId)
                        .header(REQUEST_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
        Mockito.verify(itemService, Mockito.times(1)).update(itemId, userId, updates);
    }
}