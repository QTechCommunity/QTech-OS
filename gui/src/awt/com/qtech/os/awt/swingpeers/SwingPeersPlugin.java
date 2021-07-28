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
 
package com.qtech.os.awt.swingpeers;

import gnu.java.security.action.SetPropertyAction;
import java.security.AccessController;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public final class SwingPeersPlugin extends Plugin {

    private static final String TOOLKIT = "com.qtech.os.awt.swingpeers.SwingToolkit";

    /**
     * @param descriptor
     */
    public SwingPeersPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        AccessController.doPrivileged(new SetPropertyAction("awt.toolkit", TOOLKIT));
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() throws PluginException {
    }
}
