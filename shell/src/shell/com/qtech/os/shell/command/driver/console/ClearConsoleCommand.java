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
 
package com.qtech.os.shell.command.driver.console;

import com.qtech.os.driver.console.TextConsole;
import com.qtech.os.shell.AbstractCommand;
import com.qtech.os.shell.Shell;
import com.qtech.os.shell.ShellUtils;

/**
 * This command clears the console.
 * 
 * @author Jacob Kofod
 */
public class ClearConsoleCommand extends AbstractCommand {

    public ClearConsoleCommand() {
        super("Clear console screen");
    }

    /**
     * Clear console screen
     * 
     * @param args no arguments.
     */
    public static void main(String[] args) throws Exception {
        new ClearConsoleCommand().execute(args);
    }
    
    @Override
    public void execute() throws Exception {
        final Shell shell = ShellUtils.getCurrentShell();
        TextConsole tc = (TextConsole) shell.getConsole();
        tc.clear();
        tc.setCursor(0, 0);
    }
}
