package ru.practicum.shareit.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;


public interface UserRepository extends JpaRepository<User,Long> {

//    public List<User> getUsers();
//
//    public Optional<User> getUserById(long userId);
//
//    public User createUser(User user);
//
//
//    public User update(User newUser);
//
//    public void deleteUser(Long userId);

}
