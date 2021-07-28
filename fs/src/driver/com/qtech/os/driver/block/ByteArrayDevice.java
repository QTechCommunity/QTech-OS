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

import java.nio.ByteBuffer;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.Driver;
import com.qtech.os.driver.InvalidDriverException;

/**
 * This class is a device wrapping a simple byte array
 *
 * @author epr
 */
public class ByteArrayDevice extends Device implements BlockDeviceAPI {

    private byte[] array;

    /**
     * Create a new ByteArrayDevice wrapping the given array
     *
     * @param array
     */
    public ByteArrayDevice(byte[] array) {
        super(null, "byte-array" + System.currentTimeMillis());
        this.array = array;
        registerAPI(BlockDeviceAPI.class, this);
    }

    /**
     * @return The length
     * @see com.qtech.os.driver.block.BlockDeviceAPI#getLength()
     */
    public long getLength() {
        return array.length;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.qtech.os.driver.block.BlockDeviceAPI#read(long, java.nio.ByteBuffer)
     */
    public void read(long devOffset, ByteBuffer dest) {
        //System.arraycopy(array, (int)devOffset, dest, destOffset, length);
        dest.put(this.array, (int) devOffset, dest.remaining());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.qtech.os.driver.block.BlockDeviceAPI#write(long, java.nio.ByteBuffer)
     */
    public void write(long devOffset, ByteBuffer src) {
        //System.arraycopy(src, srcOffset, array, (int)devOffset, length);
        src.get(this.array, (int) devOffset, src.remaining());
    }

    /**
     * @see com.qtech.os.driver.block.BlockDeviceAPI#flush()
     */
    public void flush() {
        /* Nothing to do here */
    }

    /**
     * @param driver
     * @throws InvalidDriverException
     * @see com.qtech.os.driver.Device#setDriver(com.qtech.os.driver.Driver)
     */
    public void setDriver(Driver driver) throws InvalidDriverException {
        throw new InvalidDriverException("No driver allowed here.");
    }
}
