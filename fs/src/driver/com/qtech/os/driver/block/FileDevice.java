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
 
package com.qtech.os.driver.block;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import com.qtech.os.driver.Device;
import com.qtech.os.partitions.PartitionTableEntry;
import com.qtech.os.util.ByteBufferUtils;

/**
 * This class is a device wrapping a simple file
 *
 * @author epr
 */
public class FileDevice extends Device implements FSBlockDeviceAPI {

    private RandomAccessFile raf;

    /**
     * Create a new FileDevice
     *
     * @param file
     * @param mode
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileDevice(File file, String mode) throws FileNotFoundException, IOException {
        super(null, "file" + System.currentTimeMillis());
        raf = new RandomAccessFile(file, mode);
        //registerAPI(BlockDeviceAPI.class, this);
        registerAPI(FSBlockDeviceAPI.class, this);
    }

    /**
     * @return The length
     * @throws IOException
     * @see com.qtech.os.driver.block.BlockDeviceAPI#getLength()
     */
    public long getLength() throws IOException {
        return raf.length();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.qtech.os.driver.block.BlockDeviceAPI#read(long, java.nio.ByteBuffer)
     */
    public void read(long devOffset, ByteBuffer destBuf) throws IOException {
        BlockDeviceAPIHelper.checkBounds(this, devOffset, destBuf.remaining());
        raf.seek(devOffset);

        //TODO optimize it also to use ByteBuffer at lower level
        ByteBufferUtils.ByteArray destBA = ByteBufferUtils.toByteArray(destBuf);
        byte[] dest = destBA.toArray();
        raf.read(dest, 0, dest.length);
        destBA.refreshByteBuffer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.qtech.os.driver.block.BlockDeviceAPI#write(long, java.nio.ByteBuffer)
     */
    public void write(long devOffset, ByteBuffer srcBuf) throws IOException {
        //log.debug("fd.write devOffset=" + devOffset + ", length=" + length);
        BlockDeviceAPIHelper.checkBounds(this, devOffset, srcBuf.remaining());
        raf.seek(devOffset);

        //TODO optimize it also to use ByteBuffer at lower level
        byte[] src = ByteBufferUtils.toArray(srcBuf);
        raf.write(src, 0, src.length);
    }

    /**
     * @see com.qtech.os.driver.block.BlockDeviceAPI#flush()
     */
    public void flush() {
        // Nothing to flush
    }

    /**
     * change the length of the underlaying file
     *
     * @param length
     * @throws IOException
     */
    public void setLength(long length) throws IOException {
        raf.setLength(length);
    }

    /**
     * close the underlaying file
     *
     * @throws IOException
     */
    public void close() throws IOException {
        raf.close();
    }

    /**
     * @see com.qtech.os.driver.block.FSBlockDeviceAPI#getPartitionTableEntry()
     */
    public PartitionTableEntry getPartitionTableEntry() {
        return null;
    }

    /**
     * @see com.qtech.os.driver.block.FSBlockDeviceAPI#getSectorSize()
     */
    public int getSectorSize() throws IOException {
        return 512;
    }
}
