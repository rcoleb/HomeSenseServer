package com.fhs.vibesense.service;

import com.fhs.vibesense.data.Event;
import com.fhs.vibesense.data.EventType;
import com.fhs.vibesense.data.Subscription;
import com.fhs.vibesense.data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureTestDatabase
public class SubscriptionServiceTests {

    @Autowired
    SubscriptionService subscriptionService;

    @BeforeEach
    @AfterEach
    void empty() {
        for (Subscription s : subscriptionService.getAllSubscriptions()) {
            subscriptionService.removeSubscription(s);
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
    void testAddSubscription() {
        Subscription subscription = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        subscriptionService.addSubscription(subscription);

        assertNotNull(subscription.getId());

        List<Subscription> found = subscriptionService.getAllSubscriptions();

        assertEquals(1, found.size());
        assertEquals(subscription, found.get(0));
    }

    @Test
    void testGetAllSubscriptions() {
        Subscription subscription1 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        Subscription subscription2 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        Subscription subscription3 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        subscriptionService.addSubscription(subscription1);
        subscriptionService.addSubscription(subscription2);
        subscriptionService.addSubscription(subscription3);

        assertNotNull(subscription1.getId());
        assertNotNull(subscription2.getId());
        assertNotNull(subscription3.getId());

        List<Subscription> result = subscriptionService.getAllSubscriptions();

        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList(subscription1, subscription2, subscription3)));
    }

    @Test
    void testRemoveSubscriptionOneSub() {
        Subscription subscription = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        subscriptionService.addSubscription(subscription);

        assertNotNull(subscription.getId());
        subscriptionService.removeSubscription(subscription);

        assertTrue(subscriptionService.getAllSubscriptions().isEmpty());
    }

    @Test
    void testRemoveSubscriptionMultipleSubs() {
        Subscription subscription1 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        Subscription subscription2 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        Subscription subscription3 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        subscriptionService.addSubscription(subscription1);
        subscriptionService.addSubscription(subscription2);
        subscriptionService.addSubscription(subscription3);

        assertNotNull(subscription1.getId());
        assertNotNull(subscription2.getId());
        assertNotNull(subscription3.getId());
        subscriptionService.removeSubscription(subscription1);

        assertEquals(2, subscriptionService.getAllSubscriptions().size());
    }

    @Test
    void testGetSubscriptionByEvent() {
        Subscription subscription1 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        Subscription subscription2 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 2L);
        subscriptionService.addSubscription(subscription1);
        subscriptionService.addSubscription(subscription2);

        assertNotNull(subscription1.getId());
        assertNotNull(subscription2.getId());

        Event event = new Event(1L, 1L, LocalDateTime.now(), EventType.STOPPED);

        List<Subscription> result = subscriptionService.getSubscriptionsForEvent(event);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(subscription1, result.get(0));
    }

    @Test
    void testGetSubscriptionByDevice() {
        Subscription subscription1 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 1L);
        Subscription subscription2 = new Subscription(getRandomUserPhone(), EventType.DOOR_OPENED, 1L);
        Subscription subscription3 = new Subscription(getRandomUserPhone(), EventType.STOPPED, 2L);
        subscriptionService.addSubscription(subscription1);
        subscriptionService.addSubscription(subscription2);
        subscriptionService.addSubscription(subscription3);

        assertNotNull(subscription1.getId());
        assertNotNull(subscription2.getId());
        assertNotNull(subscription3.getId());

        List<Subscription> result = subscriptionService.getSubscriptionsForDevice(1L);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(subscription1, subscription2)));
    }

}
