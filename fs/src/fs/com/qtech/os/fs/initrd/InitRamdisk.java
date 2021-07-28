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
 
package com.qtech.os.fs.initrd;

import java.io.IOException;

import javax.naming.NameNotFoundException;

import org.apache.log4j.Logger;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.DeviceUtils;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.block.ramdisk.RamDiskDevice;
import com.qtech.os.driver.block.ramdisk.RamDiskDriver;
import com.qtech.os.fs.FileSystem;
import com.qtech.os.fs.FileSystemException;
import com.qtech.os.fs.fat.FatFileSystemFormatter;
import com.qtech.os.fs.fat.FatType;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;
import com.qtech.os.util.NumberUtils;

/**
 * Dummy plugin that just mount an initial ramdisk on /Jnode
 *
 * @author gbin
 */
public class InitRamdisk extends Plugin {

    private static final Logger log = Logger.getLogger(InitRamdisk.class);

    /**
     * Create a new instance
     */
    public InitRamdisk(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        try {
            log.info("Create initrd ramdisk on /jnode");
            final DeviceManager dm = DeviceUtils.getDeviceManager();
            int size = getPreferences().getInt("size", (int) NumberUtils.getSize("100K"));
            final RamDiskDevice dev = new RamDiskDevice(null, "dummy", size);
            dev.setDriver(new RamDiskDriver("jnode"));
            dm.register(dev);

            log.info("Format initrd ramdisk");

            final FatFileSystemFormatter formatter = new FatFileSystemFormatter(FatType.FAT16);
            final FileSystem<?> fs = formatter.format(dev);
            try {
                fs.getRootEntry().getDirectory().addDirectory("tmp");
            } catch (IOException ex) {
                log.error("Cannot create tmp on ramdisk");
            }

            // restart the device
            log.info("Restart initrd ramdisk");
            dm.stop(dev);
            dm.start(dev);

            log.info("/jnode ready.");
        } catch (NameNotFoundException e) {
            throw new PluginException(e);
        } catch (DriverException e) {
            throw new PluginException(e);
        } catch (DeviceAlreadyRegisteredException e) {
            throw new PluginException(e);
        } catch (FileSystemException e) {
            throw new PluginException(e);
        } catch (DeviceNotFoundException ex) {
            throw new PluginException(ex);
        }
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() {
        // do nothing for the moment
    }
}
