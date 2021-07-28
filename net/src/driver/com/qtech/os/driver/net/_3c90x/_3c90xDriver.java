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
 
package com.qtech.os.driver.net._3c90x;

import java.security.PrivilegedExceptionAction;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.driver.net.NetworkException;
import com.qtech.os.driver.net.ethernet.spi.AbstractEthernetDriver;
import com.qtech.os.net.HardwareAddress;
import com.qtech.os.net.SocketBuffer;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.system.resource.ResourceNotFreeException;
import com.qtech.os.util.AccessControllerUtils;
import com.qtech.os.util.TimeoutException;

/**
 * @author epr
 */
public class _3c90xDriver extends AbstractEthernetDriver {

    private static final long TRANSMIT_TIMEOUT = 5000;

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(_3c90xDriver.class);

    /**
     * The actual device driver
     */
    private _3c90xCore dd;

    /**
     * The device flags
     */
    private final _3c90xFlags flags;

    /**
     * Create a new instance
     */
    public _3c90xDriver(ConfigurationElement config) {
        this(new _3c90xFlags(config));
    }

    /**
     * Create a new instance
     *
     * @param flags
     */
    public _3c90xDriver(_3c90xFlags flags) {
        this.flags = flags;
    }

    /**
     * Gets the hardware address of this device
     */
    public HardwareAddress getAddress() {
        return dd.getHwAddress();
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#doTransmit(SocketBuffer,
     *      HardwareAddress)
     */
    protected void doTransmitEthernet(SocketBuffer skbuf, HardwareAddress destination)
        throws NetworkException {
        try {
            // Pad
            if (skbuf.getSize() < ETH_ZLEN) {
                skbuf.append(ETH_ZLEN - skbuf.getSize());
            }

            dd.transmit(skbuf, destination, TRANSMIT_TIMEOUT);
        } catch (InterruptedException ex) {
            throw new NetworkException("Interrupted", ex);
        } catch (TimeoutException ex) {
            throw new NetworkException("Timeout", ex);
        }
    }

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        try {
            dd = newCore(getDevice(), flags);
            dd.initialize();
            super.startDevice();
        } catch (ResourceNotFreeException ex) {
            throw new DriverException("Cannot claim " + flags.getName() + " resources", ex);
        }
    }

    /**
     * Create a new _3c90xCore instance
     */
    protected _3c90xCore newCore(final Device device, final _3c90xFlags flags)
        throws DriverException, ResourceNotFreeException {
        try {
            return AccessControllerUtils.doPrivileged(new PrivilegedExceptionAction<_3c90xCore>() {
                public _3c90xCore run() throws DriverException, ResourceNotFreeException {
                    return new _3c90xCore(_3c90xDriver.this, device, (PCIDevice) device, flags);
                }
            });
        } catch (DriverException ex) {
            throw ex;
        } catch (ResourceNotFreeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DriverException(ex);
        }
    }

    /**
     * @see com.qtech.os.driver.Driver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        log.debug("stopDevice");
        super.stopDevice();
        dd.disable();
        dd.release();
        dd = null;
        log.debug("done");
    }

    /**
     * Gets the device flags
     */
    public _3c90xFlags getFlags() {
        return flags;
    }
}
