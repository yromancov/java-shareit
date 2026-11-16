package ru.practicum.shareit.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private UserDto requestor;
    private UserDto owner;

    @BeforeEach
    void setUp() {
        requestor = userService.createUser(TestData.user("requestor"));
        owner = userService.createUser(TestData.user("owner"));
    }

    private ItemRequestDto request(long userId, String description) {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription(description);
        return itemRequestService.createRequests(userId, dto);
    }

    private ItemDto answer(long requestId) {
        ItemDto dto = TestData.item("Drill");
        dto.setRequestId(requestId);
        return itemService.createItem(owner.getId(), dto);
    }

    @Test
    void createRequests() {
        ItemRequestDto saved = request(requestor.getId(), "Нужна дрель");

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Нужна дрель");
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void getAllRequestsFromUser() {
        ItemRequestDto older = request(requestor.getId(), "Нужна дрель");
        ItemRequestDto newer = request(requestor.getId(), "Нужна пила");
        ItemDto item = answer(older.getId());

        List<ItemRequestDto> result = itemRequestService.getAllRequestsFromUser(requestor.getId());

        assertThat(result).extracting(ItemRequestDto::getId).containsExactly(newer.getId(), older.getId());
        assertThat(result.get(1).getItems().get(0).getId()).isEqualTo(item.getId());
        assertThat(result.get(1).getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(result.get(1).getItems().get(0).getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void getAllRequestsWithoutUser() {
        ItemRequestDto foreign = request(requestor.getId(), "Нужна дрель");
        request(owner.getId(), "Нужен молоток");

        assertThat(itemRequestService.getAllRequestsWithoutUser(owner.getId()))
                .extracting(ItemRequestDto::getId).containsExactly(foreign.getId());
    }

    @Test
    void getRequestById() {
        ItemRequestDto saved = request(requestor.getId(), "Нужна дрель");
        answer(saved.getId());

        ItemRequestDto result = itemRequestService.getRequestById(saved.getId());

        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getItems()).hasSize(1);
    }
}