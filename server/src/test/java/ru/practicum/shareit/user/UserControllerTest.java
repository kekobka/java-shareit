package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUserById() throws Exception {
        long userId = 1L;
        when(userService.getById(userId)).thenReturn(new User());

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService, times(1)).getAll();
    }

    @Test
    void createUser() throws Exception {
        User user = new User();
        user.setName("Name");
        user.setEmail("email@mail.ru");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);

        when(userService.create(any())).thenReturn(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().json("{}"));
        verify(userService, times(1)).create(UserMapper.userToDto(user));
    }

    @Test
    void editUser() throws Exception {
        long userId = 1L;
        Map<String, Object> updates = new HashMap<>();

        when(userService.update(any(), any())).thenReturn(new User());

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(userService, times(1)).update(userId, updates);
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/" + userId)
                        .content("{}"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteById(userId);
    }
}