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
 
package com.qtech.os.driver.block.floppy.support;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.block.CHS;
import com.qtech.os.driver.block.Geometry;
import com.qtech.os.driver.block.floppy.DefaultFDC;
import com.qtech.os.driver.block.floppy.FDC;
import com.qtech.os.driver.block.floppy.FloppyControllerBus;
import com.qtech.os.driver.block.floppy.FloppyDevice;
import com.qtech.os.driver.block.floppy.FloppyDriveParameters;
import com.qtech.os.driver.block.floppy.FloppyDriveParametersCommand;
import com.qtech.os.driver.block.floppy.FloppyIdCommand;
import com.qtech.os.driver.block.floppy.FloppyParameters;
import com.qtech.os.driver.block.floppy.FloppyReadSectorCommand;
import com.qtech.os.driver.block.floppy.FloppySeekCommand;
import com.qtech.os.driver.block.floppy.FloppyWriteSectorCommand;
import com.qtech.os.system.resource.ResourceNotFreeException;

public class DefaultFloppyDeviceFactory implements FloppyDeviceFactory {
    public DefaultFloppyDeviceFactory() {
    }

    public FloppyDevice createDevice(FloppyControllerBus bus, int drive, FloppyDriveParameters dp) {
        return new FloppyDevice(bus, drive, dp);
    }

    public FDC createFDC(Device device) throws DriverException, ResourceNotFreeException {
        return new DefaultFDC(device, true);
    }

    public FloppyDriveParametersCommand createFloppyDriveParametersCommand(int drive, FloppyDriveParameters dp,
                                                                           FloppyParameters fp) {
        return new FloppyDriveParametersCommand(drive, dp, fp);
    }

    public FloppySeekCommand createFloppySeekCommand(int drive, int cylinder) {
        return new FloppySeekCommand(drive, cylinder);
    }

    public FloppyReadSectorCommand createFloppyReadSectorCommand(int drive, Geometry geometry, CHS chs,
                                                                 int currentSectorSize, boolean b, int gap1Size,
                                                                 byte[] dest, int destOffset) {
        return new FloppyReadSectorCommand(drive, geometry, chs, currentSectorSize, b, gap1Size, dest, destOffset);
    }

    public FloppyWriteSectorCommand createFloppyWriteSectorCommand(int drive, Geometry geometry, CHS chs,
                                                                   int currentSectorSize, boolean b, int gap1Size,
                                                                   byte[] src, int srcOffset) {
        return new FloppyWriteSectorCommand(drive, geometry, chs, currentSectorSize, b, gap1Size, src, srcOffset);
    }

    public FloppyIdCommand createFloppyIdCommand(int drive) {
        return new FloppyIdCommand(drive);
    }
}
