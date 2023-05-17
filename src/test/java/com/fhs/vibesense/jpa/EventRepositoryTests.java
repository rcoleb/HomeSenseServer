package com.fhs.vibesense.jpa;

import com.fhs.vibesense.data.Event;
import com.fhs.vibesense.data.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

@SpringJUnitConfig
@DataJpaTest
public class EventRepositoryTests {

    @Autowired
    EventRepository eventRepository;

    @AfterEach
    @BeforeEach
    void empty() {
        eventRepository.deleteAll();
    }

    @Test
    public void testFindAllByDeviceId() throws InterruptedException {
        Event e1 = new Event(1L, LocalDateTime.now(), EventType.STARTED);
        eventRepository.save(e1);

        Thread.sleep(100);
        Event e2 = new Event(1L, LocalDateTime.now(), EventType.STOPPED);
        eventRepository.save(e2);

        Thread.sleep(100);
        Event e3 = new Event(1L, LocalDateTime.now(), EventType.DOOR_OPENED);
        eventRepository.save(e3);

        Thread.sleep(100);
        Event e4 = new Event(2L, LocalDateTime.now(), EventType.STARTED);
        eventRepository.save(e4);

        Thread.sleep(100);
        Event e5 = new Event(2L, LocalDateTime.now(), EventType.STOPPED);
        eventRepository.save(e5);

        Thread.sleep(100);
        Event e6 = new Event(2L, LocalDateTime.now(), EventType.DOOR_OPENED);
        eventRepository.save(e6);

        assertNotNull(e1.getId());
        assertNotNull(e2.getId());
        assertNotNull(e3.getId());
        assertNotNull(e4.getId());
        assertNotNull(e5.getId());
        assertNotNull(e6.getId());

        List<Event> d1E = eventRepository.findAllByDeviceId(1L);
        assertNotNull(d1E);
        assertEquals(d1E.size(), 3);

    }

    @Test
    public void testFindFirstByDeviceIdOrderByTimestamp() throws InterruptedException {
        Event e1 = new Event(1L, LocalDateTime.now(), EventType.STARTED);
        eventRepository.save(e1);

        Thread.sleep(100);
        Event e2 = new Event(1L, LocalDateTime.now(), EventType.STOPPED);
        eventRepository.save(e2);

        Thread.sleep(100);
        Event e3 = new Event(1L, LocalDateTime.now(), EventType.DOOR_OPENED);
        eventRepository.save(e3);

        Thread.sleep(100);
        Event e4 = new Event(2L, LocalDateTime.now(), EventType.STARTED);
        eventRepository.save(e4);

        Thread.sleep(100);
        Event e5 = new Event(2L, LocalDateTime.now(), EventType.STOPPED);
        eventRepository.save(e5);

        Thread.sleep(100);
        Event e6 = new Event(2L, LocalDateTime.now(), EventType.DOOR_OPENED);
        eventRepository.save(e6);

        assertNotNull(e1.getId());
        assertNotNull(e2.getId());
        assertNotNull(e3.getId());
        assertNotNull(e4.getId());
        assertNotNull(e5.getId());
        assertNotNull(e6.getId());

        List<Event> dE = eventRepository.findFirstGroupedByTimestamp();
        assertEquals(dE.size(), 2);
        assertEquals(dE.stream().map(e -> e.getEventType()).distinct().findFirst().get(), EventType.DOOR_OPENED);

    }

}
