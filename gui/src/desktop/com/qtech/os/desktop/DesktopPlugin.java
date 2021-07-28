/*
 * $Id$
 *
 * Copyright (C) 2020-2021 QTech Community
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
 
package com.qtech.os.desktop;

import com.qtech.os.plugin.ExtensionPoint;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class DesktopPlugin extends Plugin {

    /**
     * @param descriptor
     */
    public DesktopPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * Gets the applications (apps) extensions point
     *
     * @return the extension point
     */
    public ExtensionPoint getAppsExtensionPoint() {
        return getDescriptor().getExtensionPoint("apps");
    }

    protected void startPlugin() throws PluginException {
//        System.setProperty("jnode.desktop", "com.qtech.os.desktop.classic.Desktop");
        System.setProperty("jnode.desktop", "com.qtech.os.desktop.classic.Desktop");
    }

    protected void stopPlugin() throws PluginException {
        // Nothing to do
    }
}
