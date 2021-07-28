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
 
package com.qtech.os.fs.ext2;

import com.qtech.os.driver.Device;
import com.qtech.os.fs.FileSystemException;
import com.qtech.os.fs.Formatter;
import com.qtech.os.fs.service.FileSystemService;
import com.qtech.os.naming.InitialNaming;
import javax.naming.NameNotFoundException;

/**
 * @author Andras Nagy
 */
public class Ext2FileSystemFormatter extends Formatter<Ext2FileSystem> {
    private BlockSize blockSize;

    /**
     * 
     * @param blockSize size of blocks in KB
     */
    public Ext2FileSystemFormatter(BlockSize blockSize) {
        super(new Ext2FileSystemType());
        this.blockSize = blockSize;
    }

    /*
     * (non-Javadoc)
     * @see com.qtech.os.fs.Formatter#format(com.qtech.os.driver.Device)
     */
    public synchronized Ext2FileSystem format(Device device) throws FileSystemException {
        try {
            FileSystemService fSS = InitialNaming.lookup(FileSystemService.NAME);
            Ext2FileSystemType type = fSS.getFileSystemType(Ext2FileSystemType.ID);
            Ext2FileSystem fs = new Ext2FileSystem(device, false, type);
            fs.create(blockSize);
            return fs;
        } catch (NameNotFoundException e) {
            throw new FileSystemException(e);
        }
    }
}
