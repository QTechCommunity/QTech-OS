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
 
package com.qtech.os.driver.system.acpi;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceToDriverMapper;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.system.firmware.AcpiDevice;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public final class AcpiDeviceToDriverMapper implements DeviceToDriverMapper {

    /**
     * Find a driver for the given device, or return null if not found.
     *
     * @see com.qtech.os.driver.DeviceToDriverMapper#findDriver(com.qtech.os.driver.Device)
     */
    public Driver findDriver(Device device) {
        if (device instanceof AcpiDevice) {
            return new AcpiDriver();
        }

        // No driver found
        return null;
    }

    /**
     * Gets the matching level for this mapper.
     *
     * @see com.qtech.os.driver.DeviceToDriverMapper#getMatchLevel()
     */
    public int getMatchLevel() {
        return DeviceToDriverMapper.MATCH_DEVCLASS;
    }
}
