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
 
package com.qtech.os.fs.service.def;

import java.io.File;
import java.io.IOException;
import com.qtech.os.fs.FSAccessRights;
import com.qtech.os.fs.FSDirectory;
import com.qtech.os.fs.FSEntry;
import com.qtech.os.fs.FSFile;
import com.qtech.os.fs.FileSystem;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
final class VirtualMountEntry implements FSEntry {

    private final FileSystem<?> mountedFS;

    private final String name;

    private final FSDirectory parent;

    private final FSEntry root;

    /**
     * Initialize this instance.
     *
     * @param mountedFS
     * @param path      Path in the mountedFS (null or "" for root)
     * @param name
     * @param parent
     * @throws IOException
     */
    VirtualMountEntry(FileSystem<?> mountedFS, String path, String name, VirtualDirEntry parent)
        throws IOException {
        this.mountedFS = mountedFS;
        this.name = name;
        this.parent = parent.getDirectory();
        if ((path == null) || (path.length() == 0)) {
            this.root = mountedFS.getRootEntry();
        } else {
            FSEntry e = mountedFS.getRootEntry();
            while (path != null) {
                final int idx = path.indexOf(File.separatorChar);
                final String dir;
                if (idx > 0) {
                    dir = path.substring(0, idx);
                    path = path.substring(idx + 1);
                } else {
                    dir = path;
                    path = null;
                }
                e = e.getDirectory().getEntry(dir);
            }
            this.root = e;
        }
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getAccessRights()
     */
    public FSAccessRights getAccessRights() throws IOException {
        return root.getAccessRights();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getDirectory()
     */
    public FSDirectory getDirectory() throws IOException {
        return root.getDirectory();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getFile()
     */
    public FSFile getFile() throws IOException {
        return root.getFile();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getLastModified()
     */
    public long getLastModified() throws IOException {
        return root.getLastModified();
    }

    @Override
    public String getId() {
        return getName();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getParent()
     */
    public FSDirectory getParent() {
        return parent;
    }

    /**
     * @see com.qtech.os.fs.FSEntry#isDirectory()
     */
    public boolean isDirectory() {
        return root.isDirectory();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#isDirty()
     */
    public boolean isDirty() throws IOException {
        return root.isDirty();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#isFile()
     */
    public boolean isFile() {
        return root.isFile();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#setLastModified(long)
     */
    public void setLastModified(long lastModified) throws IOException {
        root.setLastModified(lastModified);
    }

    /**
     * @see com.qtech.os.fs.FSEntry#setName(java.lang.String)
     */
    public void setName(String newName) throws IOException {
        throw new IOException("Cannot set name of mount entry");
    }

    /**
     * @see com.qtech.os.fs.FSObject#getFileSystem()
     */
    public FileSystem<?> getFileSystem() {
        return mountedFS;
    }

    /**
     * @see com.qtech.os.fs.FSObject#isValid()
     */
    public boolean isValid() {
        return !mountedFS.isClosed();
    }

    /**
     * @return Returns the mountedFS.
     */
    public final FileSystem<?> getMountedFS() {
        return mountedFS;
    }
}
