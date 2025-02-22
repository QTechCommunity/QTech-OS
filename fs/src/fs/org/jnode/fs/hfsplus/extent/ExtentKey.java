/*
 * $Id$
 *
 * Copyright (C) 2020-2022 Ultreon Team
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
 
package org.jnode.fs.hfsplus.extent;

import org.jnode.fs.hfsplus.catalog.CatalogNodeId;
import org.jnode.fs.hfsplus.tree.AbstractKey;
import org.jnode.fs.hfsplus.tree.Key;
import org.jnode.util.BigEndian;

public class ExtentKey extends AbstractKey {

    public static final byte DATA_FORK = (byte) 0x00;
    public static final byte RESOURCE_FORK = (byte) 0xFF;
    public static final int KEY_LENGTH = 12;
    public static final int MAXIMUM_KEY_LENGTH = 10;

    private int forkType;
    private int pad;
    private CatalogNodeId fileId;
    private long startBlock;

    /**
     * @param src
     * @param offset
     */
    public ExtentKey(final byte[] src, final int offset) {
        byte[] ek = new byte[KEY_LENGTH];
        System.arraycopy(src, offset, ek, 0, KEY_LENGTH);
        //TODO Understand why the +2 is necessary
        keyLength = BigEndian.getUInt16(ek, 0) + 2;
        forkType = BigEndian.getUInt8(ek, 2);
        pad = BigEndian.getUInt8(ek, 3);
        fileId = new CatalogNodeId(ek, 4);
        startBlock = BigEndian.getUInt32(ek, 8);
    }

    /**
     * @param forkType
     * @param pad
     * @param fileId
     * @param startBlock
     */
    public ExtentKey(int forkType, int pad, CatalogNodeId fileId, int startBlock) {
        super();
        this.forkType = forkType;
        this.pad = pad;
        this.fileId = fileId;
        this.startBlock = startBlock;
    }

    @Override
    public final int compareTo(final Key key) {
        int res = -1;
        if (key instanceof ExtentKey) {
            ExtentKey compareKey = (ExtentKey) key;
            res = fileId.compareTo(compareKey.getFileId());
            if (res == 0) {
                res = compareForkType(compareKey.getForkType());
                if (res == 0) {
                    return compareStartBlock(compareKey.getStartBlock());
                }
            }
        }
        return res;
    }

    @Override
    public int hashCode() {
        return 73 ^ fileId.hashCode() * forkType + (int) startBlock;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExtentKey)) {
            return false;
        }

        ExtentKey otherKey = (ExtentKey) obj;

        return
            fileId.getId() == otherKey.fileId.getId() &&
                forkType == otherKey.forkType;
    }

    @Override
    public byte[] getBytes() {
        byte[] data = new byte[this.getKeyLength()];
        return data;
    }

    private int compareForkType(int fork) {
        Integer currentForkType = Integer.valueOf(forkType);
        Integer forkType = Integer.valueOf(fork);
        return currentForkType.compareTo(forkType);
    }

    private int compareStartBlock(long block) {
        Long currentStartBlock = Long.valueOf(startBlock);
        Long startBlock = Long.valueOf(block);
        return currentStartBlock.compareTo(startBlock);
    }

    public int getForkType() {
        return forkType;
    }

    public int getPad() {
        return pad;
    }

    public CatalogNodeId getFileId() {
        return fileId;
    }

    public long getStartBlock() {
        return startBlock;
    }

    @Override
    public final String toString() {
        return String.format("[%s type:%s start:%d]", fileId, forkType == DATA_FORK ? "data" : "resource", startBlock);
    }
}
