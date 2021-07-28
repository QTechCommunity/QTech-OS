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
 
package com.qtech.os.fs.jifs.def;

import java.io.IOException;

import javax.naming.NameNotFoundException;

import org.apache.log4j.Logger;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.DeviceUtils;
import com.qtech.os.driver.virtual.VirtualDevice;
import com.qtech.os.fs.FSEntry;
import com.qtech.os.fs.jifs.ExtFSEntry;
import com.qtech.os.fs.jifs.JIFSDirectory;
import com.qtech.os.fs.jifs.JIFileSystemType;
import com.qtech.os.fs.service.FileSystemService;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.plugin.ConfigurationElement;
import com.qtech.os.plugin.Extension;
import com.qtech.os.plugin.ExtensionPoint;
import com.qtech.os.plugin.ExtensionPointListener;


/**
 * @author Andreas H\u00e4nel
 */
final class JIFSExtension implements ExtensionPointListener {

    /** My logger */
    private static final Logger log = Logger.getLogger(JIFSExtension.class);

    /** The com.qtech.os.fs.jifs.def.info extension point */
    private final ExtensionPoint infoEP;
    private JIFSDirectory extdir;

    protected JIFSExtension(ExtensionPoint infoEP) {

        if (infoEP == null) {
            throw new IllegalArgumentException("The info extension-point cannot be null.");
        }
        this.infoEP = infoEP;
        try {
            FileSystemService fSS = InitialNaming.lookup(FileSystemService.NAME);
            final DeviceManager dm = DeviceUtils.getDeviceManager();
            VirtualDevice dev = (VirtualDevice) dm.getDevice(JIFileSystemType.VIRTUAL_DEVICE_NAME);
            JIFSDirectory rootdir = (JIFSDirectory) fSS.getFileSystem(dev).getRootEntry();
            extdir = (JIFSDirectory) rootdir.getEntry("extended").getDirectory();
        } catch (NameNotFoundException e) {
            log.error("filesystemservice / filesystemtype not found");
        } catch (DeviceNotFoundException ex) {
            log.info("no jifs present");
        } catch (IOException ex) {
            log.error(ex);
        }
        refresh();
    }

    /**
     * An extension has been added to an extension point
     * 
     * @param point
     * @param extension
     */
    public void extensionAdded(ExtensionPoint point, Extension extension) {
        refresh();
    }

    /**
     * An extension has been removed from an extension point
     * 
     * @param point
     * @param extension
     */
    public void extensionRemoved(ExtensionPoint point, Extension extension) {
        refresh();
    }

    private void refresh() {
        if (extdir == null) {
            return;
        }
        extdir.refresh();
        final Extension[] extensions = infoEP.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            final Extension ext = extensions[i];
            final ConfigurationElement[] elements = ext.getConfigurationElements();
            for (int j = 0; j < elements.length; j++) {
                addEntry(elements[j]);
            }
        }
    }

    private void addEntry(ConfigurationElement element) {
        final String className = element.getAttribute("class");
        final String entryName = element.getAttribute("name");
        if (className != null) {
            try {
                final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Class<?> c = cl.loadClass(className);
                Object o = c.newInstance();
                ExtFSEntry entry = (ExtFSEntry) o;
                if (entry.getName() == null) {
                    if (entryName != null) {
                        entry.setName(entryName);
                    } else {
                        entry.setName(className);
                    }
                }
                entry.setParent(extdir);
                FSEntry add = entry;
                extdir.addFSE(add);
            } catch (ClassCastException ex) {
                log.error("Given class " + className + " does not implement .");
            } catch (ClassNotFoundException ex) {
                log.error("Cannot load  " + className);
            } catch (IllegalAccessException ex) {
                log.error("No access to  " + className);
            } catch (InstantiationException ex) {
                log.error(ex);
            } catch (IOException ex) {
                log.error("could not set name of file: " + entryName);
            }
        }
    }
}
