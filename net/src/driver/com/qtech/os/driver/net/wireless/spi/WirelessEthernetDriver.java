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
 
package com.qtech.os.driver.net.wireless.spi;

import java.io.PrintWriter;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.net.NetworkException;
import com.qtech.os.driver.net.WirelessNetDeviceAPI;
import com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver;
import com.qtech.os.driver.net.ethernet.spi.Flags;
import com.qtech.os.driver.net.spi.AbstractDeviceCore;
import com.qtech.os.net.wireless.AuthenticationMode;
import com.qtech.os.system.resource.ResourceNotFreeException;

/**
 * Base class for wireless ethernet drivers.
 *
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public abstract class WirelessEthernetDriver extends BasicEthernetDriver implements WirelessNetDeviceAPI {

    /**
     * Device prefix for ethernet devices
     */
    public static final String WLAN_DEVICE_PREFIX = "wlan";

    /**
     * @see com.qtech.os.driver.net.WirelessNetDeviceAPI#getAuthenticationMode()
     */
    public AuthenticationMode getAuthenticationMode() {
        try {
            return getWirelessCore().getAuthenticationMode();
        } catch (DriverException ex) {
            log.debug("Cannot read AuthenticationMode, defaulting to OPENSYSTEM", ex);
            return AuthenticationMode.OPENSYSTEM;
        }
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#getDevicePrefix()
     */
    protected final String getDevicePrefix() {
        return WLAN_DEVICE_PREFIX;
    }

    /**
     * @see com.qtech.os.driver.net.WirelessNetDeviceAPI#getESSID()
     */
    public String getESSID() {
        try {
            return getWirelessCore().getESSID();
        } catch (DriverException ex) {
            log.debug("Cannot read ESSID", ex);
            return null;
        }
    }

    /**
     * Gets the wireless device core.
     *
     * @return the WirelessDeviceCore
     */
    protected final WirelessDeviceCore getWirelessCore() {
        return (WirelessDeviceCore) getDeviceCore();
    }

    /**
     * @see com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver#newCore(com.qtech.os.driver.Device,
     * com.qtech.os.driver.net.ethernet.spi.Flags)
     */
    protected final AbstractDeviceCore newCore(Device device, Flags flags)
        throws DriverException, ResourceNotFreeException {
        return newWirelessCore(device, flags);
    }

    /**
     * Create a new device code instance
     */
    protected abstract WirelessDeviceCore newWirelessCore(Device device, Flags flags)
        throws DriverException, ResourceNotFreeException;

    /**
     * @see com.qtech.os.driver.net.WirelessNetDeviceAPI#setAuthenticationMode(
     * com.qtech.os.net.wireless.AuthenticationMode)
     */
    public void setAuthenticationMode(AuthenticationMode mode) throws NetworkException {
        try {
            getWirelessCore().setAuthenticationMode(mode);
        } catch (DriverException ex) {
            log.debug("setAuthenticationMode failed", ex);
            throw new NetworkException(ex);
        }
    }

    /**
     * @see com.qtech.os.driver.net.WirelessNetDeviceAPI#setESSID(java.lang.String)
     */
    public void setESSID(String essid) throws NetworkException {
        try {
            getWirelessCore().setESSID(essid);
        } catch (DriverException ex) {
            log.debug("setESSID failed", ex);
            throw new NetworkException(ex);
        }
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#showInfo(java.io.PrintWriter)
     */
    public void showInfo(PrintWriter out) {
        super.showInfo(out);
        out.println("Current ESSID " + getESSID());
    }

    /**
     * @see com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver#startDevice()
     */
    protected void startDevice() throws DriverException {
        super.startDevice();
        getDevice().registerAPI(WirelessNetDeviceAPI.class, this);
    }

    /**
     * @see com.qtech.os.driver.net.ethernet.spi.BasicEthernetDriver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        getDevice().unregisterAPI(WirelessNetDeviceAPI.class);
        super.stopDevice();
    }
}
