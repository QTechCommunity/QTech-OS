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
 
package com.qtech.os.driver.net.loopback;

import com.qtech.os.driver.Bus;
import com.qtech.os.driver.DeviceException;
import com.qtech.os.driver.DeviceFinder;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DriverException;

/**
 * @author epr
 */
public class LoopbackFinder implements DeviceFinder {

    /**
     * @see com.qtech.os.driver.DeviceFinder#findDevices(com.qtech.os.driver.DeviceManager, com.qtech.os.driver.Bus)
     */
    public void findDevices(DeviceManager devMan, Bus bus) throws DeviceException {
        try {
            devMan.register(new LoopbackDevice(bus));
        } catch (DriverException ex) {
            throw new DeviceException(ex);
        }
    }

}
