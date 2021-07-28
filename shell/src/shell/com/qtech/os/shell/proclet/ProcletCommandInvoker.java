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
 
package com.qtech.os.shell.proclet;

import java.util.Map;
import java.util.Properties;

import com.qtech.os.shell.AsyncCommandInvoker;
import com.qtech.os.shell.Command;
import com.qtech.os.shell.CommandInvoker;
import com.qtech.os.shell.CommandLine;
import com.qtech.os.shell.CommandRunner;
import com.qtech.os.shell.CommandShell;
import com.qtech.os.shell.CommandThread;
import com.qtech.os.shell.CommandThreadImpl;
import com.qtech.os.shell.ShellException;
import com.qtech.os.shell.SimpleCommandInvoker;
import com.qtech.os.shell.io.CommandIO;
import org.jnode.vm.VmSystem;

/**
 * This command invoker runs commands in their own proclet, giving each one its
 * own standard input / output / error stream, system properties and environment
 * variables.
 * 
 * @author crawley@jnode.org
 */
public class ProcletCommandInvoker extends AsyncCommandInvoker implements CommandInvoker {

    public static final Factory FACTORY = new Factory() {
        public SimpleCommandInvoker create(CommandShell shell) {
            return new ProcletCommandInvoker(shell);
        }

        public String getName() {
            return "proclet";
        }
    };
    
    private static boolean initialized;

    public ProcletCommandInvoker(CommandShell commandShell) {
        super(commandShell);
        init();
    }

    public String getName() {
        return "proclet";
    }
    
    public int invoke(CommandLine commandLine, Properties sysProps, Map<String, String> env)
        throws ShellException {
        CommandRunner cr = setup(commandLine, sysProps, env);
        return runIt(commandLine, cr);
    }

    public CommandThread invokeAsynchronous(CommandLine commandLine, Properties sysProps,
            Map<String, String> env) throws ShellException {
        CommandRunner cr = setup(commandLine, sysProps, env);
        return forkIt(commandLine, cr);
    }
    
    protected CommandThreadImpl createThread(CommandRunner cr) {
        CommandIO[] ios = cr.getIOs();
        return Proclet.createProclet(cr, cr.getSysProps(), cr.getEnv(), 
                new Object[] {
                        ios[Command.STD_IN].getInputStream(), 
                        ios[Command.STD_OUT].getPrintStream(),
                        ios[Command.STD_ERR].getPrintStream()}, 
                cr.getCommandName());
    }
    
    private static synchronized void init() {
        if (!initialized) {
            VmSystem.switchToExternalIOContext(new ProcletIOContext());
            initialized = true;
        }
    }
}
