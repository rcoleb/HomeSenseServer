package com.fhs.vibesense.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.DeviceType;
import com.fhs.vibesense.data.User;
import com.fhs.vibesense.jpa.DeviceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureTestDatabase
public class DeviceServiceTests {

    @Autowired
    DeviceService deviceService;

    @AfterEach
    @BeforeEach
    void empty() {
        for (Device d : deviceService.getAllDevices()) {
            deviceService.removeDevice(d);
        }
    }

    @Test
    void testAddDevice() {
        Device testDevice = new Device(1L, DeviceType.DRYER);
        deviceService.addDevice(testDevice);

        assertNotNull(testDevice.getId());
        Optional<Device> actual = deviceService.getDeviceById(testDevice.getId());
        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testDevice);
    }

    @Test
    void testRemoveDevice() {
        Device testDevice = new Device(1L, DeviceType.WASHER);
        deviceService.addDevice(testDevice);
        Optional<Device> actual = deviceService.getDeviceById(testDevice.getId());
        assertTrue(actual.isPresent());

        deviceService.removeDevice(testDevice);

        assertTrue(deviceService.getDeviceById(testDevice.getId()).isEmpty());
    }

    @Test
    void testGetAllDevices() {
        Device testDevice1 = new Device(1L, DeviceType.WASHER);
        Device testDevice2 = new Device(2L, DeviceType.DRYER);
        List<Device> userList = new ArrayList<>();
        userList.add(testDevice1);
        userList.add(testDevice2);

        deviceService.addDevice(testDevice1);
        deviceService.addDevice(testDevice2);

        assertNotNull(testDevice1.getId());
        assertNotNull(testDevice2.getId());

        List<Device> result = deviceService.getAllDevices();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(userList));
    }

    @Test
    void testGetDevicesByDeviceType() {
        Device testDevice1 = new Device(1L, DeviceType.WASHER);
        Device testDevice2 = new Device(2L, DeviceType.WASHER);
        Device testDevice3 = new Device(3L, DeviceType.DRYER);
        deviceService.addDevice(testDevice1);
        deviceService.addDevice(testDevice2);
        deviceService.addDevice(testDevice3);

        assertNotNull(testDevice1.getId());
        assertNotNull(testDevice2.getId());
        assertNotNull(testDevice3.getId());

        List<Device> devices = deviceService.getDevicesByType(DeviceType.WASHER);

        assertEquals(devices.size(), 2);
        assertTrue(devices.containsAll(Arrays.asList(testDevice1, testDevice2)));
    }

}

