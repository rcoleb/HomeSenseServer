package com.fhs.vibesense.data;

import com.twilio.http.TwilioRestClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TwilioConfig {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.fromPhoneNumber}")
    private String fromPhoneNumber;

    @Bean
    public TwilioRestClient twilioRestClient() {
        return new TwilioRestClient.Builder(accountSid, authToken).build();
    }
}
