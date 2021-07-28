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
 
package com.qtech.os.driver.net.lance;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver;
import com.qtech.os.driver.net.ethernet.spi.Flags;
import com.qtech.os.driver.net.spi.AbstractDeviceCore;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.system.resource.ResourceNotFreeException;

/**
 * @author epr
 */
public class LanceDriver extends BasicEthernetDriver {

    public LanceDriver(ConfigurationElement config) {
        this(new LanceFlags(config));
    }

    public LanceDriver(LanceFlags flags) {
        this.flags = flags;
    }

    /**
     * Create a new LanceCore instance
     */
    protected AbstractDeviceCore newCore(Device device, Flags flags)
        throws DriverException, ResourceNotFreeException {
        return new LanceCore(this, device, (PCIDevice) device, flags);
    }
}
