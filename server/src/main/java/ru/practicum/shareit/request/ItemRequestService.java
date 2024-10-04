package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Collection<ItemRequestDto> getAllSelf(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Id: " + userId));
        return itemRequestRepository.findByRequesterId(userId).stream()
                .map(itemRequest -> {
                    List<ItemRequestingDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                            .map(ItemMapper::itemToRequestingDto)
                            .toList();
                    return ItemRequestMapper.itemToRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    public ItemRequestDto getById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id: " + id));
        List<ItemRequestingDto> items = itemRepository.findAllByRequestId(id).stream()
                .map(ItemMapper::itemToRequestingDto)
                .toList();
        return ItemRequestMapper.itemToRequestDto(itemRequest, items);
    }

    public Page<ItemRequestDto> getAllOthers(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestRepository.findAllByOrderByCreatedDesc(pageable).map(itemRequest -> {
            List<ItemRequestingDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::itemToRequestingDto)
                    .toList();
            return ItemRequestMapper.itemToRequestDto(itemRequest, items);
        });
    }

    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto request) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Id: " + userId));
        ItemRequest itemRequest = new ItemRequest(0L, request.getDescription(), requester, Instant.now());

        return ItemRequestMapper.itemToRequestDto(itemRequestRepository.save(itemRequest), null);
    }
}