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
 
package com.qtech.os.driver.block.ide.disk;

import com.qtech.os.driver.Bus;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.bus.ide.IDEDevice;
import com.qtech.os.partitions.PartitionTableEntry;

/**
 * @author epr
 */
public class IDEDiskPartitionDevice extends Device {

    /**
     * The device i'm a partition on
     */
    private final IDEDevice parent;
    /**
     * The first sector of this partition
     */
    private final long startSector;
    /**
     * The number of sectors of this partition
     */
    private final long sectors;
    private final PartitionTableEntry pte;

    /**
     * Create a new instance
     *
     * @param id
     */
    public IDEDiskPartitionDevice(Bus bus, String id, IDEDevice parent, PartitionTableEntry pte, long startSector,
                                  long sectors) {
        super(bus, id);
        this.parent = parent;
        this.pte = pte;
        this.startSector = startSector;
        this.sectors = sectors;
    }

    /**
     * Gets the device this partition is on.
     */
    public IDEDevice getParent() {
        return parent;
    }

    /**
     * Gets the number of sectors of this partition
     */
    public long getSectors() {
        return sectors;
    }

    /**
     * Gets the first sector of this partition
     */
    public long getStartSector() {
        return startSector;
    }

    /**
     * Gets the partition table entry specifying this device.
     *
     * @return A PartitionTableEntry or null if no partition table entry exists.
     */
    public PartitionTableEntry getPartitionTableEntry() {
        return pte;
    }
}
