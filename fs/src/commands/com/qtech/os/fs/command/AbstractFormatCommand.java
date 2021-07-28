/*
 * $Id$
 *
 * Copyright (C) 2003-2013 QTech Community
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
 
package com.qtech.os.fs.command;

import javax.naming.NameNotFoundException;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.block.FSBlockDeviceAPI;
import com.qtech.os.fs.FileSystem;
import com.qtech.os.fs.FileSystemException;
import com.qtech.os.fs.Formatter;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.DeviceArgument;

/**
 * @author Fabien DUMINY (fduminy at jnode.org)
 * @author crawley@jnode.org
 */
public abstract class AbstractFormatCommand<T extends FileSystem<?>> extends AbstractCommand {
    
    protected final DeviceArgument ARG_DEVICE = 
        new DeviceArgument("device", Argument.MANDATORY, "the device to format", FSBlockDeviceAPI.class);
    
    public AbstractFormatCommand(String description) {
        super(description);
        registerArguments(ARG_DEVICE);
    }

    protected abstract Formatter<T> getFormatter();

    public final void execute() 
        throws FileSystemException, NameNotFoundException, DeviceNotFoundException, DriverException {
        Device dev = ARG_DEVICE.getValue();
        Formatter<T> formatter = getFormatter();
        formatter.format(dev);

        // restart the device
        final DeviceManager dm = InitialNaming.lookup(DeviceManager.NAME);
        dm.stop(dev);
        dm.start(dev);
    }
}
