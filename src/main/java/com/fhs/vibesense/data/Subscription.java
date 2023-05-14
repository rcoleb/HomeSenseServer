package com.fhs.vibesense.data;

import com.fhs.vibesense.service.SubscriptionService;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription")
public class Subscription {

    public Subscription(String userPhone, EventType eventType, Long deviceId) {
        this.userPhone = userPhone;
        this.eventType = eventType;
        this.deviceId = deviceId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_phone")
    private String userPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "device_id")
    private Long deviceId;
}