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
 
package com.qtech.os.apps.jpartition.utils.device;

import java.io.File;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import com.qtech.os.apps.jpartition.ErrorReporter;
import com.qtech.os.apps.jpartition.swingview.FileDeviceView;
import com.qtech.os.apps.vmware.disk.VMWareDisk;
import com.qtech.os.apps.vmware.disk.tools.DiskFactory;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAlreadyRegisteredException;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.DriverException;
import com.qtech.os.driver.bus.ide.IDEDevice;
import com.qtech.os.emu.naming.BasicNameSpace;
import com.qtech.os.emu.plugin.model.DummyConfigurationElement;
import com.qtech.os.emu.plugin.model.DummyExtension;
import com.qtech.os.emu.plugin.model.DummyExtensionPoint;
import com.qtech.os.emu.plugin.model.DummyPluginDescriptor;
import com.qtech.os.fs.service.FileSystemService;
import com.qtech.os.fs.service.def.FileSystemPlugin;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.naming.NameSpace;
import com.qtech.os.test.fs.driver.stubs.StubDeviceManager;
import com.qtech.os.test.fs.filesystem.config.FSType;
import com.qtech.os.util.OsUtils;


public class DeviceUtils {
    private static final long DEFAULT_FILE_SIZE = 1024 * 1024;
    private static final Logger log = Logger.getLogger(FileDeviceView.class);

    private static boolean coreInitialized = false;

