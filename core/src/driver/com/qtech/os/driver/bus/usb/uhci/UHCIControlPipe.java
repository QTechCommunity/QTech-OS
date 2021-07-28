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
 
package com.qtech.os.driver.bus.usb.uhci;

import com.qtech.os.driver.bus.usb.SetupPacket;
import com.qtech.os.driver.bus.usb.USBControlPipe;
import com.qtech.os.driver.bus.usb.USBDevice;
import com.qtech.os.driver.bus.usb.USBEndPoint;
import com.qtech.os.driver.bus.usb.USBPacket;
import com.qtech.os.driver.bus.usb.USBRequest;
import com.qtech.os.system.resource.ResourceManager;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class UHCIControlPipe extends UHCIPipe implements USBControlPipe {

    /**
     * @param pm
     * @param rm
     * @param device
     * @param ep
     * @param skelQH
     */
    public UHCIControlPipe(UHCIPipeManager pm, ResourceManager rm, USBDevice device, USBEndPoint ep, QueueHead skelQH) {
        super(pm, rm, device, ep, USB_ENDPOINT_XFER_CONTROL, skelQH);
    }

    /**
     * @see com.qtech.os.driver.bus.usb.USBControlPipe#createRequest(com.qtech.os.driver.bus.usb.SetupPacket,
     * com.qtech.os.driver.bus.usb.USBPacket)
     */
    public USBRequest createRequest(SetupPacket setupP, USBPacket dataP) {
        return new UHCIControlRequest(setupP, dataP);
    }

}
