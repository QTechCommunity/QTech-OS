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

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.naming.NameNotFoundException;
import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.system.firmware.AcpiDevice;
import com.qtech.os.driver.system.firmware.AcpiRSDPInfo;
import com.qtech.os.driver.system.firmware.FirmwareAPI;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.system.resource.MemoryResource;
import com.qtech.os.system.resource.MemoryScanner;
import com.qtech.os.system.resource.ResourceManager;
import com.qtech.os.system.resource.ResourceNotFreeException;
import com.qtech.os.system.resource.ResourceOwner;
import org.vmmagic.unboxed.Address;

/**
 * BIOS firmware driver implementation.
 *
 * @author Francois-Frederic Ozog
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
final class BiosDriver extends Driver implements FirmwareAPI {

    private static final Logger log = Logger.getLogger(BiosDriver.class);
    private AcpiRSDPInfo info;
    private AcpiDevice acpiDevice;

    /**
     * Initialize this instance.
     */
    public BiosDriver() {
    }

    /**
     * Start the device
     *
     * @throws DriverException
     */
    protected void startDevice() throws DriverException {
        final Device dev = getDevice();

        // Find the ACPI info
        try {
            final ResourceManager rm;
            rm = InitialNaming.lookup(ResourceManager.NAME);
            info = findAcpiRSDTPTR(rm);
        } catch (NameNotFoundException ex) {
            throw new DriverException("Cannot find the resource manager");
        } catch (ResourceNotFreeException ex) {
            log.error("Cannot claim BIOS region", ex);
        }

        // Register out API
        dev.registerAPI(FirmwareAPI.class, this);

        // Start an ACPI device if we found the info for it.
        if (info != null) {
            log.info("Start ACPI device");
            acpiDevice = new AcpiDevice(dev.getBus(), "acpi", info);
            try {
                dev.getManager().register(acpiDevice);
            } catch (DeviceAlreadyRegisteredException ex) {
                log.error("Cannot register ACPI device", ex);
            }
        }
    }

    /**
     * Stop the device
     *
     * @throws DriverException
     */
    protected void stopDevice() throws DriverException {
        final Device dev = getDevice();
        if (acpiDevice != null) {
            dev.getManager().unregister(acpiDevice);
        }
        dev.unregisterAPI(FirmwareAPI.class);
    }

    /**
     * Find the ACPI root descriptor table.
     *
     * @param rm
     * @return
     * @throws ResourceNotFreeException
     */
    private static final AcpiRSDPInfo findAcpiRSDTPTR(final ResourceManager rm) throws ResourceNotFreeException {
        final byte[] match = {'R', 'S', 'D', ' ', 'P', 'T', 'R', ' '};

        final MemoryScanner scanner = (MemoryScanner) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return rm.getMemoryScanner();
            }
        });
        final Address tablePtr =
            scanner.findInt8Array(Address.fromIntZeroExtend(0xe0000), 0x1ffff, match, 0, match.length, 1);
        if (tablePtr != null) {
            final int version = getRSDTVersion(rm, tablePtr);
            return new AcpiRSDPInfo(tablePtr, version);
        } else {
            // Not an ACPI system
            return null;
        }
    }

    private static int getRSDTVersion(ResourceManager rm, Address start) {
        final MemoryResource res;
        try {
            res = rm.claimMemoryResource(ResourceOwner.SYSTEM, start, 20, ResourceManager.MEMMODE_NORMAL);
        } catch (ResourceNotFreeException e) {
            // Cannot claim memory
            return 1;
        }
        try {
            return res.getByte(15);
        } finally {
            res.release();
        }
    }
}
