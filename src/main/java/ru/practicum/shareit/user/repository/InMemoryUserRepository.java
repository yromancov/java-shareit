package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
@Repository
public class InMemoryUserRepository implements UserRepository {
    private Map<Long, User> userStorage = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override

    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(userStorage.get(userId));
    }

    @Override
    public User createUser(User user) {
        checkEmailUnique(user);
        user.setId(getNextId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        checkEmailUnique(newUser);
        User oldUser = userStorage.get(newUser.getId());
        if (oldUser == null) {
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        userStorage.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    @Override
    public User deleteUser(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        userStorage.remove(user.getId());
        return user;
    }

    private long getNextId() {
        long currentMaxId = userStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void checkEmailUnique(User user) {
        boolean emailExists = userStorage.values().stream()
                .anyMatch(existingUser -> existingUser.getEmail().equalsIgnoreCase(user.getEmail())
                        && !existingUser.getId().equals(user.getId()));

        if (emailExists) {
            throw new DuplicateDataException("Этот email уже используется");
        }
    }
}
