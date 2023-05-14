package com.fhs.vibesense.service;

import com.fhs.vibesense.data.Event;
import com.fhs.vibesense.data.Subscription;
import com.fhs.vibesense.jpa.SubscriptionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
@RestController
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostMapping("/sub")
    public void receiveSubscription(@RequestBody Subscription subscription) {
        addSubscription(subscription);
    }

    public Subscription addSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public void removeSubscription(Subscription subscription) {
        subscriptionRepository.delete(subscription);
    }

    public List<Subscription> getSubscriptionsForEvent(@NotNull Event event) {
        return subscriptionRepository.findByEventTypeAndDeviceId(event.getEventType(), event.getDeviceId());
    }

    public List<Subscription> getSubscriptionsForDevice(Long id) {
        return subscriptionRepository.findByDeviceId(id);
    }

    public List<Subscription> getAllSubscriptions() { return subscriptionRepository.findAll();  }
}
