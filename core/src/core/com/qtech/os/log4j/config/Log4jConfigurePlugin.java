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
 
package com.qtech.os.log4j.config;

import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.naming.NameNotFoundException;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import com.qtech.os.bootlog.BootLogInstance;
import com.qtech.os.driver.console.ActiveTextConsole;
import com.qtech.os.driver.console.ConsoleManager;
import com.qtech.os.driver.console.TextConsole;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;
import com.qtech.os.util.WriterOutputStream;
import org.jnode.vm.VmSystem;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class Log4jConfigurePlugin extends Plugin {

    public static final String LAYOUT = "%-5p [%c{1}]: %m%n";

    /**
     * @param descriptor
     */
    public Log4jConfigurePlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        final Logger root = Logger.getRootLogger();
        try {
            // Create the appenders
            final ConsoleManager conMgr = InitialNaming.lookup(ConsoleManager.NAME);
            final TextConsole console =
                (TextConsole) conMgr.createConsole(
                    "Log4j",
                    (ConsoleManager.CreateOptions.TEXT |
                        ConsoleManager.CreateOptions.SCROLLABLE |
                        ConsoleManager.CreateOptions.NO_SYSTEM_OUT_ERR |
                        ConsoleManager.CreateOptions.NO_LINE_EDITTING));
            conMgr.registerConsole(console);

            console.setAcceleratorKeyCode(KeyEvent.VK_F7);
            final VirtualConsoleAppender debugApp =
                new VirtualConsoleAppender(new PatternLayout(LAYOUT), console, false);
            debugApp.setThreshold(Level.DEBUG);
            BootLogInstance.get().setDebugOut(new PrintStream(new WriterOutputStream(console.getOut(), false), true));

            TextConsole atc = new ActiveTextConsole(conMgr);
            final VirtualConsoleAppender infoApp = new VirtualConsoleAppender(
                    new PatternLayout(LAYOUT), atc, false);
            infoApp.setThreshold(Level.INFO);

            // Add the new appenders
            root.addAppender(debugApp);
            root.addAppender(infoApp);

            // Remove the existing appenders.
            for (Enumeration<?> appEnum = root.getAllAppenders(); appEnum.hasMoreElements();) {
                final Appender appender = (Appender) appEnum.nextElement();
                if (appender != debugApp && appender != infoApp) {
                    root.removeAppender(appender);
                }
            }
            // lkd - bootloader flag sends logging data to Unsafe.debug()
            if (VmSystem.getCmdLine().indexOf(" lkd") > 0) {
                UnsafeDebugAppender kdbApp = new UnsafeDebugAppender(new PatternLayout(LAYOUT));
                kdbApp.setThreshold(Level.DEBUG);
                root.addAppender(kdbApp);
            }
        } catch (NameNotFoundException ex) {
            root.error("ConsoleManager not found", ex);
        }
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() throws PluginException {
        // Using LogManager.resetConfiguration() or LogManager.shutdown() 
        // can't avoid log4j WARN message about log4j not being properly configured.        
        // So, simply do nothing
    }

}
