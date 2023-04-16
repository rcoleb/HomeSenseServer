package com.fhs.vibesense.service;

import com.fhs.vibesense.data.*;
import com.fhs.vibesense.jpa.DeviceRepository;
import com.fhs.vibesense.jpa.EventRepository;
import com.fhs.vibesense.jpa.UserRepository;
import com.fhs.vibesense.data.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SubscriptionService subscriptionService;
    private final TwilioConfig twilioConfig;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    public EventService(EventRepository eventRepository,
                        SubscriptionService subscriptionService,
                        TwilioConfig twilioConfig,
                        UserRepository userRepository, DeviceRepository deviceRepository) {
        this.eventRepository = eventRepository;
        this.subscriptionService = subscriptionService;
        this.twilioConfig = twilioConfig;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }

    public void processEvent(Event event) {
        // TODO log
        eventRepository.save(event);
        sendNotifications(event);
    }

    private void sendNotifications(Event event) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsForEvent(event);
        for (Subscription subscription : subscriptions) {
            User user = getUserById(subscription.getUserPhone());
            String message = getMessageForEvent(event);
            sendNotification(user.getPhoneNumber(), message);
        }
    }

    private User getUserById(String userPhone) {
        return userRepository.findById(userPhone).orElseThrow(() -> new RuntimeException("User not found, phone number: " + userPhone));
    }

    private Device getDeviceById(Long deviceId) {
        return deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found, id: " + deviceId));
    }

    private String getMessageForEvent(@NotNull Event event) {
        String deviceType = getDeviceById(event.getDeviceId()).getDeviceType().name().toLowerCase();
        String eventType = event.getEventType().toString().toLowerCase().replaceAll("_", " ");
        String message = String.format("Your %s %s", deviceType, eventType);
        return message;
    }

    private void sendNotification(String phoneNumber, String message) {
        // TODO log
        MessageCreator messageCreator = Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioConfig.getFromPhoneNumber()), message);
        messageCreator.create(twilioConfig.twilioRestClient());
    }

    public List<Event> getEventsForDevice(Long id) {
        return eventRepository.findAllByDeviceId(id);
    }

    public void deleteEvent(Event event) {
        eventRepository.deleteById(event.getId());
    }
}