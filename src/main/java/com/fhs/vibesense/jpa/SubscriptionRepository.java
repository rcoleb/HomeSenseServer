package com.fhs.vibesense.jpa;

import com.fhs.vibesense.data.EventType;
import com.fhs.vibesense.data.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByEventTypeAndDeviceId(EventType eventType, Long deviceId);

    List<Subscription> findByDeviceId(Long id);

}
