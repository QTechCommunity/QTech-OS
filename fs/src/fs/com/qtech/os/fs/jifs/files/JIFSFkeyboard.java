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
 
package com.qtech.os.fs.jifs.files;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.qtech.os.driver.ApiNotFoundException;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.input.KeyboardAPIAdapter;
import com.qtech.os.driver.serial.SerialPortDriver;
import com.qtech.os.fs.FSDirectory;
import com.qtech.os.fs.jifs.JIFSFile;
import com.qtech.os.fs.jifs.ReadOnlyDeviceException;
import com.qtech.os.naming.InitialNaming;

/**
 * File, which contains information about devices.
 * 
 * @author Andreas H\u00e4nel
 */
public class JIFSFkeyboard extends JIFSFile {
    private String id;
    private ByteBuffer readBuffer = ByteBuffer.wrap(new byte[1]);
    private ByteBuffer writeBuffer = ByteBuffer.wrap(new byte[0]);
    private KeyboardAPIAdapter driver;

    public JIFSFkeyboard() {

    }

    public JIFSFkeyboard(String id, FSDirectory parent) {
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
            this.driver = device.getAPI(KeyboardAPIAdapter.class);
        } catch (javax.naming.NameNotFoundException E) {
            System.err.println("could not find DeviceManager");
        } catch (DeviceNotFoundException e) {
            System.err.println("Could not find device with id: " + this.id);
        } catch (ApiNotFoundException e) {
            System.err.println();
        }
    }

    public void read(long fileOffset, ByteBuffer destBuf) {
        refresh();
        if (this.readBuffer == null) {
            return;
        }

        boolean keyDown = driver.isKeyDown((int) fileOffset);
        destBuf.put(keyDown ? (byte) 1 : (byte) 0);
    }
}
