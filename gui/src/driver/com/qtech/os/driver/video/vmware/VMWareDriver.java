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
 
package com.qtech.os.driver.video.vmware;

import java.io.PrintWriter;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceException;
import com.qtech.os.driver.DeviceInfoAPI;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.driver.video.AbstractFrameBufferDriver;
import com.qtech.os.driver.video.AlreadyOpenException;
import com.qtech.os.driver.video.FrameBufferConfiguration;
import com.qtech.os.driver.video.HardwareCursorAPI;
import com.qtech.os.driver.video.NotOpenException;
import com.qtech.os.driver.video.Surface;
import com.qtech.os.driver.video.UnknownConfigurationException;
import com.qtech.os.system.resource.ResourceNotFreeException;

/**
 * @author epr
 */
public class VMWareDriver extends AbstractFrameBufferDriver implements
    VMWareConstants, DeviceInfoAPI {

    private FrameBufferConfiguration currentConfig;
    private VMWareCore kernel;

    private FrameBufferConfiguration[] configs;

    /**
     * Create a new instance
     */
    public VMWareDriver() {
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getConfigurations()
     */
    public final FrameBufferConfiguration[] getConfigurations() {
        return configs;
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getCurrentConfiguration()
     */
    public final FrameBufferConfiguration getCurrentConfiguration() {
        return currentConfig;
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#open(com.qtech.os.driver.video.FrameBufferConfiguration)
     */
    public synchronized Surface open(FrameBufferConfiguration config)
        throws UnknownConfigurationException, AlreadyOpenException,
        DeviceException {
        for (int i = 0; i < configs.length; i++) {
            if (config.equals(configs[i])) {
                kernel.open(config);
                this.currentConfig = config;
                return kernel;
            }
        }
        throw new UnknownConfigurationException();
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getCurrentSurface()
     */
    public synchronized Surface getCurrentSurface() throws NotOpenException {
        if (currentConfig != null) {
            return kernel;
        } else {
            throw new NotOpenException();
        }
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#isOpen()
     */
    public final synchronized boolean isOpen() {
        return (currentConfig != null);
    }

    /**
     * Notify of a close of the graphics object
     *
     * @param graphics
     */
    final synchronized void close(VMWareCore graphics) {
        this.currentConfig = null;
    }

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        try {
            kernel = new VMWareCore(this, (PCIDevice) getDevice());
            configs = kernel.getConfigs();
        } catch (ResourceNotFreeException ex) {
            throw new DriverException(ex);
        }
        final Device dev = getDevice();
        super.startDevice();
        dev.registerAPI(HardwareCursorAPI.class, kernel);
        dev.registerAPI(DeviceInfoAPI.class, this);
    }

    /**
     * @see com.qtech.os.driver.Driver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        final Device dev = getDevice();
        dev.unregisterAPI(HardwareCursorAPI.class);
        if (currentConfig != null) {
            kernel.close();
        }
        if (kernel != null) {
            kernel.release();
            kernel = null;
        }
        super.stopDevice();
    }

    @Override
    public void showInfo(PrintWriter out) {
        if (kernel != null) {
            kernel.showInfo(out);
        }
    }
}
