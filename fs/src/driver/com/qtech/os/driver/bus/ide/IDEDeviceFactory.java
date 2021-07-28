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
 
package com.qtech.os.driver.bus.ide;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DriverException;
import com.qtech.os.partitions.ibm.IBMPartitionTable;
import com.qtech.os.system.resource.ResourceNotFreeException;

public interface IDEDeviceFactory {
    /**
     * The name used to lookup this service.
     */
    public static final Class<IDEDeviceFactory> NAME = IDEDeviceFactory.class;

    IDEDevice createIDEDevice(IDEBus bus, boolean primary, boolean master, String name,
                              IDEDriveDescriptor descriptor, DefaultIDEControllerDriver controller);

    IDEBus createIDEBus(Device parent, boolean primary)
        throws IllegalArgumentException, DriverException, ResourceNotFreeException;

    IDEIO createIDEIO(Device parent, boolean primary)
        throws IllegalArgumentException, DriverException, ResourceNotFreeException;

    IBMPartitionTable createIBMPartitionTable(byte[] bs, Device dev);
}
