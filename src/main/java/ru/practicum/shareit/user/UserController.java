package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid User user) {
        log.info("POST /users");
        return UserMapper.userToDto(userService.create(user));
    }

    @GetMapping("{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.info("GET /users/{}", userId);
        return UserMapper.userToDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET /users");
        return UserMapper.userToDto(userService.getAll());
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        log.info("PATCH /users/{}", userId);
        return UserMapper.userToDto(userService.update(userId, userDto));
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteById(userId);
    }
}