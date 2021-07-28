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
 
package com.qtech.os.driver.block.usb.storage;

import org.apache.log4j.Logger;
import com.qtech.os.driver.Bus;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.scsi.CDB;
import com.qtech.os.driver.bus.scsi.SCSIDevice;
import com.qtech.os.driver.bus.scsi.SCSIException;
import com.qtech.os.driver.bus.scsi.SCSIHostControllerAPI;
import com.qtech.os.driver.bus.scsi.cdb.spc.CDBInquiry;
import com.qtech.os.driver.bus.scsi.cdb.spc.CDBTestUnitReady;
import com.qtech.os.driver.bus.scsi.cdb.spc.InquiryData;
import com.qtech.os.driver.bus.usb.USBConfiguration;
import com.qtech.os.driver.bus.usb.USBDataPipe;
import com.qtech.os.driver.bus.usb.USBDevice;
import com.qtech.os.driver.bus.usb.USBException;
import com.qtech.os.driver.bus.usb.USBPipeListener;
import com.qtech.os.driver.bus.usb.USBRequest;
import com.qtech.os.util.NumberUtils;
import com.qtech.os.util.TimeoutException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class USBStorageSCSIHostDriver extends Driver
    implements SCSIHostControllerAPI, USBPipeListener, USBStorageConstants {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(USBStorageSCSIHostDriver.class);

    /**
     * Storage specific device data
     */
    private USBStorageDeviceData storageDeviceData;

    /**
     * The SCSI device that i'm host of
     */
    private USBStorageSCSIDevice scsiDevice;

    /**
     * Initialize this instance.
     */
    public USBStorageSCSIHostDriver() {
    }

    @Override
    protected void startDevice() throws DriverException {
        try {
            USBDevice usbDevice = (USBDevice) getDevice();
            USBConfiguration conf = usbDevice.getConfiguration(0);
            usbDevice.setConfiguration(conf);
            // Set usb mass storage informations.
            this.storageDeviceData = new USBStorageDeviceData(usbDevice);
            USBDataPipe pipe;
            pipe = (USBDataPipe) this.storageDeviceData.getBulkOutEndPoint().getPipe();
            pipe.addListener(this);
            pipe.open();

            pipe = (USBDataPipe) this.storageDeviceData.getBulkInEndPoint().getPipe();
            pipe.addListener(this);
            pipe.open();

            usbDevice.registerAPI(SCSIHostControllerAPI.class, this);
            final Bus hostBus = new USBStorageSCSIHostBus(getDevice());
            scsiDevice = new USBStorageSCSIDevice(hostBus, "_sg");

            // Execute INQUIRY
            try {
                scsiDevice.inquiry();
            } catch (SCSIException ex) {
                throw new DriverException("Cannot INQUIRY device", ex);
            } catch (TimeoutException ex) {
                throw new DriverException("Cannot INQUIRY device : timeout", ex);
            } catch (InterruptedException ex) {
                throw new DriverException("Interrupted while INQUIRY device", ex);
            }
            // Register the generic SCSI device.
            try {
                final DeviceManager dm = usbDevice.getManager();
                dm.rename(scsiDevice, "sg", true);
                dm.register(scsiDevice);
                dm.rename(usbDevice, SCSIHostControllerAPI.DEVICE_PREFIX, true);
            } catch (DeviceAlreadyRegisteredException ex) {
                throw new DriverException(ex);
            }
        } catch (USBException e) {
            throw new DriverException(e);
        }

    }

    @Override
    protected void stopDevice() throws DriverException {
        final Device dev = getDevice();

        // Unregister the SCSI device that we host
        dev.getManager().unregister(scsiDevice);
        dev.unregisterAPI(SCSIHostControllerAPI.class);
    }

    public void requestCompleted(USBRequest request) {
        log.debug("USBStorageSCSIHostDriver completed with status:" + request.getStatus());
    }

    public void requestFailed(USBRequest request) {
        log.debug("USBStorageSCSIHostDriver failed with status:" + request.getStatus());
    }

    private final class USBStorageSCSIHostBus extends Bus {

        /**
         * @param parent
         */
        public USBStorageSCSIHostBus(Device parent) {
            super(parent);
        }
    }

    /**
     * @author Fabien Lesire
     */
    public final class USBStorageSCSIDevice extends SCSIDevice {

        private InquiryData inquiryResult;

        public USBStorageSCSIDevice(Bus bus, String id) {
            super(bus, id);
        }

        @Override
        public int executeCommand(CDB cdb, byte[] data, int dataOffset, long timeout)
            throws SCSIException, TimeoutException, InterruptedException {
            log.debug("*** execute command ***");
            ITransport t = storageDeviceData.getTransport();
            t.transport(cdb, timeout);
            return 0;
        }

        protected final void testUnit() throws SCSIException, TimeoutException, InterruptedException {
            log.info("*** Test unit ready ***");
            int res = this.executeCommand(new CDBTestUnitReady(), null, 0, 50000);
            log.debug("*** result : 0x" + NumberUtils.hex(res) + " ***");
        }

        /**
         * Execute an INQUIRY command.
         *
         * @throws SCSIException
         * @throws TimeoutException
         * @throws InterruptedException
         */
        protected final void inquiry() throws SCSIException, TimeoutException,
            InterruptedException {
            log.info("*** INQUIRY ***");
            final byte[] inqData = new byte[96];

            ITransport t = storageDeviceData.getTransport();
            t.transport(new CDBInquiry(inqData.length), 50000);

            inquiryResult = new InquiryData(inqData);
            log.debug("INQUIRY Data : " + inquiryResult.toString());
        }

        /*protected final void capacity() throws SCSIException, TimeoutException, InterruptedException {
            log.info("*** Read capacity ***");
            CapacityData cd = MMCUtils.readCapacity(this);
            log.debug("Capacity Data : " + cd.toString());

        }*/

        /**
         * @see com.qtech.os.driver.bus.scsi.SCSIDeviceAPI#getDescriptor()
         */
        public final InquiryData getDescriptor() {
            return inquiryResult;
        }

    }
}
