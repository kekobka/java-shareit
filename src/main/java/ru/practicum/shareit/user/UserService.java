package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public User create(UserDto user) {
        log.info("POST /users");

        return userRepository.save(UserMapper.dtoToUser(user));
    }

    public User getById(long userId) {
        log.info("GET /users/{}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Id: " + userId));
    }

    public List<User> getAll() {
        log.info("GET /users");

        return userRepository.findAll();
    }

    public User update(Long userId, Map<String, Object> params) {
        log.info("PATCH /users/{}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Id: " + userId));

        params.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "email":
                    user.setEmail((String) value);
                    break;
            }
        });

        return userRepository.save(user);
    }

    public void deleteById(long userId) {
        log.info("DELETE /users/{}", userId);

        userRepository.deleteById(userId);
    }

}