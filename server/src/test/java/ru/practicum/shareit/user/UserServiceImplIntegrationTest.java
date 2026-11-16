package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        UserDto saved = userService.createUser(TestData.user("anna"));

        assertThat(saved.getId()).isNotNull();
        assertThat(userService.getUserById(saved.getId()).getEmail()).isEqualTo("anna@mail.ru");
    }

    @Test
    void updateUser() {
        UserDto saved = userService.createUser(TestData.user("anna"));
        UserDto patch = new UserDto();
        patch.setName("Anna New");

        UserDto updated = userService.updateUser(saved.getId(), patch);

        assertThat(updated.getName()).isEqualTo("Anna New");
        assertThat(updated.getEmail()).isEqualTo("anna@mail.ru");
    }
}