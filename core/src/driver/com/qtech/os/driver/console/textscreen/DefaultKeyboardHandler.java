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

import java.io.IOException;

import javax.naming.NameNotFoundException;

import com.qtech.os.bootlog.BootLogInstance;
import com.qtech.os.driver.ApiNotFoundException;
import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceListener;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.input.KeyboardAPI;
import com.qtech.os.driver.input.KeyboardEvent;
import com.qtech.os.driver.input.KeyboardListener;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.system.event.FocusEvent;
import com.qtech.os.system.event.FocusListener;


/**
 * @author crawley@jnode.org
 *
 */
public class DefaultKeyboardHandler extends KeyboardHandler implements KeyboardListener,
        FocusListener, DeviceListener {

    private KeyboardAPI api;
    private final DeviceManager devMan;

    private boolean hasFocus;

    public DefaultKeyboardHandler(KeyboardAPI api) {
        if (api != null) {
            this.devMan = null;
            this.api = api;
            this.api.addKeyboardListener(this);
        } else {
            DeviceManager dm = null;
            try {
                dm = InitialNaming.lookup(DeviceManager.NAME);
                dm.addListener(this);
            } catch (NameNotFoundException ex) {
                BootLogInstance.get().error("DeviceManager not found", ex);
            }
            this.devMan = dm;
        }
    }

    private void registerKeyboardApi(Device device) {
        if (this.api == null) {
            try {
                this.api = device.getAPI(KeyboardAPI.class);
                this.api.addKeyboardListener(this);
            } catch (ApiNotFoundException ex) {
                BootLogInstance.get().error("KeyboardAPI not found", ex);
            }
            this.devMan.removeListener(this);
        }
    }

    /**
     * @see com.qtech.os.driver.input.KeyboardListener#keyPressed(com.qtech.os.driver.input.KeyboardEvent)
     */
    public void keyPressed(KeyboardEvent event) {
        if (hasFocus) {
            postEvent(event);
        }
    }

    /**
     * @see com.qtech.os.driver.input.KeyboardListener#keyReleased(com.qtech.os.driver.input.KeyboardEvent)
     */
    public void keyReleased(KeyboardEvent event) {
    }

    /**
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
        if (api != null) {
            api.removeKeyboardListener(this);
        }
    }

    /**
     * @see com.qtech.os.driver.DeviceListener#deviceStarted(com.qtech.os.driver.Device)
     */
    public void deviceStarted(Device device) {
        if (device.implementsAPI(KeyboardAPI.class)) {
            registerKeyboardApi(device);
        }
    }

    /**
     * @see com.qtech.os.driver.DeviceListener#deviceStop(com.qtech.os.driver.Device)
     */
    public void deviceStop(Device device) {
        /* Do nothing */
    }

    public void focusGained(FocusEvent event) {
        hasFocus = true;
    }

    public void focusLost(FocusEvent event) {
        hasFocus = false;
    }
}
