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
 
package com.qtech.os.driver.video.cirrus;

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
import com.qtech.os.driver.video.ddc.DisplayDataChannelAPI;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.system.resource.ResourceNotFreeException;

/**
 * @author peda
 */
public class CirrusDriver extends AbstractFrameBufferDriver implements CirrusConstants {

    private final String architecture;
    
    private final String model;

    private FrameBufferConfiguration currentConfig;
    
    private CirrusCore driver;
    
    /**
     * Create a new instance
     */
    public CirrusDriver(ConfigurationElement config) throws DriverException {
        this(config.getAttribute("architecture"), config.getAttribute("name"));
    }

    /**
     * Create a new instance
     */
    public CirrusDriver(String architecture, String model) {
        this.architecture = architecture;
        this.model = model;
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getConfigurations()
     */
    public final FrameBufferConfiguration[] getConfigurations() {
        return driver.getConfigurations();
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
        throws UnknownConfigurationException, AlreadyOpenException, DeviceException {

        // TODO: do check if mode is possible
        driver.open(config);

        return driver;
    }

    /**
     * @see com.qtech.os.driver.video.FrameBufferAPI#getCurrentSurface()
     */
    public synchronized Surface getCurrentSurface() throws NotOpenException {
        if (currentConfig != null) {
            return driver;
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
     * close the current config
     */
    public final void close() {
        this.currentConfig = null;
    }

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        try {
            driver = new CirrusCore(this, architecture, model, (PCIDevice) getDevice());
        } catch (ResourceNotFreeException ex) {
            throw new DriverException(ex);
        }
        super.startDevice();
        @SuppressWarnings("unused")
        final Device dev = getDevice();
        //dev.registerAPI(DisplayDataChannelAPI.class, driver); 
        //       <-- should we register this one? We do read edid our own..
        //dev.registerAPI(HardwareCursorAPI.class, driver.getHardwareCursor());
    }

    /**
     * @see com.qtech.os.driver.Driver#stopDevice()
     */
    protected void stopDevice() throws DriverException {
        if (currentConfig != null) {
            driver.close();
        }
        if (driver != null) {
            driver.release();
            driver = null;
        }
        final Device dev = getDevice();
        dev.unregisterAPI(DisplayDataChannelAPI.class);
        dev.unregisterAPI(HardwareCursorAPI.class);
        super.stopDevice();
    }
}
