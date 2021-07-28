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
 
package com.qtech.os.driver;

import javax.naming.NameNotFoundException;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;

/**
 * Plugin used to start the device discovery process.
 *
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class DeviceFinderPlugin extends Plugin {

    /**
     * @param descriptor
     */
    public DeviceFinderPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * Start this plugin
     *
     * @throws PluginException
     */
    protected void startPlugin() throws PluginException {
        try {
            final DefaultDeviceManager devMan = (DefaultDeviceManager) InitialNaming.lookup(DeviceManager.NAME);
            devMan.findDevices();
        } catch (NameNotFoundException ex) {
            throw new PluginException("Cannot find DeviceManager");
        } catch (InterruptedException ex) {
            throw new PluginException("findDevices was interrupted", ex);
        }
    }

    /**
     * Stop this plugin
     *
     * @throws PluginException
     */
    protected void stopPlugin() throws PluginException {
        // Do nothing
    }
}
