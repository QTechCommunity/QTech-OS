/*
 * $Id$
 *
 * Copyright (C) 2003-2013 QTech Community
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
 
package com.qtech.os.fs.smbfs.command;

import java.io.File;

import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceUtils;
import com.qtech.os.driver.DriverException;
import com.qtech.os.fs.service.FileSystemService;
import com.qtech.os.fs.smbfs.SMBFSDevice;
import com.qtech.os.fs.smbfs.SMBFSDriver;
import com.qtech.os.fs.smbfs.SMBFileSystem;
import com.qtech.os.fs.smbfs.SMBFileSystemType;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.syntax.Argument;
import com.qtech.os.shell.syntax.FileArgument;
import com.qtech.os.shell.syntax.HostNameArgument;
import com.qtech.os.shell.syntax.StringArgument;

/**
 * @author Levente S\u00e1ntha
 * @author crawley@jnode.org
 */
public class SMBMountCommand extends AbstractCommand {
    private final FileArgument MOUNTPOINT_ARG = 
        new FileArgument("directory", Argument.MANDATORY, "the mountpoint");
    private final HostNameArgument HOST_ARG = 
        new HostNameArgument("host", Argument.MANDATORY, "Samba host");
    private final StringArgument PATH_ARG = 
        new StringArgument("path", Argument.MANDATORY, "Samba path");
    private static final StringArgument USERNAME_ARG = 
        new StringArgument("username", Argument.MANDATORY, "Samba user");
    private static final StringArgument PASSWORD_ARG = 
        new StringArgument("password", Argument.OPTIONAL, "Samba password");
    
    public SMBMountCommand() {
        super("Mount a Samba filesystem");
        registerArguments(MOUNTPOINT_ARG, HOST_ARG, PATH_ARG, USERNAME_ARG, PASSWORD_ARG);
    }

    public static void main(String[] args) throws Exception {
        new SMBMountCommand().execute(args);
    }

    public void execute() throws Exception {
        final File mountPoint = MOUNTPOINT_ARG.getValue();
        final String host = HOST_ARG.getValue();
        final String path = PATH_ARG.getValue();
        final String user = USERNAME_ARG.getValue();
        final String password = PASSWORD_ARG.getValue();
        
        final FileSystemService fss = InitialNaming.lookup(FileSystemService.NAME);
        SMBFileSystemType type = fss.getFileSystemType(SMBFileSystemType.ID);
        
        final SMBFSDevice dev = new SMBFSDevice(host, path, user, password);
        dev.setDriver(new SMBFSDriver());
        final DeviceManager dm = DeviceUtils.getDeviceManager();
        dm.register(dev);
        
        // This controls whether we attempt to undo the effects of the command
        // e.g. when the 'mount' step fails.
        boolean ok = false;
        try {
            final SMBFileSystem fs = type.create(dev, true);
            fss.registerFileSystem(fs);
            try {
                fss.mount(mountPoint.toString(), fs, null);
                ok = true;
            } finally {
                if (!ok) {
                    fss.unregisterFileSystem(dev);
                }
            }
        } finally {
            try {
                if (!ok) {
                    dm.unregister(dev);
                }
            } catch (DriverException ex) {
                // ignore
            }
        }
    }
}
