package com.fhs.vibesense.service;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.DeviceType;
import com.fhs.vibesense.data.EventType;
import com.fhs.vibesense.data.Subscription;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@RestController
public class TwilioService {

    private final SubscriptionService subscriptionService;

    private final DeviceService deviceService;

    @Autowired
    public TwilioService(SubscriptionService subscriptionService, DeviceService deviceService) {
        this.subscriptionService = subscriptionService;
        this.deviceService = deviceService;
    }

    @PostMapping("/sms")
    public String receiveSMS(@RequestBody String requestBody) {
        // parse the incoming Twilio request to extract the message body and sender's phone number
        String messageBody = extractMessageBody(requestBody).strip();

        String userPhone = extractSenderPhoneNumber(requestBody).strip();

        String[] pts = messageBody.split("\\+");

        DeviceType dType;
        try {
            dType = DeviceType.valueOf(pts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            StringJoiner sj = new StringJoiner(", ");
            Arrays.stream(DeviceType.values()).map(DeviceType::name).forEach(sj::add);
            return "Error - invalid device type (" + pts[0] + "). Allowed types are: " + sj + ". Subscription format must be <DEVICE_TYPE> <EVENT_TYPE>.";
        }

        EventType eventType;
        try {
            eventType = EventType.valueOf(pts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            StringJoiner sj = new StringJoiner(", ");
            Arrays.stream(EventType.values()).map(EventType::name).forEach(sj::add);

            return "Error - invalid event type (" + pts[1] + "). Allowed types are: " + sj + ". Subscription format must be <DEVICE_TYPE> <EVENT_TYPE>.";
        }
        List<Device> devices = deviceService.getDevicesByType(dType);

        // create an event subscription for the user with the given phone number and message body
        for (Device device : devices) {
//            log.debug("Added subscription for device " + device.getId() + " (" + device.getDeviceType() + ") for user " + userPhone);
            subscriptionService.addSubscription(new Subscription(userPhone, eventType, device.getId()));
        }

        // send a response message back to Twilio to let them know we received the message
        return "You are now subscribed - the next time the " + pts[0] + " is " + pts[1] + ", you will be notified. Thanks!";
    }

    private String extractMessageBody(String requestBody) {
        int ind = requestBody.indexOf("&Body=");
        int ind2 = requestBody.indexOf('&', ind+6);
        return requestBody.substring(ind + 6, ind2);
    }

    private String extractSenderPhoneNumber(String requestBody) {
        int ind = requestBody.indexOf("&From=");
        int ind2 = requestBody.indexOf('&', ind+9);
        return requestBody.substring(ind + 9, ind2);
    }

}