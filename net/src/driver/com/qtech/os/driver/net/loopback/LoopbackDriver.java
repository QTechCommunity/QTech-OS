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

import com.qtech.os.driver.net.NetworkException;
import com.qtech.os.driver.net.spi.AbstractNetDriver;
import com.qtech.os.net.HardwareAddress;
import com.qtech.os.net.SocketBuffer;
import com.qtech.os.net.ethernet.EthernetAddress;
import com.qtech.os.net.ethernet.EthernetConstants;
import com.qtech.os.net.ethernet.EthernetHeader;
import com.qtech.os.net.ethernet.EthernetUtils;

/**
 * Driver for loopback device.
 *
 * @author epr
 * @author Martin Husted Hartvig
 */
public class LoopbackDriver extends AbstractNetDriver implements
    EthernetConstants {

    private static final EthernetAddress hwAddress = new EthernetAddress(
        "00-00-00-00-00-00");

    /**
     * Gets the hardware address of this device
     */
    public HardwareAddress getAddress() {
        return hwAddress;
    }

    /**
     * Gets the maximum transfer unit, the number of bytes this device can
     * transmit at a time.
     */
    public int getMTU() {
        return ETH_DATA_LEN;
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#doTransmit(SocketBuffer,
     *      HardwareAddress)
     */
    protected final void doTransmit(SocketBuffer skbuf,
                                    HardwareAddress destination) throws NetworkException {
        skbuf.insert(ETH_HLEN);
        if (destination != null) {
            destination.writeTo(skbuf, 0);
        } else {
            EthernetAddress.BROADCAST.writeTo(skbuf, 0);
        }
        getAddress().writeTo(skbuf, 6);
        skbuf.set16(12, skbuf.getProtocolID());

        onReceive(skbuf);
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#onReceive(com.qtech.os.net.SocketBuffer)
     */
    public void onReceive(SocketBuffer skbuf) throws NetworkException {

        // Extract ethernet header
        final EthernetHeader hdr = new EthernetHeader(skbuf);
        skbuf.setLinkLayerHeader(hdr);
        skbuf.setProtocolID(EthernetUtils.getProtocol(hdr));
        skbuf.pull(hdr.getLength());

        // Send to PM
        super.onReceive(skbuf);
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#getDevicePrefix()
     */
    protected String getDevicePrefix() {
        return LOOPBACK_DEVICE_PREFIX;
    }

    /**
     * @see com.qtech.os.driver.net.spi.AbstractNetDriver#renameToDevicePrefixOnly()
     */
    protected boolean renameToDevicePrefixOnly() {
        return true;
    }
}
