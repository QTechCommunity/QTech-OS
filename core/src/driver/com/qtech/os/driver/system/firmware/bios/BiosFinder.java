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
 
package com.qtech.os.driver.system.firmware.bios;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Bus;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceException;
import com.qtech.os.driver.DeviceFinder;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DriverException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public final class BiosFinder implements DeviceFinder {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(BiosFinder.class);

    /**
     * @param devMan
     * @param bus
     * @throws DeviceException
     * @see com.qtech.os.driver.DeviceFinder#findDevices(com.qtech.os.driver.DeviceManager, com.qtech.os.driver.Bus)
     */
    public void findDevices(DeviceManager devMan, Bus bus) throws DeviceException {
        try {
            final Device bios = new Device(bus, "bios");
            bios.setDriver(new BiosDriver());
            devMan.register(bios);
        } catch (DriverException ex) {
            throw new DeviceException(ex);
        }
    }

}
