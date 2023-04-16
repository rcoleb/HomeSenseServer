package com.fhs.vibesense.service;

import com.fhs.vibesense.data.*;
import com.fhs.vibesense.jpa.DeviceRepository;
import com.fhs.vibesense.jpa.EventRepository;
import com.fhs.vibesense.jpa.UserRepository;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


public class EventServiceTests {

}
