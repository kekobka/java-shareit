package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collections;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBookingsById() throws Exception {
        long bookingId = 1L;
        long userId = 1L;

        Mockito.when(bookingService.getById(bookingId, userId)).thenReturn(new Booking());

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/" + bookingId)
                        .header(REQUEST_HEADER, userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));
        Mockito.verify(bookingService, Mockito.times(1)).getById(bookingId, userId);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getBookingsByUser(BookingState state) throws Exception {
        long userId = 2L;
        Mockito.when(bookingService.getByUser(
                ArgumentMatchers.eq(userId),
                ArgumentMatchers.eq(BookingState.valueOf(state.name())))
        ).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(REQUEST_HEADER, userId)
                        .param("state", state.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        Mockito.verify(bookingService, Mockito.times(1))
                .getByUser(
                        ArgumentMatchers.eq(userId),
                        ArgumentMatchers.eq(BookingState.valueOf(state.name()))
                );
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getBookingsByOwner(BookingState state) throws Exception {
        long userId = 3L;
        Mockito.when(
                bookingService.getByUser(
                        ArgumentMatchers.eq(userId),
                        ArgumentMatchers.eq(BookingState.valueOf(state.name()))
                )).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(REQUEST_HEADER, userId)
                        .param("state", state.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        Mockito.verify(bookingService, Mockito.times(1))
                .getByOwner(
                        ArgumentMatchers.eq(userId),
                        ArgumentMatchers.eq(BookingState.valueOf(state.name()))
                );
    }

    @Test
    void createBooking() throws Exception {
        long userId = 4L;
        LocalDateTime start = LocalDateTime.of(2024, 8, 9, 12, 0);
        LocalDateTime end = start.plusMinutes(30);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String bookingJson = objectMapper.writeValueAsString(bookingDto);

        Mockito.when(bookingService.create(bookingDto, userId)).thenReturn(new Booking());

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header(REQUEST_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));

        Mockito.verify(bookingService, Mockito.times(1))
                .create(ArgumentMatchers.eq(bookingDto), ArgumentMatchers.eq(userId));
    }

    @Test
    void approveBooking() throws Exception {
        long userId = 5L;
        long bookingId = 2L;
        boolean approved = true;

        Mockito.when(bookingService.approve(bookingId, approved, userId)).thenReturn(new Booking());

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/" + bookingId)
                        .header(REQUEST_HEADER, userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{}"));

        Mockito.verify(bookingService, Mockito.times(1)).approve(bookingId, approved, userId);
    }
}