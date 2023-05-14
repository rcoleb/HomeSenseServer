package com.fhs.vibesense.service;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.DeviceType;
import com.fhs.vibesense.jpa.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Service
@RestController
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @PostMapping("/device")
    public ResponseEntity<String> upsertDevice(@RequestBody Device deviceBody) {
        addDevice(deviceBody);
        return ResponseEntity.ok("Device added successfully");
    }

    public Device addDevice(Device device) {
        return deviceRepository.save(device);
    }

    public void removeDevice(Device device) {
        deviceRepository.delete(device);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    public List<Device> getDevicesByType(DeviceType type) {
        return deviceRepository.findByDeviceType(type);
    }
}
