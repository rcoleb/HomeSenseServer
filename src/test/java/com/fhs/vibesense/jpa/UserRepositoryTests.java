package com.fhs.vibesense.jpa;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.DeviceType;
import com.fhs.vibesense.data.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        // Given
        User user = new User("1234567890");

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved);
        assertEquals(user, saved);
    }

    @Test
    public void testFindUsersByPhone() {
        // Given
        User user = new User("1234567890");
        User saved = userRepository.save(user);

        // When
        User foundUser = userRepository.findById(user.getPhoneNumber()).orElse(null);

        // Then
        assertNotNull(foundUser);
        assertEquals(foundUser.getPhoneNumber(), user.getPhoneNumber());
    }

    @Test
    public void testDeleteUser() {
        // Given
        User user = new User("1234567890");
        userRepository.save(user);

        // When
        userRepository.delete(user);

        // Then
        assertTrue(userRepository.findById(user.getPhoneNumber()).isEmpty());
    }

    @Test
    public void testFindAllUsers() {
        // Given
        User user1 = new User("1234567890");
        User user2 = new User("4567890123");
        userRepository.saveAll(Arrays.asList(user1, user2));

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertEquals(users.size(), 2);
        assertTrue(users.containsAll(Arrays.asList(user1, user2)));
    }
}
