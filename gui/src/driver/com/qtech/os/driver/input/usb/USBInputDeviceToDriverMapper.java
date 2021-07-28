/*
 * $Id$
 *
 * Copyright (C) 2003-2015 QTech Community
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package com.qtech.os.driver.input.usb;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceToDriverMapper;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.bus.usb.InterfaceDescriptor;
import com.qtech.os.driver.bus.usb.USBConfiguration;
import com.qtech.os.driver.bus.usb.USBConstants;
import com.qtech.os.driver.bus.usb.USBDevice;
import com.qtech.os.driver.bus.usb.USBInterface;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class USBInputDeviceToDriverMapper implements DeviceToDriverMapper, USBConstants,
        USBHidConstants {

    /**
     * @see com.qtech.os.driver.DeviceToDriverMapper#findDriver(com.qtech.os.driver.Device)
     */
    public Driver findDriver(Device device) {
        if (device instanceof USBDevice) {
            final USBDevice dev = (USBDevice) device;
            if (dev.getDescriptor().getDeviceClass() == USB_CLASS_PER_INTERFACE) {

                final USBConfiguration conf = dev.getConfiguration(0);
                final USBInterface intf = conf.getInterface(0);
                final InterfaceDescriptor descr = intf.getDescriptor();

                if (descr.getInterfaceClass() == USB_CLASS_HID) {
                    if (descr.getInterfaceSubClass() == HID_SUBCLASS_BOOT_INTERFACE) {
                        switch (descr.getInterfaceProtocol()) {
                            case HID_PROTOCOL_KEYBOARD:
                                // We found an USB keyboard
                                return new USBKeyboardDriver();
                            case HID_PROTOCOL_MOUSE:
                                // We found an USB mouse
                                return new USBMouseDriver();
                            default:
                                return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the matching level of this mapper. The mappers are queried in order
     * of match level. This will ensure the best available driver for a device.
     * 
     * @return One of the MATCH_xxx constants.
     * @see #MATCH_DEVICE_REVISION
     * @see #MATCH_DEVICE
     * @see #MATCH_DEVCLASS
     */
    public int getMatchLevel() {
        return MATCH_DEVCLASS;
    }
}
