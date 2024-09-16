package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid User user) {
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
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        return UserMapper.userToDto(userService.update(userId, userDto));
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}