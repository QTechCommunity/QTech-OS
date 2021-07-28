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
 
package com.qtech.os.debugger;

import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NameNotFoundException;

import com.qtech.os.driver.ApiNotFoundException;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceListener;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceUtils;
import com.qtech.os.driver.input.KeyboardAPI;
import com.qtech.os.driver.input.SystemTriggerAPI;
import com.qtech.os.plugin.Plugin;
import com.qtech.os.plugin.PluginDescriptor;
import com.qtech.os.plugin.PluginException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class DebuggerPlugin extends Plugin implements DeviceListener {

    private final Debugger debugger = new Debugger();

    /**
     * @param descriptor
     */
    public DebuggerPlugin(PluginDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * @see com.qtech.os.plugin.Plugin#startPlugin()
     */
    protected void startPlugin() throws PluginException {
        try {
            final DeviceManager dm = DeviceUtils.getDeviceManager();
            dm.addListener(this);
            final Collection<Device> devs = new ArrayList<Device>(dm.getDevices());
            for (Device dev : devs) {
                addListeners(dev);
            }
        } catch (NameNotFoundException ex) {
            throw new PluginException(ex);
        }
    }

    /**
     * @see com.qtech.os.plugin.Plugin#stopPlugin()
     */
    protected void stopPlugin() throws PluginException {
        try {
            final DeviceManager dm = DeviceUtils.getDeviceManager();
            dm.removeListener(this);
            final Collection<Device> devs = dm.getDevices();
            for (Device dev : devs) {
                removeListeners(dev);
            }
        } catch (NameNotFoundException ex) {
            throw new PluginException(ex);
        }
    }

    /**
     * @see com.qtech.os.driver.DeviceListener#deviceStarted(com.qtech.os.driver.Device)
     */
    public void deviceStarted(Device device) {
        addListeners(device);
    }

    /**
     * @see com.qtech.os.driver.DeviceListener#deviceStop(com.qtech.os.driver.Device)
     */
    public void deviceStop(Device device) {
        removeListeners(device);
    }

    private void addListeners(Device device) {
        if (device.implementsAPI(SystemTriggerAPI.class)) {
            try {
                final SystemTriggerAPI api = device.getAPI(SystemTriggerAPI.class);
                api.addSystemTriggerListener(debugger);
            } catch (ApiNotFoundException ex) {
                // Ignore
            }
        }
        if (device.implementsAPI(KeyboardAPI.class)) {
            try {
                final KeyboardAPI api = device.getAPI(KeyboardAPI.class);
                api.addKeyboardListener(debugger);
            } catch (ApiNotFoundException ex) {
                // Ignore
            }
        }
    }

    private void removeListeners(Device device) {
        if (device.implementsAPI(SystemTriggerAPI.class)) {
            try {
                final SystemTriggerAPI api = device.getAPI(SystemTriggerAPI.class);
                api.removeSystemTriggerListener(debugger);
            } catch (ApiNotFoundException ex) {
                // Ignore
            }
        }
        if (device.implementsAPI(KeyboardAPI.class)) {
            try {
                final KeyboardAPI api = device.getAPI(KeyboardAPI.class);
                api.removeKeyboardListener(debugger);
            } catch (ApiNotFoundException ex) {
                // Ignore
            }
        }
    }
}
