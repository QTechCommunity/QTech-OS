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
import com.qtech.os.driver.bus.scsi.CDB;
import com.qtech.os.driver.bus.usb.SetupPacket;
import com.qtech.os.driver.bus.usb.USBControlPipe;
import com.qtech.os.driver.bus.usb.USBDataPipe;
import com.qtech.os.driver.bus.usb.USBDevice;
import com.qtech.os.driver.bus.usb.USBException;
import com.qtech.os.driver.bus.usb.USBPacket;
import com.qtech.os.driver.bus.usb.USBRequest;
import com.qtech.os.util.NumberUtils;

final class USBStorageBulkTransport implements ITransport, USBStorageConstants {

    /**
     * My logger
     */
    private static final Logger log = Logger.getLogger(USBStorageBulkTransport.class);
    /** */
    private final USBStorageDeviceData storageDeviceData;

    /**
     * @param storageDeviceData
     */
    public USBStorageBulkTransport(USBStorageDeviceData storageDeviceData) {
        this.storageDeviceData = storageDeviceData;
    }

    /*
     * (non-Javadoc)
     * @see com.qtech.os.driver.block.usb.storage.ITransport#transport(com.qtech.os.driver.bus.scsi.CDB, int)
     */
    public void transport(CDB cdb, long timeout) {
        try {
            byte[] scsiCmd = cdb.toByteArray();
            // Setup command wrapper 
            CBW cbw = new CBW();
            cbw.setSignature(US_BULK_CB_SIGN);
            cbw.setTag(1);
            cbw.setDataTransferLength((byte) cdb.getDataTransfertCount());
            cbw.setFlags((byte) 0);
            cbw.setLun((byte) 0);
            cbw.setLength((byte) scsiCmd.length);
            cbw.setCdb(scsiCmd);
            log.debug(cbw.toString());
            // Sent CBW to device
            USBDataPipe outPipe = ((USBDataPipe) storageDeviceData.getBulkOutEndPoint().getPipe());
            USBRequest req = outPipe.createRequest(cbw);
            if (timeout <= 0) {
                outPipe.asyncSubmit(req);
            } else {
                outPipe.syncSubmit(req, timeout);
            }
            //
            CSW csw = new CSW();
            csw.setSignature(US_BULK_CS_SIGN);
            USBDataPipe inPipe = ((USBDataPipe) storageDeviceData.getBulkInEndPoint().getPipe());
            USBRequest resp = inPipe.createRequest(csw);
            if (timeout <= 0) {
                inPipe.asyncSubmit(resp);
            } else {
                inPipe.syncSubmit(resp, timeout);
            }
        } catch (USBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bulk-Only mass storage reset.
     */
    public void reset() throws USBException {
        final USBControlPipe pipe = storageDeviceData.getDevice().getDefaultControlPipe();
        final USBRequest req = pipe.createRequest(new SetupPacket(USB_DIR_OUT
            | USB_TYPE_CLASS | USB_RECIP_INTERFACE, 0xFF, 0, 0, 0), null);
        pipe.syncSubmit(req, GET_TIMEOUT);
    }

    /**
     * Get max logical unit allowed by device. Device not support multiple LUN <i>may</i> stall.
     *
     * @param usbDev
     * @throws USBException
     */
    public void getMaxLun(USBDevice usbDev) throws USBException {
        log.info("*** Get max lun ***");
        final USBControlPipe pipe = usbDev.getDefaultControlPipe();
        final USBPacket packet = new USBPacket(1);
        final USBRequest req = pipe.createRequest(new SetupPacket(USB_DIR_IN
            | USB_TYPE_CLASS | USB_RECIP_INTERFACE, 0xFE, 0, 0, 1), packet);
        pipe.syncSubmit(req, GET_TIMEOUT);
        log.debug("*** Request data     : " + req.toString());
        log.debug("*** Request status   : 0x" + NumberUtils.hex(req.getStatus(), 4));
        if (req.getStatus() == USBREQ_ST_COMPLETED) {
            storageDeviceData.setMaxLun(packet.getData()[0]);
        } else if (req.getStatus() == USBREQ_ST_STALLED) {
            storageDeviceData.setMaxLun((byte) 0);
        } else {
            throw new USBException("Request status   : 0x" + NumberUtils.hex(req.getStatus(), 4));
        }
    }
}
