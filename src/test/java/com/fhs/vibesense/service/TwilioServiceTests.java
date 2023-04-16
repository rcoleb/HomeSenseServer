package com.fhs.vibesense.service;

import com.fhs.vibesense.data.DeviceType;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringJUnitConfig
//@AutoConfigureTestDatabase
//@WebMvcTest(value = TwilioService.class)
public class TwilioServiceTests {


    @Test
    public void testParseMessageBody() {
        StringJoiner sj = new StringJoiner(", ");
        Arrays.stream(DeviceType.values()).map(DeviceType::name).forEach(sj::add);
        MessagingResponse twiml = new MessagingResponse.Builder()
                .message(new Message.Builder()
                        .body(new Body.Builder()
                                .build())
                        .build())
                .build();

        System.out.println(twiml.toXml());
        System.out.println(twiml.toString());
    }

}
