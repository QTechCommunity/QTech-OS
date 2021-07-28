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
 
package com.qtech.os.driver.console.textscreen;

import java.awt.event.KeyEvent;
import java.io.PrintStream;

import javax.naming.NamingException;

import com.qtech.os.driver.console.ConsoleException;
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
public class TextScreenConsolePlugin extends Plugin {

    private TextScreenConsoleManager mgr;

    /**
     * @param descriptor
     */
    public TextScreenConsolePlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        try {
            mgr = new TextScreenConsoleManager();
            InitialNaming.bind(ConsoleManager.NAME, mgr);

            // Create the first console
            final TextConsole first = (TextConsole) mgr.createConsole(
                null,
                (ConsoleManager.CreateOptions.TEXT |
                    ConsoleManager.CreateOptions.SCROLLABLE));
            first.setAcceleratorKeyCode(KeyEvent.VK_F1);
            mgr.focus(first);
            System.setOut(new PrintStream(new WriterOutputStream(first.getOut(), false), true));
            System.setErr(new PrintStream(new WriterOutputStream(first.getErr(), false), true));
            System.out.println(VmSystem.getBootLog());
        } catch (ConsoleException ex) {
            throw new PluginException(ex);
        } catch (NamingException ex) {
            throw new PluginException(ex);
        }
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() throws PluginException {
        if (mgr != null) {
            mgr.closeAll();
            InitialNaming.unbind(ConsoleManager.NAME);
            mgr = null;
        }
    }
}
