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
 
package com.qtech.os.driver.video.vesa;

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
import com.qtech.os.system.resource.ResourceNotFreeException;
import org.jnode.vm.x86.UnsafeX86;
import org.vmmagic.unboxed.Address;

/**
 * 
 * @author Fabien DUMINY (fduminy at jnode.org)
 * 
 */
public class VESADriver extends AbstractFrameBufferDriver {

    private FrameBufferConfiguration currentConfig;
    private VESACore kernel;

    private FrameBufferConfiguration[] configs;

    /**
     * Create a new instance
     */
    public VESADriver() {
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
        throws UnknownConfigurationException, AlreadyOpenException, DeviceException {
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
    final synchronized void close(VESACore graphics) {
        this.currentConfig = null;
    }

    /**
     * @see com.qtech.os.driver.Driver#startDevice()
     */
    protected void startDevice() throws DriverException {
        ModeInfoBlock modeInfoBlock = null;
        try {
            Address vbeControlInfo = UnsafeX86.getVbeControlInfos();
            VbeInfoBlock vbeInfoBlock = new VbeInfoBlock(vbeControlInfo);
            if (vbeInfoBlock.isEmpty()) {
                throw new DriverException(
                        "can't start device (vbeInfoBlock is empty): grub haven't switched to graphic mode");
            }

            Address vbeModeInfo = UnsafeX86.getVbeModeInfos();
            modeInfoBlock = new ModeInfoBlock(vbeModeInfo);
            if (modeInfoBlock.isEmpty()) {
                throw new DriverException(
                        "can't start device (modeInfoBlock is empty): grub haven't switched to graphic mode");
            }

            kernel = new VESACore(this, vbeInfoBlock, modeInfoBlock, (PCIDevice) getDevice());            
            configs = kernel.getConfigs();
        } catch (ResourceNotFreeException ex) {
            throw new DriverException(ex);
        } catch (Throwable t) {
            throw new DriverException(t);
        }
        final Device dev = getDevice();
        super.startDevice();

        dev.registerAPI(HardwareCursorAPI.class, kernel);
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
}
