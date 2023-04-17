package com.fhs.vibesense.service;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.User;
import com.fhs.vibesense.jpa.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @AfterEach
    @BeforeEach
    void empty() {
        for (User u : userService.getAllUsers()) {
            userService.removeUser(u);
        }
    }

    private static final Random rand = new Random();
    private static String getRandomUserPhone() {
        StringJoiner sj = new StringJoiner("");
        sj.add("1");
        for (int i = 0; i < 11; i++) {
            sj.add(Integer.toString(rand.nextInt(0, 10)));
        }
        return sj.toString();
    }

    @Test
    void testAddUser() {
        User testUser = new User(getRandomUserPhone());
        userService.addUser(testUser);

        Optional<User> actual = userService.getUserByPhoneNumber(testUser.getPhoneNumber());
        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testUser);
    }

    @Test
    void testRemoveUser() {
        User testUser = new User(getRandomUserPhone());
        userService.addUser(testUser);

        userService.removeUser(testUser);

        assertTrue(userService.getUserByPhoneNumber(testUser.getPhoneNumber()).isEmpty());
    }

    @Test
    void testGetAllUsers() {
        User testUser1 = new User(getRandomUserPhone());
        User testUser2 = new User(getRandomUserPhone());
        List<User> userList = new ArrayList<>();
        userList.add(testUser1);
        userList.add(testUser2);
        userService.addUser(testUser1);
        userService.addUser(testUser2);

        List<User> result = userService.getAllUsers();

        assertEquals(result.size(), 2);
        assertTrue(result.containsAll(userList));
    }

    @Test
    void testGetUserByPhoneNumber() {
        User testUser = new User(getRandomUserPhone());
        userService.addUser(testUser);

        Optional<User> result = userService.getUserByPhoneNumber(testUser.getPhoneNumber());

        assertEquals(Optional.of(testUser), result);
    }
}