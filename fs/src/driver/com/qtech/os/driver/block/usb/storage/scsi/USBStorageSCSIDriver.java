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
 
package com.qtech.os.driver.block.usb.storage.scsi;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.RemovableDeviceAPI;
import com.qtech.os.driver.block.FSBlockAlignmentSupport;
import com.qtech.os.driver.block.FSBlockDeviceAPI;
import com.qtech.os.driver.block.usb.storage.USBStorageConstants;
import com.qtech.os.driver.block.usb.storage.USBStorageSCSIHostDriver.USBStorageSCSIDevice;
import com.qtech.os.driver.bus.scsi.SCSIDevice;
import com.qtech.os.driver.bus.scsi.SCSIDeviceAPI;
import com.qtech.os.driver.bus.scsi.SCSIException;
import com.qtech.os.driver.bus.scsi.SCSIHostControllerAPI;
import com.qtech.os.driver.bus.scsi.cdb.mmc.CapacityData;
import com.qtech.os.driver.bus.scsi.cdb.mmc.MMCUtils;
import com.qtech.os.driver.bus.usb.USBPipeListener;
import com.qtech.os.driver.bus.usb.USBRequest;
import com.qtech.os.partitions.PartitionTableEntry;
import com.qtech.os.util.TimeoutException;

public class USBStorageSCSIDriver extends Driver
    implements FSBlockDeviceAPI, RemovableDeviceAPI, SCSIHostControllerAPI, USBPipeListener, USBStorageConstants {

    /** */
    private final FSBlockAlignmentSupport blockAlignment;

    /** */
    private boolean locked;

    /** */
    private CapacityData capacity;

    /** */
    private boolean changed;

    /** */
    // private final ResourceManager rm;
    public USBStorageSCSIDriver() {
        this.blockAlignment = new FSBlockAlignmentSupport(this, 2048);
    }

    @Override
    protected void startDevice() throws DriverException {
        final Device dev = getDevice();
        // Rename the device
        try {
            final DeviceManager dm = dev.getManager();
            synchronized (dm) {
                dm.rename(dev, "sg", true);
            }
        } catch (DeviceAlreadyRegisteredException ex) {
            throw new DriverException(ex);
        }

        this.locked = false;
        this.changed = true;
        this.capacity = null;
        this.blockAlignment.setAlignment(2048);

        dev.registerAPI(RemovableDeviceAPI.class, this);
        dev.registerAPI(FSBlockDeviceAPI.class, blockAlignment);
    }

    @Override
    protected void stopDevice() throws DriverException {
        try {
            unlock();
        } catch (IOException ex) {
            throw new DriverException(ex);
        } finally {
            final SCSIDevice dev = (SCSIDevice) getDevice();
            dev.unregisterAPI(RemovableDeviceAPI.class);
            dev.unregisterAPI(FSBlockDeviceAPI.class);
            dev.unregisterAPI(SCSIDeviceAPI.class);
        }

    }

    public int getSectorSize() throws IOException {
        processChanged();
        return capacity.getBlockLength();
    }

    public PartitionTableEntry getPartitionTableEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getLength() throws IOException {
        processChanged();
        return capacity.getBlockLength() & capacity.getLogicalBlockAddress();
    }

    public void read(long devOffset, ByteBuffer dest) throws IOException {
        processChanged();
        if (capacity == null) {
            throw new IOException("No medium");
        }

    }

    public void write(long devOffset, ByteBuffer src) throws IOException {
        // TODO Auto-generated method stub

    }

    public void flush() throws IOException {
        // TODO Auto-generated method stub

    }

    public void requestCompleted(USBRequest request) {
        // TODO Auto-generated method stub

    }

    public void requestFailed(USBRequest request) {
        // TODO Auto-generated method stub

    }

    /**
     * Unlock the device.
     *
     * @throws IOException
     */
    public synchronized void unlock() throws IOException {
        if (!locked) {
            final SCSIDevice dev = (SCSIDevice) getDevice();
            try {
                MMCUtils.setMediaRemoval(dev, false, false);
            } catch (SCSIException ex) {
                final IOException ioe = new IOException();
                ioe.initCause(ex);
                throw ioe;
            } catch (TimeoutException ex) {
                final IOException ioe = new IOException();
                ioe.initCause(ex);
                throw ioe;
            } catch (InterruptedException ex) {
                throw new InterruptedIOException();
            }
            locked = false;
        }
    }

    private void processChanged() throws IOException {
        if (changed) {
            this.capacity = null;
            final USBStorageSCSIDevice dev = (USBStorageSCSIDevice) getDevice();
            try {
                // Gets the capacity.
                this.capacity = MMCUtils.readCapacity(dev);
                this.blockAlignment.setAlignment(capacity.getBlockLength());
            } catch (SCSIException ex) {
                final IOException ioe = new IOException();
                ioe.initCause(ex);
                throw ioe;
            } catch (TimeoutException ex) {
                final IOException ioe = new IOException();
                ioe.initCause(ex);
                throw ioe;
            } catch (InterruptedException ex) {
                throw new InterruptedIOException();
            }
            changed = false;
        }
    }

    public boolean canLock() {
        return true;
    }

    /**
     * It's a removable device.
     */
    public boolean canEject() {
        return true;
    }

    public void lock() throws IOException {
        // TODO Auto-generated method stub

    }

    public boolean isLocked() {
        return locked;
    }

    public void eject() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void load() throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
