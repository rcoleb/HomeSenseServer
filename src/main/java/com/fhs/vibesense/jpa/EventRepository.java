package com.fhs.vibesense.jpa;

import com.fhs.vibesense.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDeviceId(Long deviceId);

    @Query(value = "SELECT DISTINCT ON (device_id) id, device_id, timestamp, event_type FROM event ORDER BY timestamp DESC", nativeQuery = true)
    List<Event> findFirstGroupedByTimestamp();

}
