/*
 * $Id$
 *
 * Copyright (C) 2003-2015 JNode.org
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
 
package org.jnode.install.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import org.jnode.driver.Device;
import org.jnode.driver.DeviceAPI;
import org.jnode.driver.DeviceNotFoundException;
import org.jnode.driver.DeviceUtils;
import org.jnode.driver.block.BlockDeviceAPI;
import org.jnode.driver.block.FSBlockDeviceAPI;
import org.jnode.fs.jfat.command.JGrub;
import org.jnode.install.AbstractInstaller;
import org.jnode.install.ActionInput;
import org.jnode.install.ActionOutput;
import org.jnode.install.InputContext;
import org.jnode.install.InstallerAction;
import org.jnode.install.OutputContext;
import org.jnode.util.StringUtils;

/**
 * @author Levente S\u00e1ntha
 */
public class GrubInstallerAction implements InstallerAction {
    private JGrub jgrub;

    public ActionInput getInput(final InputContext inContext) {
        return new ActionInput() {
            public AbstractInstaller.Step collect() {
                try {
                    String deviceID = inContext.getStringInput("Enter the installation disk device name (example: hda0) : ");

                    Device disk = DeviceUtils.getDevice(deviceID);
                    jgrub = new JGrub(new PrintWriter(new OutputStreamWriter(System.out)), disk);

                    inContext.setStringValue(ActionConstants.INSTALL_ROOT_DIR, jgrub.getMountPoint());
                    return AbstractInstaller.Step.forth;
                } catch (Exception e) {
                    if (e instanceof DeviceNotFoundException) {
                        System.err.println("Device not found: " + e.getLocalizedMessage());
                        Collection<Device> devicesByAPI = DeviceUtils.getDevicesByAPI(DeviceAPI.class);
                        List<String> deviceIds = new ArrayList<String>();
                        for (Device device : devicesByAPI) {
                            deviceIds.add(device.getId());
                        }

                        System.out.println("Available Disks: " + StringUtils.join(", ", deviceIds));
                    } else {
                        StringWriter stringWriter = new StringWriter();
                        PrintWriter1 printWriter1 = new PrintWriter1(stringWriter, true);
                        e.printStackTrace(printWriter1);
                        StringBuffer buffer = stringWriter.getBuffer();
                        String s = buffer.toString();
                        String[] split = s.split("\n");
                        for (String str : split) {
                            System.out.println(str);
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }

                    return AbstractInstaller.Step.back;
                }
            }
        };
    }

    public void execute() throws Exception {
        jgrub.install();
    }

    public ActionOutput getOutput(OutputContext outContext) {
        return null;
    }

    public static class PrintWriter1 extends PrintWriter {
        public PrintWriter1(Writer out) {
            super(out);
        }

        public PrintWriter1(Writer out, boolean autoFlush) {
            super(out, autoFlush);
        }

        public PrintWriter1(OutputStream out) {
            super(out);
        }

        public PrintWriter1(OutputStream out, boolean autoFlush) {
            super(out, autoFlush);
        }

        public PrintWriter1(String fileName) throws FileNotFoundException {
            super(fileName);
        }

        public PrintWriter1(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
            super(fileName, csn);
        }

        public PrintWriter1(File file) throws FileNotFoundException {
            super(file);
        }

        public PrintWriter1(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
            super(file, csn);
        }

        @Override
        public void write(int c) {
            if (c == '\r' || c == '\n') {
                Scanner scanner = new Scanner(System.in);
                scanner.nextByte();
            }
            super.write(c);
        }
    }
}
