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
 
package com.qtech.os.driver.net.spi;

import java.io.PrintWriter;
import java.util.HashMap;
import javax.naming.NameNotFoundException;
import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceInfoAPI;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.net.NetDeviceAPI;
import com.qtech.os.driver.net.NetDeviceEvent;
import com.qtech.os.driver.net.NetDeviceListener;
import com.qtech.os.driver.net.NetworkException;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.net.HardwareAddress;
import com.qtech.os.net.LayerHeader;
import com.qtech.os.net.ProtocolAddressInfo;
import com.qtech.os.net.SocketBuffer;
import com.qtech.os.net.util.NetUtils;
import com.qtech.os.util.NumberUtils;
import com.qtech.os.util.Queue;
import com.qtech.os.util.QueueProcessor;
import com.qtech.os.util.QueueProcessorThread;

/**
 * @author epr
 */
public abstract class AbstractNetDriver extends Driver
    implements NetDeviceAPI, DeviceInfoAPI, QueueProcessor<Object[]> {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(AbstractNetDriver.class);
    /**
     * Device prefix for ethernet devices
     */
    public static final String ETH_DEVICE_PREFIX = "eth";
    /**
     * Device prefix for loopback devices
     */
    public static final String LOOPBACK_DEVICE_PREFIX = "lo";

    /**
     * Number of received bytes
     */
    private long rx_count;
    /**
     * Number of transmitted bytes
     */
    private long tx_count;
    /**
     * Mapping between protocol id and protocol address
     */
    private final HashMap<Integer, ProtocolAddressInfo> protocolAddresses = new HashMap<Integer, ProtocolAddressInfo>();
    /**
     * Queue used to store frames ready for transmission
     */
    private final Queue<Object[]> txQueue = new Queue<Object[]>();
    /**
     * Thread used to transmit frames
     */
    private QueueProcessorThread<Object[]> txThread;
    /**
     * Event processor
     */
    private NetDeviceEventProcessor eventProcessor;

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        final Device device = getDevice();
        try {
            final DeviceManager dm = InitialNaming.lookup(DeviceManager.NAME);
            if (renameToDevicePrefixOnly()) {
                dm.rename(device, getDevicePrefix(), true);
            } else {
                final String prefix = getDevicePrefix() + '-';
                if (!device.getId().startsWith(prefix)) {
                    dm.rename(device, getDevicePrefix() + '-' + device.getId(), false);
                }
            }
        } catch (DeviceAlreadyRegisteredException ex) {
            log.error("Cannot rename device", ex);
        } catch (NameNotFoundException ex) {
            throw new DriverException("Cannot find DeviceManager", ex);
        }
        device.registerAPI(DeviceInfoAPI.class, this);
        device.registerAPI(NetDeviceAPI.class, this);
        txThread = new QueueProcessorThread<Object[]>(device.getId() + "-tx", txQueue, this);
        txThread.start();
    }

    /**
     * @see com.qtech.os.driver.Driver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        getDevice().unregisterAPI(NetDeviceAPI.class);
        getDevice().unregisterAPI(DeviceInfoAPI.class);
        txThread.stopProcessor();
        txThread = null;
    }

    /**
     * @see com.qtech.os.driver.net.NetDeviceAPI#transmit(SocketBuffer, HardwareAddress)
     */
    public final void transmit(SocketBuffer skbuf, HardwareAddress destination)
        throws NetworkException {
        // Update all layer headers
        int offset = 0;
        offset = updateLayerHeader(skbuf, skbuf.getLinkLayerHeader(), offset);
        offset = updateLayerHeader(skbuf, skbuf.getNetworkLayerHeader(), offset);
        offset = updateLayerHeader(skbuf, skbuf.getTransportLayerHeader(), offset);

        //log.debug("Adding to transmit queue");
        txQueue.add(new Object[]{skbuf, destination});
    }

    private final int updateLayerHeader(SocketBuffer skbuf, LayerHeader hdr, int offset) {
        if (hdr != null) {
            hdr.finalizeHeader(skbuf, offset);
            return offset + hdr.getLength();
        } else {
            return offset;
        }
    }

    /**
     * @see com.qtech.os.driver.net.NetDeviceAPI#addEventListener(com.qtech.os.driver.net.NetDeviceListener)
     */
    public void addEventListener(NetDeviceListener listener) {
        NetDeviceEventProcessor proc = this.eventProcessor;
        if (proc == null) {
            this.eventProcessor = proc = new NetDeviceEventProcessor();
        }
        proc.addEventListener(listener);
    }

    /**
     * @see com.qtech.os.driver.net.NetDeviceAPI#removeEventListener(com.qtech.os.driver.net.NetDeviceListener)
     */
    public void removeEventListener(NetDeviceListener listener) {
        final NetDeviceEventProcessor proc = this.eventProcessor;
        if (proc != null) {
            proc.removeEventListener(listener);
        }
    }

    /**
     * Post an event that will be fired (on another thread) to the listeners.
     *
     * @param event
     */
    public void postEvent(NetDeviceEvent event) {
        final NetDeviceEventProcessor proc = this.eventProcessor;
        if (proc != null) {
            proc.postEvent(event);
        }
    }

    /**
     * @see com.qtech.os.util.QueueProcessor#process(java.lang.Object)
     */
    public void process(Object[] object) {
        try {
            //log.debug("<transmit dev=" + getDevice().getId() + ">");
            final Object[] data = (Object[]) object;
            final SocketBuffer skbuf = (SocketBuffer) data[0];
            final HardwareAddress destination = (HardwareAddress) data[1];
            tx_count += skbuf.getSize();
            doTransmit(skbuf, destination);
            //log.debug("</transmit dev=" + getDevice().getId() + ">");
        } catch (NetworkException ex) {
            log.error("Cannot transmit packet", ex);
        }
    }

    /**
     * @see com.qtech.os.driver.net.NetDeviceAPI#transmit(SocketBuffer, HardwareAddress)
     */
    protected abstract void doTransmit(SocketBuffer skbuf, HardwareAddress destination)
        throws NetworkException;

    /**
     * Gets the prefix for the device name
     *
     * @see #ETH_DEVICE_PREFIX
     */
    protected abstract String getDevicePrefix();

    /**
     * If this method returns true, the rename of the device id will be set
     * to a devicePrefix with an auto-number, if false, the device id will be renamed
     * to devicePrefix "-" old-deviceid.
     *
     * @see #getDevicePrefix()
     */
    protected boolean renameToDevicePrefixOnly() {
        return false;
    }

    /**
     * Pass a received frame onto the PacketTypeManager.
     *
     * @param skbuf
     */
    protected void onReceive(SocketBuffer skbuf)
        throws NetworkException {
        skbuf.setDevice(getDevice());
        rx_count += skbuf.getSize();
        NetUtils.sendToPTM(skbuf);
    }

    /**
     * Gets the protocol address information for a given protocol.
     *
     * @param protocolID
     * @return The protocol address information, or null if not found.
     */
    public ProtocolAddressInfo getProtocolAddressInfo(int protocolID) {
        return protocolAddresses.get(protocolID);
    }

    /**
     * Sets the protocol address information for a given protocol.
     *
     * @param protocolID
     */
    public void setProtocolAddressInfo(int protocolID, ProtocolAddressInfo addressInfo) {
        protocolAddresses.put(protocolID, addressInfo);
    }

    /**
     * @see com.qtech.os.driver.DeviceInfoAPI#showInfo(java.io.PrintWriter)
     */
    public void showInfo(PrintWriter out) {
        if (!protocolAddresses.isEmpty()) {
            out.println("Protocol addresses:");
            for (int protId : protocolAddresses.keySet()) {
                out.println("    0x" + NumberUtils.hex(protId, 4) + ' '
                        + getProtocolAddressInfo(protId));
            }
        }
    }    
}
