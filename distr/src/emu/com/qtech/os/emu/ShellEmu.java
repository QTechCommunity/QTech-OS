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
 
package com.qtech.os.emu;

import java.io.File;

import com.qtech.os.driver.console.ConsoleManager;
import com.qtech.os.driver.console.swing.SwingTextScreenConsoleManager;
import com.qtech.os.driver.console.textscreen.TextScreenConsole;
import com.qtech.os.driver.console.textscreen.TextScreenConsoleManager;
import com.qtech.os.shell.CommandShell;

/**
 * @author Levente S\u00e1ntha
 * @author crawley@jnode.org
 */
public class ShellEmu extends Emu {
    
    public ShellEmu(File root) throws EmuException {
        super(root);
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length > 0 && argv[0].startsWith("-")) {
            System.err.println("Usage: shellemu [<jnode-home>]");
            return;
        }
        new ShellEmu(argv.length > 0 ? new File(argv[0]) : null).run();
    }

    private void run() throws Exception {
        TextScreenConsoleManager cm = new SwingTextScreenConsoleManager();
        TextScreenConsole console = cm.createConsole(
                "Console 1",
                (ConsoleManager.CreateOptions.TEXT | ConsoleManager.CreateOptions.SCROLLABLE));
        new Thread(new CommandShell(console, true)).start();
    }
}
