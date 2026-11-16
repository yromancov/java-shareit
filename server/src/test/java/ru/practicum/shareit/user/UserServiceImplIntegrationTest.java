package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ParameterNotValidException;

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

    @Test
    void createUserWithDuplicateEmail() {
        userService.createUser(TestData.user("anna"));

        assertThatThrownBy(() -> userService.createUser(TestData.user("anna")))
                .isInstanceOf(DuplicateDataException.class);
    }

    @Test
    void createUserWithBlankEmail() {
        UserDto dto = TestData.user("anna");
        dto.setEmail(" ");

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void updateUserWithEmailOfAnotherUser() {
        UserDto anna = userService.createUser(TestData.user("anna"));
        userService.createUser(TestData.user("boris"));
        UserDto patch = new UserDto();
        patch.setEmail("boris@mail.ru");

        assertThatThrownBy(() -> userService.updateUser(anna.getId(), patch))
                .isInstanceOf(DuplicateDataException.class);
    }

    @Test
    void updateUserEmail() {
        UserDto anna = userService.createUser(TestData.user("anna"));
        UserDto patch = new UserDto();
        patch.setEmail("new@mail.ru");

        assertThat(userService.updateUser(anna.getId(), patch).getEmail()).isEqualTo("new@mail.ru");
    }

    @Test
    void updateUnknownUser() {
        assertThatThrownBy(() -> userService.updateUser(999L, new UserDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUsers() {
        userService.createUser(TestData.user("anna"));
        userService.createUser(TestData.user("boris"));

        assertThat(userService.getUsers()).hasSize(2);
    }

    @Test
    void deleteUser() {
        UserDto saved = userService.createUser(TestData.user("anna"));

        userService.deleteUser(saved.getId());

        assertThatThrownBy(() -> userService.getUserById(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
