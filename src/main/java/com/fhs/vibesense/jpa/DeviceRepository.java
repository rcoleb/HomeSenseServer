package com.fhs.vibesense.jpa;

import com.fhs.vibesense.data.Device;
import com.fhs.vibesense.data.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByDeviceType(DeviceType deviceType);

}
