package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto user) {
        return UserMapper.userToDto(userService.create(user));
    }

    @GetMapping("{userId}")
    public UserDto getById(@PathVariable long userId) {
        return UserMapper.userToDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return UserMapper.userToDto(userService.getAll());
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody Map<String, Object> params) {
        return UserMapper.userToDto(userService.update(userId, params));
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}