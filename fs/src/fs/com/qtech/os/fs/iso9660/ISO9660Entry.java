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
 
package com.qtech.os.fs.iso9660;

import java.io.IOException;
import com.qtech.os.fs.FSAccessRights;
import com.qtech.os.fs.FSDirectory;
import com.qtech.os.fs.FSEntry;
import com.qtech.os.fs.FSFile;
import com.qtech.os.fs.FileSystem;

/**
 * @author Chira
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public final class ISO9660Entry implements FSEntry {

    private ISO9660FileSystem fs;
    private EntryRecord entryRecord = null;

    public ISO9660Entry(ISO9660FileSystem fs, EntryRecord entry) {
        this.fs = fs;
        this.entryRecord = entry;
    }

    @Override
    public String getId() {
        return getName();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getName()
     */
    public String getName() {
        return entryRecord.getFileIdentifier();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getParent()
     */
    public FSDirectory getParent() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getLastModified()
     */
    public long getLastModified() throws IOException {
        return entryRecord.getRecordingTime().toJavaMillis();
    }

    public long getLastAccessed() {
        return 0;
    }

    /**
     * @see com.qtech.os.fs.FSEntry#isFile()
     */
    public boolean isFile() {
        return !entryRecord.isDirectory();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#isDirectory()
     */
    public boolean isDirectory() {
        return entryRecord.isDirectory();
    }

    /**
     * @see com.qtech.os.fs.FSEntry#setName(java.lang.String)
     */
    public void setName(String newName) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * @see com.qtech.os.fs.FSEntry#setLastModified(long)
     */
    public void setLastModified(long lastModified) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void setLastAccessed(long lastAccessed) {
        throw new UnsupportedOperationException("Filesystem is read-only");
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getFile()
     */
    public FSFile getFile() throws IOException {
        return new ISO9660File(this);
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getDirectory()
     */
    public FSDirectory getDirectory() throws IOException {
        return new ISO9660Directory(this);
    }

    /**
     * @see com.qtech.os.fs.FSEntry#getAccessRights()
     */
    public FSAccessRights getAccessRights() throws IOException {
        throw new UnsupportedOperationException("not implemented yet");
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
    public FileSystem<?> getFileSystem() {
        return fs;
    }

    /**
     * @return Returns the cDFSentry.
     */
    public EntryRecord getCDFSentry() {
        return entryRecord;
    }

    /**
     * @param sentry The cDFSentry to set.
     */
    public void setCDFSentry(EntryRecord sentry) {
        entryRecord = sentry;
    }

    /**
     * Indicate if the entry has been modified in memory (ie need to be saved)
     *
     * @return true if the entry need to be saved
     * @throws IOException
     */
    public boolean isDirty() throws IOException {
        return true;
    }
}
