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
 
package com.qtech.os.fs.jarfs;

import gnu.java.nio.InputStreamChannel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.jar.JarFile;

import com.qtech.os.fs.FSFile;
import com.qtech.os.fs.FileSystem;
import com.qtech.os.fs.ReadOnlyFileSystemException;

/** 
 * @author Fabien DUMINY (fduminy at users.sourceforge.net)
 */
public class JarFSFile implements FSFile {

    private final JarFSEntry entry;

    /**
     * @param entry
     */
    public JarFSFile(JarFSEntry entry) {
        this.entry = entry;
    }

    /**
     * @see com.qtech.os.fs.FSFile#getLength()
     */
    public long getLength() {
        return entry.getJarEntry().getSize();
    }

    /**
     * @see com.qtech.os.fs.FSFile#setLength(long)
     */
    public void setLength(long length) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @see com.qtech.os.fs.FSFile#read(long, ByteBuffer)
     */
    public void read(long fileOffset, ByteBuffer destBuf) throws IOException {
        final JarFileSystem fs = (JarFileSystem) getFileSystem();
        final JarFile jarFile = fs.getJarFile();
        final InputStream is = jarFile.getInputStream(entry.getJarEntry());
        is.skip(fileOffset);
        InputStreamChannel isc = new InputStreamChannel(is);
        isc.read(destBuf);
        isc.close();
        is.close();
    }

    /**
     * @see com.qtech.os.fs.FSFile#write(long, ByteBuffer)
     */ 
    public void write(long fileOffset, ByteBuffer src) throws IOException {
        throw new ReadOnlyFileSystemException("Not yet implemented");
    }

    /**
     * @see com.qtech.os.fs.FSFile#flush()
     */
    public void flush() throws IOException {
        // Readonly
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @see com.qtech.os.fs.FSObject#isValid()
     */
    public boolean isValid() {
        return true;
    }

    /**
     * @see com.qtech.os.fs.FSObject#getFileSystem()
     */
    public final FileSystem<?> getFileSystem() {
        return entry.getFileSystem();
    }
}
