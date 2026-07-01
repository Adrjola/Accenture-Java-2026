package com.bootcamp.smarthome.controller;

import com.bootcamp.smarthome.device.Device;
import com.bootcamp.smarthome.exception.HomeAutomationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController {

    public static final int MAX_DEVICES = 8;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final Device[] devices = new Device[MAX_DEVICES];
    private int deviceCount = 0;

    public void addDevice(Device device) {
        if (deviceCount >= MAX_DEVICES) {
            throw new IllegalStateException(
                    "Cannot add device '" + device.getDeviceId() +
                    "': controller is at maximum capacity (" + MAX_DEVICES + ").");
        }

        devices[deviceCount] = device;
        deviceCount++;
        System.out.println("Device registered: " + device);
    }

    public Device findDevice(String deviceId) {
        for (int i = 0; i < deviceCount; i++) {
            if (devices[i] != null && devices[i].getDeviceId().equals(deviceId)) {
                return devices[i];
            }
        }

        return null;
    }

    public void sendCommand(String fullCommand) throws HomeAutomationException {
        String deviceId = "unknown";

        try {
            deviceId = CommandParser.extractDeviceId(fullCommand);
            String command = CommandParser.extractCommand(fullCommand);
            logger.debug("Command received for device '{}': {}", deviceId, fullCommand);

            Device device = findDevice(deviceId);

            if (device == null) {
                logger.error("Device not found: {}", deviceId);
                return;
            }

            if (!device.isOnline()) {
                logger.warn("Device '{}' is offline - command skipped.", deviceId);
                return;
            }

            device.executeCommand(command);
            logger.info("Command executed successfully for device '{}'", deviceId);
        } catch (HomeAutomationException exception) {
            logger.error("Exception caught during command processing", exception);
            throw new HomeAutomationException(
                    "Command '" + fullCommand + "' failed for device '" + deviceId + "'",
                    exception
            );
        } finally {
            System.out.println("Command processing ended for device [" + deviceId + "]");
        }
    }

    public void printAllDevices() {
        System.out.println("=== Registered Devices (" + deviceCount + "/" + MAX_DEVICES + ") ===");
        for (int i = 0; i < deviceCount; i++) {
            System.out.println("  " + devices[i]);
        }
    }

    public int getDeviceCount() {
        return deviceCount;
    }
}
