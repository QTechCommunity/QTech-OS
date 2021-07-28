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
 
package com.qtech.os.fs.ntfs;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.block.FSBlockDeviceAPI;
import com.qtech.os.fs.BlockDeviceFileSystemType;
import com.qtech.os.fs.FileSystemException;
import com.qtech.os.partitions.PartitionTableEntry;

/**
 * @author Chira
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class NTFSFileSystemType implements BlockDeviceFileSystemType<NTFSFileSystem> {
    public static final Class<NTFSFileSystemType> ID = NTFSFileSystemType.class;
    public static final String TAG = "NTFS    ";

    public String getName() {
        return "NTFS";
    }

    /**
     * @see com.qtech.os.fs.BlockDeviceFileSystemType#supports(com.qtech.os.partitions.PartitionTableEntry,
     * byte[], com.qtech.os.driver.block.FSBlockDeviceAPI)
     */
    public boolean supports(PartitionTableEntry pte, byte[] firstSectors, FSBlockDeviceAPI devApi) {
        if (firstSectors.length < 0x11) {
            // Not enough data for detection
            return false;
        }

        // Intentionally not checking the PARTTYPE because that often lies.
        return new String(firstSectors, 0x03, 8).equals(TAG);
    }

    /**
     * @see com.qtech.os.fs.FileSystemType#create(Device, boolean)
     */
    public NTFSFileSystem create(Device device, boolean readOnly) throws FileSystemException {
        return new NTFSFileSystem(device, readOnly, this);
    }
}
