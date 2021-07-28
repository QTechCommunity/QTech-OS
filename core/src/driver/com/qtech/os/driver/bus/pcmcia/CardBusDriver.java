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
 
package com.qtech.os.driver.bus.pcmcia;

import javax.naming.NameNotFoundException;
import org.apache.log4j.Logger;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.driver.bus.pci.PCIHeaderType2;
import com.qtech.os.naming.InitialNaming;

/**
 * Driver for a CardBus controller (PCI-to-CardBus bridge).
 * It is based on the TI PCI4451, ymmv with other controllers.
 *
 * @author markhale
 */
public class CardBusDriver extends Driver {
    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(CardBusDriver.class);
    private CardBusBus bus;

    public CardBusDriver() {
    }

    protected void startDevice() throws DriverException {
        final Device device = getDevice();
        final DeviceManager dm;
        try {
            dm = InitialNaming.lookup(DeviceManager.NAME);
            dm.rename(device, getDevicePrefix(), true);
        } catch (DeviceAlreadyRegisteredException ex) {
            log.error("Cannot rename device", ex);
            throw new DriverException("Cannot rename device", ex);
        } catch (NameNotFoundException ex) {
            throw new DriverException("Cannot find DeviceManager", ex);
        }
        PCIHeaderType2 header = ((PCIDevice) device).getConfig().asHeaderType2();
        this.bus = new CardBusBus(this, header.getCardBus());
    }

    protected void stopDevice() throws DriverException {
        /** @todo Implement this com.qtech.os.driver.Driver abstract method */
    }

    protected String getDevicePrefix() {
        return "cardbus";
    }
}
