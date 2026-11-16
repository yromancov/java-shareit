package ru.practicum.shareit.request;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto request() {
        ItemShortDto item = new ItemShortDto();
        item.setId(10L);
        item.setName("Drill");
        item.setOwnerId(3L);
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.now());
        dto.setItems(List.of(item));
        return dto;
    }

    @Test
    void createRequest() throws Exception {
        when(itemRequestService.createRequests(eq(1L), any())).thenReturn(request());

        mvc.perform(post("/requests").header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Нужна дрель\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getRequestsFromUser() throws Exception {
        when(itemRequestService.getAllRequestsFromUser(1L)).thenReturn(List.of(request()));

        mvc.perform(get("/requests").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].items[0].id").value(10))
                .andExpect(jsonPath("$[0].items[0].name").value("Drill"))
                .andExpect(jsonPath("$[0].items[0].ownerId").value(3));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequestsWithoutUser(2L)).thenReturn(List.of(request()));

        mvc.perform(get("/requests/all").header(USER_HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(1L)).thenReturn(request());

        mvc.perform(get("/requests/1").header(USER_HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}