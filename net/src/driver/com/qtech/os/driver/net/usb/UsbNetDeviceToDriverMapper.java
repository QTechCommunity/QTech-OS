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
 
package com.qtech.os.driver.net.usb;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceToDriverMapper;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.bus.usb.InterfaceDescriptor;
import com.qtech.os.driver.bus.usb.USBConfiguration;
import com.qtech.os.driver.bus.usb.USBDevice;
import com.qtech.os.driver.bus.usb.USBInterface;
import com.qtech.os.driver.net.usb.bluetooth.UsbBluetoothDriver;

/**
 * This class define driver finder for USB Wireless device.
 * 
 * @author fabien L.
 */
public class UsbNetDeviceToDriverMapper implements DeviceToDriverMapper, UsbNetConstant {

    private static final Logger log = Logger.getLogger(UsbNetDeviceToDriverMapper.class);

    public Driver findDriver(Device device) {

        if (!(device instanceof USBDevice)) {
            return null;
        }
        final USBDevice dev = (USBDevice) device;
        final USBConfiguration conf = dev.getConfiguration(0);
        final USBInterface intf = conf.getInterface(0);
        final InterfaceDescriptor descr = intf.getDescriptor();
        if (descr.getInterfaceClass() != USB_CLASS_WIRELESS) {
            return null;
        }
        log.debug("Found USB wireless device.");
        if (descr.getInterfaceSubClass() == US_SC_RF) {
            log.debug("Subclass " + descr.getInterfaceSubClass());
            return new UsbBluetoothDriver();
        }
        return null;
    }

    public int getMatchLevel() {
        return MATCH_DEVCLASS;
    }

}
