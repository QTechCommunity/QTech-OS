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
 
package com.qtech.os.driver.bus.ide.atapi;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceToDriverMapper;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.bus.ide.IDEDevice;


/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class ATAPIDeviceToDriverMapper implements DeviceToDriverMapper {

    /**
     * @see com.qtech.os.driver.DeviceToDriverMapper#findDriver(com.qtech.os.driver.Device)
     */
    public Driver findDriver(Device device) {
        if (device instanceof IDEDevice) {
            final IDEDevice ideDev = (IDEDevice) device;
            if (ideDev.getDescriptor().isAtapi()) {
                return new ATAPIDriver();
            }
        }
        return null;
    }

    /**
     * @see com.qtech.os.driver.DeviceToDriverMapper#getMatchLevel()
     */
    public int getMatchLevel() {
        return DeviceToDriverMapper.MATCH_DEVCLASS;
    }
}
