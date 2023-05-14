package com.fhs.vibesense.service;

import ch.qos.logback.core.testUtil.MockInitialContext;
import com.fhs.vibesense.data.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureTestDatabase
public class EventServiceTests {

    @Autowired
    DeviceService deviceService;

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    UserService userService;

    @Autowired
    EventService eventService;

    @Test
    public void testProcessEvent() {
        EventService eventService1 = Mockito.spy(eventService);
        String phoneNumber = "15005550006";
        userService.addUser(new User(phoneNumber));
        Device dWasher = new Device(1L, DeviceType.WASHER);
        deviceService.addDevice(dWasher);
        Device dDryer = new Device(3L, DeviceType.DRYER);
        deviceService.addDevice(dDryer);
        Subscription subscription = new Subscription(phoneNumber, EventType.STOPPED, dWasher.getId());
        subscriptionService.addSubscription(subscription);
        Event event = new Event(null, dWasher.getId(), LocalDateTime.now(), EventType.STOPPED);

        Mockito.doAnswer(invocation -> {
            System.out.println((String) invocation.getArgument(1));
            return null;
        }).when(eventService1).sendNotification(Mockito.anyString(), Mockito.anyString());
        eventService1.processEvent(event);

        assertNotNull(event.getId());
    }

}
