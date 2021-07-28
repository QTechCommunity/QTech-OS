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
 
package com.qtech.os.fs.jifs.files;

import javax.naming.NameNotFoundException;

import com.qtech.os.fs.FSDirectory;
import com.qtech.os.fs.FileSystemType;
import com.qtech.os.fs.jifs.JIFSFile;
import com.qtech.os.fs.service.FileSystemService;
import com.qtech.os.naming.InitialNaming;

/**
 * 
 * @author Andreas H\u00e4nel
 */
public class JIFSFfilesystems extends JIFSFile {

    public JIFSFfilesystems() {
        super("fs");
    }

    public JIFSFfilesystems(FSDirectory parent) {
        this();
        setParent(parent);
    }

    public void refresh() {
        super.refresh();
        try {
            FileSystemService fSS = InitialNaming.lookup(FileSystemService.NAME);
            addStringln("Registered Filesystems:");
            for (FileSystemType<?> current : fSS.fileSystemTypes()) {
                addStringln("\t" + current.getName());

            }
        } catch (NameNotFoundException e) {
            System.err.print(e);
        }
    }

}
