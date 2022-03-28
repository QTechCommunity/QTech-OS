/*
 * $Id$
 *
 * Copyright (C) 2020-2022 Ultreon Team
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
 
package org.jnode.fs.jifs.files;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Collection;
import javax.naming.NameNotFoundException;
import org.jnode.driver.Device;
import org.jnode.driver.DeviceManager;
import org.jnode.driver.DeviceNotFoundException;
import org.jnode.driver.Driver;
import org.jnode.driver.character.ChannelAlreadyOwnedException;
import org.jnode.driver.input.AbstractKeyboardDriver;
import org.jnode.driver.input.KeyboardAPI;
import org.jnode.driver.serial.SerialPortDriver;
import org.jnode.fs.FSDirectory;
import org.jnode.fs.jifs.JIFSFile;
import org.jnode.naming.InitialNaming;

/**
 * File, which contains information about devices.
 * 
 * @author Andreas H\u00e4nel
 */
public class JIFSFdevice extends JIFSFile {
    private String id;
    private ByteBuffer readBuffer = ByteBuffer.wrap(new byte[1]);
    private ByteBuffer writeBuffer = ByteBuffer.wrap(new byte[0]);

    public JIFSFdevice() {

    }

    public JIFSFdevice(String id, FSDirectory parent) {
        super(id);
        this.id = id;
        setParent(parent);
    }

    @Override
    public void refresh() {
        super.refresh();
        try {
            // Get device manager.
            final DeviceManager dm = InitialNaming.lookup(DeviceManager.NAME);

            // Get device and driver.
            Device device = dm.getDevice(this.id);
            Driver driver = device.getDriver();

            // Check driver instance.
            if (driver instanceof SerialPortDriver) {
                // Cast to serial port driver.
                SerialPortDriver serialPortDriver = (SerialPortDriver) driver;

                // Read.
                this.readBuffer.clear();
                serialPortDriver.read(this.readBuffer);

                // Write and flush.
                serialPortDriver.write(this.writeBuffer);
                serialPortDriver.flush();
                this.writeBuffer.clear();
            }
        } catch (javax.naming.NameNotFoundException E) {
            System.err.println("could not find DeviceManager");
        } catch (DeviceNotFoundException e) {
            System.err.println("Could not find device with id: " + this.id);
        }
    }

    public static boolean supportsDriver(Driver driver) {
        return (driver instanceof SerialPortDriver);
    }

    public void read(long fileOffset, ByteBuffer destBuf) {
        refresh();
        if (this.readBuffer == null) {
            return;
        }

        ByteBuffer buf = this.readBuffer;
        buf.position((int) fileOffset);
        buf = (ByteBuffer) buf.slice().limit(destBuf.remaining());
        destBuf.put(buf);
    }

    @Override
    public void write(long fileOffset, ByteBuffer src) throws IOException {
        this.writeBuffer = ByteBuffer.wrap(src.array(), (int) fileOffset, (int)(src.capacity() - fileOffset));
        refresh();
    }

    @Override
    public void flush() throws IOException {
        // Get device manager.
        final DeviceManager dm;
        try {
            dm = InitialNaming.lookup(DeviceManager.NAME);
        } catch (NameNotFoundException e) {
            throw new IOException("Could not get device manager.");
        }

        // Get device and driver.
        Device device;
        try {
            device = dm.getDevice(this.id);
        } catch (DeviceNotFoundException e) {
            throw new IOException("Couldn't find device with id: " + id, e);
        }
        Driver driver = device.getDriver();

        // Check driver instance.
        if (driver instanceof SerialPortDriver) {

        }
    }
}
