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
 
package com.qtech.os.driver.bus.pci;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceToDriverMapper;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.plugin.ConfigurationElement;


/**
 * Generic mapper for PCI devices that match of baseclass + subclass
 * and an optional minor class.
 *
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class PCIClassToDriverMapper extends AbstractPCIDeviceToDriverMapper implements DeviceToDriverMapper {

    /**
     * @param config
     * @throws DriverException
     */
    public PCIClassToDriverMapper(ConfigurationElement config)
        throws DriverException {
        super(config);
    }

    /**
     * @see com.qtech.os.driver.DeviceToDriverMapper#findDriver(com.qtech.os.driver.Device)
     */
    public Driver findDriver(Device device) {
        if (!(device instanceof PCIDevice)) {
            return null;
        }
        final PCIDevice pciDev = (PCIDevice) device;
        final PCIDeviceConfig cfg = pciDev.getConfig();

        if (!matches(cfg.getBaseClass(), cfg.getSubClass(), cfg.getMinorClass())) {
            return null;
        } else {
            return newDriver(pciDev);
        }
    }

    /**
     * @see com.qtech.os.driver.DeviceToDriverMapper#getMatchLevel()
     */
    public int getMatchLevel() {
        return DeviceToDriverMapper.MATCH_DEVCLASS;
    }
}
