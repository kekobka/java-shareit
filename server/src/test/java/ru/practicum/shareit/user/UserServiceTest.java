package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void getUserById() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        User result = userService.getById(userId);
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAll();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUser() {
        User user = new User(1L, "name", "desc");
        UserDto userDto = UserMapper.userToDto(user);
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.create(userDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUser() {
        User user = new User(1L, "name", "desc");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.update(user.getId(), Collections.emptyMap());
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUserName() {
        User user = new User(1L, "name", "desc");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.update(user.getId(), Map.of("name", "NewName"));
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUserEmail() {
        User user = new User(1L, "name", "desc");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.update(user.getId(), Map.of("email", "newmail@mail.ru"));
        Assertions.assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser() {
        userService.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}