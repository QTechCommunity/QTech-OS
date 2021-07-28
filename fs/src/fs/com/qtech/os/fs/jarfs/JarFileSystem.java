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

import java.io.IOException;
import java.util.Map;
import java.util.jar.JarFile;
import com.qtech.os.driver.block.JarFileDevice;
import com.qtech.os.fs.FSDirectory;
import com.qtech.os.fs.FSEntry;
import com.qtech.os.fs.FSFile;
import com.qtech.os.fs.FileSystemException;
import com.qtech.os.fs.spi.AbstractFileSystem;

/**
 * @author Fabien DUMINY (fduminy at users.sourceforge.net)
 */
public class JarFileSystem extends AbstractFileSystem<JarFSEntry> {

    private JarFile jarFile;
    private JarFSCache cache;
    private JarFSEntry rootEntry;

    /**
     * @see com.qtech.os.fs.FileSystem#getDevice()
     */
    public JarFileSystem(JarFileDevice device, JarFileSystemType type) throws FileSystemException {
        super(device, true, type); // jar file systems are always readOnly

        jarFile = device.getJarFile();
        cache = new JarFSCache();
        rootEntry = FSTreeBuilder.build(this, jarFile, cache);
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    protected FSFile createFile(FSEntry entry) {
        return new JarFSFile((JarFSEntry) entry);
    }

    protected FSDirectory createDirectory(FSEntry entry) {
        Map<String, JarFSEntry> entries = cache.getChildEntries((JarFSEntry) entry);
        return new JarFSDirectory((JarFSEntry) entry, entries);
    }

    protected JarFSEntry createRootEntry() {
        return rootEntry;
    }

    public long getFreeSpace() {
        // TODO implement me
        return -1;
    }

    public long getTotalSpace() {
        // TODO implement me
        return -1;
    }

    public long getUsableSpace() {
        // TODO implement me
        return -1;
    }

    @Override
    public String getVolumeName() throws IOException {
        return jarFile.getName();
    }
}
