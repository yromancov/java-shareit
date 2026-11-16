package ru.practicum.shareit.item;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;

    private ItemDto item() {
        ItemDto dto = TestData.item("Drill");
        dto.setId(1L);
        return dto;
    }

    @Test
    void createItem() throws Exception {
        ItemDto dto = item();
        dto.setRequestId(5L);
        when(itemService.createItem(eq(1L), any())).thenReturn(dto);

        mvc.perform(post("/items").header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requestId").value(5));
    }

    @Test
    void editItem() throws Exception {
        when(itemService.editItem(eq(1L), eq(1L), any())).thenReturn(item());

        mvc.perform(patch("/items/1").header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenReturn(item());

        mvc.perform(get("/items/1").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemsByOwner() throws Exception {
        when(itemService.getItemsByOwner(1L)).thenReturn(List.of(item()));

        mvc.perform(get("/items").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItems("drill")).thenReturn(List.of(item()));

        mvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addComment() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Отлично");
        comment.setAuthorName("anna");
        when(itemService.addComment(eq(2L), eq(1L), any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment").header(USER_HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Отлично\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName").value("anna"));
    }
}