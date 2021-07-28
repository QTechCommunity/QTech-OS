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
 
package com.qtech.os.driver.video.ati.mach64;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceException;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.pci.PCIDevice;
import com.qtech.os.driver.video.AbstractFrameBufferDriver;
import com.qtech.os.driver.video.AlreadyOpenException;
import com.qtech.os.driver.video.FrameBufferConfiguration;
import com.qtech.os.driver.video.HardwareCursorAPI;
import com.qtech.os.driver.video.NotOpenException;
import com.qtech.os.driver.video.Surface;
import com.qtech.os.driver.video.UnknownConfigurationException;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.system.resource.ResourceNotFreeException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class Mach64Driver extends AbstractFrameBufferDriver implements Mach64Constants {

    private FrameBufferConfiguration currentConfig;
    private Mach64Core kernel;
    private Mach64Surface surface;
    private final String model;

    private static final FrameBufferConfiguration[] CONFIGS =
            new FrameBufferConfiguration[] {Mach64Configuration.VESA_118,
                Mach64Configuration.VESA_115};

    /**
     * Create a new instance
     */
    public Mach64Driver(ConfigurationElement config) throws DriverException {
        this.model = config.getAttribute("name");
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getConfigurations()
     */
    public FrameBufferConfiguration[] getConfigurations() {
        return CONFIGS;
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getCurrentConfiguration()
     */
    public FrameBufferConfiguration getCurrentConfiguration() {
        return currentConfig;
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#open(com.qtech.os.driver.video.FrameBufferConfiguration)
     */
    public Surface open(FrameBufferConfiguration config)
        throws UnknownConfigurationException, AlreadyOpenException, DeviceException {
        for (int i = 0; i < CONFIGS.length; i++) {
            if (config.equals(CONFIGS[i])) {
                try {
                    this.surface = kernel.open((Mach64Configuration) config);
                    this.currentConfig = config;
                    return surface;
                } catch (ResourceNotFreeException ex) {
                    throw new DeviceException(ex);
                }
            }
        }
        throw new UnknownConfigurationException();
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getCurrentSurface()
     */
    public synchronized Surface getCurrentSurface() throws NotOpenException {
        if (currentConfig != null) {
            return surface;
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
    final synchronized void close(Mach64Core graphics) {
        this.currentConfig = null;
        this.surface = null;
    }

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        try {
            kernel = new Mach64Core(this, model, (PCIDevice) getDevice());
        } catch (ResourceNotFreeException ex) {
            throw new DriverException(ex);
        }
        super.startDevice();
        // final Device dev = getDevice();
        // dev.registerAPI(DisplayDataChannelAPI.class, kernel);
        // dev.registerAPI(HardwareCursorAPI.class, kernel.getHardwareCursor());
    }

    /**
     * @see com.qtech.os.driver.Driver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        if (currentConfig != null) {
            kernel.close();
        }
        if (kernel != null) {
            kernel.release();
            kernel = null;
        }
        final Device dev = getDevice();
        // dev.unregisterAPI(DisplayDataChannelAPI.class);
        dev.unregisterAPI(HardwareCursorAPI.class);
        super.stopDevice();
    }

}
