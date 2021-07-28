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
 
package com.qtech.os.install.cmdline;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Scanner;
import javax.naming.NameNotFoundException;
import com.qtech.os.driver.console.ConsoleManager;
import com.qtech.os.driver.console.TextConsole;
import com.qtech.os.install.AbstractInstaller;
import com.qtech.os.install.InputContext;
import com.qtech.os.install.OutputContext;
import com.qtech.os.install.action.CopyFilesAction;
import com.qtech.os.install.action.GrubInstallerAction;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.util.NumberUtils;
import com.qtech.os.util.ReaderInputStream;

/**
 * @author Levente S\u00e1ntha
 */
public class CommandLineInstaller extends AbstractInstaller {

    public CommandLineInstaller() {
        //grub
        actionList.add(new GrubInstallerAction());
        //files
        actionList.add(new CopyFilesAction());
    }

    public static void main(String... argv) {
        new CommandLineInstaller().start();
    }


    protected InputContext getInputContext() {
        final Reader in1;
        try {
            TextConsole focus = (TextConsole) (InitialNaming.lookup(ConsoleManager.NAME)).getFocus();
            in1 = focus.getIn();
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new InputContext() {
            private BufferedReader in = new BufferedReader(in1);

            public String getStringInput(String message) {
                try {
                    System.out.print(message);
//                    int read = in.read();
//                    if (read < 16) {
//                        System.out.print("0" + NumberUtils.hex(read));
//                    } else {
//                        System.out.print(NumberUtils.hex(read));
//                    }
//                    while (true) {
//                        read = in.read();
//                        if (read < 16) {
//                            System.out.print(":0" + NumberUtils.hex(read));
//                        } else {
//                            System.out.print(":" + NumberUtils.hex(read));
//                        }
//                    }

//                    StringWriter strWriter = new StringWriter();
//                    BufferedWriter bufferedWriter = new BufferedWriter(strWriter);
//
//                    while (true) {
//                        int read = in.read();
//                        if (read == '\n' || read == '\r') {
//                            break;
//                        }
//                        if (read == -1) {
//                            continue;
//                        }
//
//                        System.out.print((char) read);
//
//                        bufferedWriter.write(read);
//                    }
                    String s = in.readLine();
                    System.out.println(NumberUtils.hex(s.getBytes()));
                    return s;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    protected OutputContext getOutputContext() {
        return new OutputContext() {
            public void showMessage(String msg) {
                System.out.println(msg);
            }
        };
    }
}
