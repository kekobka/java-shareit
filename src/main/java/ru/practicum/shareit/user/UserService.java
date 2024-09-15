package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private long id = 0;

    public User create(User user) {
        if (isEmailExist(user.getEmail()))
            throw new EmailAlreadyExistException(user.getEmail());
        user.setId(generateId());
        return userRepository.create(user);
    }

    public User getById(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Id: " + userId));
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User update(long userId, UserDto userDto) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Id: " + userId));

        if (userDto.getEmail() != null && isEmailExist(userDto.getEmail())) {
            throw new EmailAlreadyExistException(userDto.getEmail());
        }
        User updatedUser = updateUserDetails(user, userDto);
        userRepository.deleteById(userId, user);

        return userRepository.create(updatedUser);
    }

    private User updateUserDetails(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    public void deleteById(long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Id: " + userId));
        userRepository.deleteById(userId, user);
    }

    private boolean isEmailExist(String email) {
        return userRepository.getEmails().contains(email);
    }

    private long generateId() {
        return ++id;
    }
}