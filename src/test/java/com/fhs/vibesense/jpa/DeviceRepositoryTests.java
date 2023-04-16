package com.fhs.vibesense.jpa;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.DeviceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@DataJpaTest
public class DeviceRepositoryTests {

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    public void testSaveDevice() {
        // Given
        Device device = new Device(null, DeviceType.WASHER);

        // When
        deviceRepository.save(device);

        // Then
        assertNotNull(device.getId());
    }

    @Test
    public void testFindDeviceById() {
        // Given
        Device device = new Device(null, DeviceType.WASHER);
        deviceRepository.save(device);

        // When
        Device foundDevice = deviceRepository.findById(device.getId()).orElse(null);

        // Then
        assertNotNull(foundDevice);
        assertEquals(foundDevice.getDeviceType(), DeviceType.WASHER);
        assertEquals(foundDevice.getId(), device.getId());
    }

    @Test
    public void testDeleteDevice() {
        // Given
        Device device = new Device(null, DeviceType.WASHER);
        deviceRepository.save(device);

        // When
        deviceRepository.delete(device);

        // Then
        assertTrue(deviceRepository.findById(device.getId()).isEmpty());
    }

    @Test
    public void testFindAllDevices() {
        // Given
        Device device1 = new Device(null, DeviceType.WASHER);
        Device device2 = new Device(null, DeviceType.DRYER);
        deviceRepository.saveAll(Arrays.asList(device1, device2));

        // When
        List<Device> devices = deviceRepository.findAll();

        // Then
        assertEquals(devices.size(), 2);
        assertTrue(devices.containsAll(Arrays.asList(device1, device2)));
    }
}
