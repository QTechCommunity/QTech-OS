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
 
package com.qtech.os.driver.bus.ide.atapi;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Bus;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.ide.IDEBus;
import com.qtech.os.driver.bus.ide.IDEDevice;
import com.qtech.os.driver.bus.ide.command.IDEPacketCommand;
import com.qtech.os.driver.bus.scsi.CDB;
import com.qtech.os.driver.bus.scsi.SCSIDevice;
import com.qtech.os.driver.bus.scsi.SCSIException;
import com.qtech.os.driver.bus.scsi.SCSIHostControllerAPI;
import com.qtech.os.driver.bus.scsi.cdb.spc.CDBInquiry;
import com.qtech.os.driver.bus.scsi.cdb.spc.InquiryData;
import com.qtech.os.util.NumberUtils;
import com.qtech.os.util.TimeoutException;

/**
 * @author epr
 */
public class ATAPIDriver extends Driver implements SCSIHostControllerAPI {

    private ATAPIBus atapiBus;

    private ATAPISCSIDevice scsiDevice;

    private static final Logger log = Logger.getLogger(ATAPIDriver.class);

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        final Device ideDev = getDevice();

        // Register my api's
        ideDev.registerAPI(SCSIHostControllerAPI.class, this);

        // Create the ATAPI bus
        this.atapiBus = new ATAPIBus(ideDev);

        // Create the generic SCSI device, attached to the ATAPI bus
        scsiDevice = new ATAPISCSIDevice(atapiBus, "_sg");

        // Execute INQUIRY
        try {
            scsiDevice.inquiry();
        } catch (SCSIException ex) {
            throw new DriverException("Cannot INQUIRY device due to SCSIException", ex);
        } catch (TimeoutException ex) {
            throw new DriverException("Cannot INQUIRY device due to TimeoutException", ex);
        } catch (InterruptedException ex) {
            throw new DriverException("Interrupted while INQUIRY device", ex);
        }

        // Register the generic SCSI device.
        try {
            final DeviceManager dm = ideDev.getManager();
            synchronized (dm) {
                dm.rename(scsiDevice, "sg", true);
                dm.register(scsiDevice);
                dm.rename(ideDev, SCSIHostControllerAPI.DEVICE_PREFIX, true);
            }
        } catch (DeviceAlreadyRegisteredException ex) {
            throw new DriverException(ex);
        }
    }

    /**
     * @see com.qtech.os.driver.Driver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        final Device ideDev = getDevice();

        // Unregister the SCSI device
        try {
            ideDev.getManager().unregister(scsiDevice);
        } finally {
            scsiDevice = null;
            atapiBus = null;
        }

        // Unregister my api's
        ideDev.unregisterAPI(SCSIHostControllerAPI.class);
    }

    /**
     * ATAPI bus implementation.
     *
     * @author Ewout Prangsma (epr@users.sourceforge.net)
     */
    static class ATAPIBus extends Bus {

        public ATAPIBus(Device dev) {
            super(dev);
        }
    }

    class ATAPISCSIDevice extends SCSIDevice {

        private InquiryData inquiryResult;


        /**
         * Initialize this instance.
         *
         * @param bus
         * @param id
         */
        public ATAPISCSIDevice(ATAPIBus bus, String id) {
            super(bus, id);
        }

        /**
         * (non-Javadoc)
         *
         * @see com.qtech.os.driver.bus.scsi.SCSIDevice#executeCommand(com.qtech.os.driver.bus.scsi.CDB, byte[], int, long)
         */
        public final int executeCommand(CDB cdb, byte[] data,
                                        int dataOffset, long timeout) throws SCSIException,
            TimeoutException, InterruptedException {
            final IDEDevice dev = (IDEDevice) getDevice();
            final IDEBus bus = (IDEBus) dev.getBus();

            final IDEPacketCommand cmd = new IDEPacketCommand(
                dev.isPrimary(), dev.isMaster(), cdb.toByteArray(), data,
                dataOffset);
            bus.executeAndWait(cmd, timeout);

            if (!cmd.isFinished()) {
                throw new TimeoutException("Timeout in SCSI command");
            } else if (cmd.hasError()) {
                throw new SCSIException("Command error 0x" + NumberUtils.hex(cmd.getError(), 2));
            } else {
                return cmd.getDataTransfered();
            }
        }

        /**
         * Execute an INQUUIRY command.
         *
         * @throws SCSIException
         * @throws TimeoutException
         * @throws InterruptedException
         */
        protected final void inquiry() throws SCSIException, TimeoutException,
            InterruptedException {
            final byte[] inqData = new byte[96];
            scsiDevice.executeCommand(new CDBInquiry(inqData.length), inqData,
                0, 50000);
            inquiryResult = new InquiryData(inqData);
            log.debug("INQUIRY Data : " + inquiryResult.toString());
        }

        /**
         * (non-Javadoc)
         *
         * @see com.qtech.os.driver.bus.scsi.SCSIDevice#getDescriptor()
         */
        public final InquiryData getDescriptor() {
            return inquiryResult;
        }
    }
}
