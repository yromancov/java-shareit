package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.model.User;

import java.util.*;


public interface UserRepository {

    public List<User> getUsers();

    public Optional<User> getUserById(long userId);

    public User createUser(User user);


    public User update(User newUser);

    public void deleteUser(Long userId);

}
