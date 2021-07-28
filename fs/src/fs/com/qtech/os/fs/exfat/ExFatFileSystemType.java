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
 
package com.qtech.os.fs.exfat;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.block.FSBlockDeviceAPI;
import com.qtech.os.fs.BlockDeviceFileSystemType;
import com.qtech.os.fs.FileSystemException;
import com.qtech.os.partitions.PartitionTableEntry;

/**
 * @author Luke Quinane
 */
public class ExFatFileSystemType implements BlockDeviceFileSystemType<ExFatFileSystem> {
    public static final Class<ExFatFileSystemType> ID = ExFatFileSystemType.class;

    @Override
    public ExFatFileSystem create(Device device, boolean readOnly) throws FileSystemException {
        return new ExFatFileSystem(device, readOnly, this);
    }

    @Override
    public String getName() {
        return "exFAT";
    }

    @Override
    public boolean supports(PartitionTableEntry pte, byte[] firstSector, FSBlockDeviceAPI devApi) {

        // Check for ExFAT
        if (firstSector[11] == 0x0 &&
            firstSector[3] == 'E' &&
            firstSector[4] == 'X' &&
            firstSector[5] == 'F' &&
            firstSector[6] == 'A' &&
            firstSector[7] == 'T') {
            return true;
        }

        return false;
    }
}
