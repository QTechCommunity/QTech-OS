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
 
package com.qtech.os.driver.bus.firewire;

import javax.naming.NameNotFoundException;
import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.naming.InitialNaming;

/**
 * Driver for a FireWire controller.
 *
 * @author markhale
 */
public class FireWireDriver extends Driver {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(FireWireDriver.class);
    private FireWireBus bus;

    public FireWireDriver() {
    }

    protected void startDevice() throws DriverException {
        final Device device = getDevice();
        final DeviceManager dm;
        try {
            dm = InitialNaming.lookup(DeviceManager.NAME);
            dm.rename(device, getDevicePrefix(), true);
        } catch (DeviceAlreadyRegisteredException ex) {
            log.error("Cannot rename device", ex);
            throw new DriverException("Cannot rename device", ex);
        } catch (NameNotFoundException ex) {
            throw new DriverException("Cannot find DeviceManager", ex);
        }
        int busId = 0; // fix to handle multiple firewire controllers
        bus = new FireWireBus(this, busId);
    }

    protected void stopDevice() throws DriverException {
        /** @todo Implement this com.qtech.os.driver.Driver abstract method */
    }

    protected String getDevicePrefix() {
        return "firewire";
    }
}
