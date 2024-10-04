package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.exception.AccessDeniedException;

@WebMvcTest(ItemController.class)
public class ExceptionControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void handleNotFound() throws Exception {
        Mockito.when(itemService.allItemsFromUser(ArgumentMatchers.anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void handleMethodDuplicateDataException() throws Exception {
        Mockito.when(itemService.allItemsFromUser(ArgumentMatchers.anyLong())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void handleMethodNotOwnerException() throws Exception {
        Mockito.when(itemService.allItemsFromUser(ArgumentMatchers.anyLong())).thenThrow(AccessDeniedException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}