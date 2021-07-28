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
 
package com.qtech.os.driver.net.eepro100;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver;
import com.qtech.os.driver.net.ethernet.spi.Flags;
import com.qtech.os.driver.net.spi.AbstractDeviceCore;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.system.resource.ResourceNotFreeException;


/**
 * @author flesire
 */
public class EEPRO100Driver extends BasicEthernetDriver {

    /**
     * Create a new instance
     */
    public EEPRO100Driver(ConfigurationElement config) {
        this(new EEPRO100Flags(config));
    }

    public EEPRO100Driver(EEPRO100Flags flags) {
        this.flags = flags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.qtech.os.driver.net.ethernet.BasicEthernetDriver#newCore(com.qtech.os.driver.Device,
     *      com.qtech.os.driver.net.ethernet.Flags)
     */
    protected AbstractDeviceCore newCore(Device device, Flags flags) throws DriverException, ResourceNotFreeException {
        return new EEPRO100Core(this, device, (PCIDevice) device, (EEPRO100Flags) flags);
    }

}
