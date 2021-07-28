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
 
package com.qtech.os.driver.block.usb.storage.scsi;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceToDriverMapper;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.block.usb.storage.USBStorageSCSIHostDriver.USBStorageSCSIDevice;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class USBStorageSCSIDeviceToDriverMapper implements DeviceToDriverMapper {
    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(USBStorageSCSIDeviceToDriverMapper.class);

    /**
     * @see com.qtech.os.driver.DeviceToDriverMapper#findDriver(com.qtech.os.driver.Device)
     */
    public Driver findDriver(Device device) {
        log.debug("*** USBStorageSCSIDeviceToDriverMapper::findDriver ***");
        if (device instanceof USBStorageSCSIDevice) {
            return new USBStorageSCSIDriver();
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
