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
 
package com.qtech.os.driver.chipset.via;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.util.NumberUtils;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class Via82C686 extends Driver {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(Via82C686.class);

    /**
     * Start the device
     *
     * @throws DriverException
     */
    protected void startDevice() throws DriverException {
        // TODO apply io-apic quirk

        final PCIDevice dev = (PCIDevice) getDevice();
        for (int i = 0x55; i <= 0x58; i++) {
            final int v = dev.readConfigByte(i);
            log.debug("PCI[" + NumberUtils.hex(i, 2) + "] " + NumberUtils.hex(v, 2));
        }

    }

    /**
     * Stop the device
     *
     * @throws DriverException
     */
    protected void stopDevice() throws DriverException {
        // Nothing to do here
    }
}