    public static final void initJNodeCore() {
        if (!coreInitialized && !OsUtils.isQOS()) {

            try {
                // ShellEmu.main(new String[0]);
                NameSpace namespace = new BasicNameSpace();
                InitialNaming.setNameSpace(namespace);
                InitialNaming.bind(DeviceManager.NAME, StubDeviceManager.INSTANCE);
                
                // Build a plugin descriptor that is sufficient for the FileSystemPlugin to 
                // configure file system types for testing.
                DummyPluginDescriptor desc = new DummyPluginDescriptor(true);
                DummyExtensionPoint ep = new DummyExtensionPoint("types", "com.qtech.os.fs.types", "types");
                desc.addExtensionPoint(ep);
                for (FSType fsType : FSType.values()) {
                    DummyExtension extension = new DummyExtension();
                    DummyConfigurationElement element = new DummyConfigurationElement();
                    element.addAttribute("class", fsType.getFsTypeClass().getName());
                    extension.addElement(element);
                    ep.addExtension(extension);
                }
                
                FileSystemService fss = new FileSystemPlugin(desc);
                InitialNaming.bind(FileSystemService.class, fss);
                
            } catch (NameAlreadyBoundException e) {
                throw new RuntimeException(e);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            coreInitialized = true;
        }
        
        
//        if (!coreInitialized && !OsUtils.isJNode()) {
//            // We are not running in JNode, emulate a JNode environment.
//
//            InitialNaming.setNameSpace(new BasicNameSpace());
//
//            // Build a plugin descriptor that is sufficient for the FileSystemPlugin to
//            // configure file system types for testing.
//            DummyPluginDescriptor desc = new DummyPluginDescriptor(true);
//            DummyExtensionPoint ep = new DummyExtensionPoint("types", "com.qtech.os.fs.types", "types");
//            desc.addExtensionPoint(ep);
//            for (FSType fsType : FSType.values()) {
//                DummyExtension extension = new DummyExtension();
//                DummyConfigurationElement element = new DummyConfigurationElement();
//                element.addAttribute("class", fsType.getFsTypeClass().getName());
//                extension.addElement(element);
//                ep.addExtension(extension);
//            }
//
//            FileSystemService fss = new FileSystemPlugin(desc);
//            try {
//                InitialNaming.bind(FileSystemService.class, fss);
//            } catch (NameAlreadyBoundException e) {
//                throw new RuntimeException(e);
//            } catch (NamingException e) {
//                throw new RuntimeException(e);
//            }
//            coreInitialized = true;
//        }        
    }

    public static IDEDevice createFakeDevice(ErrorReporter errorReporter) {
        IDEDevice device = null;
        try {
            String name = findUnusedName("fake");
            FakeIDEDevice fd = new FakeIDEDevice(name, true, true, DEFAULT_FILE_SIZE);
            if (addDevice(fd)) {
                device = fd;
            } else {
                errorReporter.reportError(log, DeviceUtils.class.getName(), "failed to add device");
            }
        } catch (Exception e) {
            log.error(e);
        }

        return device;
    }

    public static IDEDevice createVMWareDevice(ErrorReporter errorReporter) {
        IDEDevice device = null;

        try {
            AbstractIDEDevice fd = createVMWareDevice();
            if (addDevice(fd)) {
                device = fd;
            } else {
                errorReporter.reportError(log, DeviceUtils.class.getName(), "failed to add device");
            }
        } catch (Exception e) {
            log.error(e);
        }

        return device;
    }

    public static IDEDevice createFileDevice(ErrorReporter errorReporter) {
        IDEDevice device = null;

        try {
            AbstractIDEDevice fd = createFileDevice();
            if (addDevice(fd)) {
                device = fd;
            } else {
                errorReporter.reportError(log, DeviceUtils.class.getName(), "failed to add device");
            }
        } catch (Exception e) {
            log.error(e);
        }

        return device;
    }

    private static AbstractIDEDevice createVMWareDevice() throws Exception {
        File tmpFile = File.createTempFile("disk", "");
        File directory = tmpFile.getParentFile();
        String name = tmpFile.getName();

        File mainFile = DiskFactory.createSparseDisk(directory, name, DEFAULT_FILE_SIZE);
        VMWareDisk vmwareDisk = new VMWareDisk(mainFile);

        AbstractIDEDevice dev = new VMWareIDEDevice(name, true, true, vmwareDisk);
        return dev;
    }

    private static AbstractIDEDevice createFileDevice() throws Exception {
        File tmpFile = File.createTempFile("disk", "");
        File directory = tmpFile.getParentFile();
        String name = tmpFile.getName();

        AbstractIDEDevice dev =
                new FileIDEDevice(name, true, true, new File(directory, name), DEFAULT_FILE_SIZE);
        return dev;
    }

    public static void restart(Device device) {
        DeviceManager devMan;
        try {
            devMan = com.qtech.os.driver.DeviceUtils.getDeviceManager();

            devMan.stop(device);
            devMan.start(device);
        } catch (NameNotFoundException e) {
            log.error(e);
        } catch (DeviceNotFoundException e) {
            log.error(e);
        } catch (DriverException e) {
            log.error(e);
        }
    }

    public static boolean addDevice(AbstractIDEDevice device) {
        boolean success = false;
        try {
            DeviceManager devMan = com.qtech.os.driver.DeviceUtils.getDeviceManager();
            devMan.register(device);
            success = true;

            // PartitionHelper helper = new PartitionHelper(device);
            // helper.initMbr();
            // helper.write();
            // if(helper.hasValidMBR())
            // {
            // helper.modifyPartition(0, true, 0, DEFAULT_FILE_SIZE,
            // false, IBMPartitionTypes.PARTTYPE_WIN95_FAT32);
            // }
        } catch (NameNotFoundException e) {
            log.error(e);
        } catch (DeviceAlreadyRegisteredException e) {
            log.error(e);
        } catch (DriverException e) {
            log.error(e);
            // } catch (DeviceNotFoundException e) {
            // log.error(e);
            // } catch (ApiNotFoundException e) {
            // log.error(e);
            // } catch (IOException e) {
            // log.error(e);
        }

        return success;
    }

    public static String findUnusedName(String baseName) throws NameNotFoundException {
        DeviceManager devMan = com.qtech.os.driver.DeviceUtils.getDeviceManager();
        String name = null;
        int i = 0;
        do {
            String newName = baseName + "-" + i;
            try {
                devMan.getDevice(newName);
                i++;
            } catch (DeviceNotFoundException e) {
                name = newName;
            }
        } while (name == null);

        return name;
    }
}
