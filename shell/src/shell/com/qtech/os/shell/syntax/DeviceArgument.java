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
 
package com.qtech.os.shell.syntax;

import javax.naming.NameNotFoundException;

import com.qtech.os.driver.Device;
import com.qtech.os.driver.DeviceAPI;
import com.qtech.os.driver.DeviceManager;
import com.qtech.os.driver.DeviceNotFoundException;
import com.qtech.os.driver.console.CompletionInfo;
import com.qtech.os.naming.InitialNaming;
import com.qtech.os.shell.CommandLine.Token;

/**
 * This argument class accepts and completes device ids.
 * 
 * @author qades
 * @author crawley@jnode.org
 */
public class DeviceArgument extends Argument<Device> {
    private final Class<? extends DeviceAPI> apiClass;

    public DeviceArgument(String label, int flags, String description, 
            Class<? extends DeviceAPI> apiClass) {
        super(label, flags, new Device[0], description);
        this.apiClass = apiClass;
    }

    public DeviceArgument(String label, int flags, String description) {
        this(label, flags, description, null);
    }

    public DeviceArgument(String label, int flags) {
        this(label, flags, null, null);
    }

    @Override
    protected Device doAccept(Token token, int flags) throws CommandSyntaxException {
        try {
            final DeviceManager devMgr = getDeviceManager();
            final Device device = devMgr.getDevice(token.text);
            if (apiClass == null || device.implementsAPI(apiClass)) {
                return device;
            } else {
                throw new CommandSyntaxException("this device does not implement the " +
                        apiClass.getSimpleName() + " API");
            }
        } catch (DeviceNotFoundException ex) {
            throw new CommandSyntaxException("unknown device");
        } 
    }

    @Override
    public void doComplete(CompletionInfo completions, String partial, int flags) {
        final DeviceManager devMgr = getDeviceManager();

        // collect matching devices
        for (Device dev : devMgr.getDevices()) {
            if (apiClass != null && !dev.implementsAPI(apiClass)) {
                continue;
            }
            final String devId = dev.getId();
            if (devId.startsWith(partial)) {
                completions.addCompletion(devId);
            }
        }
    }

    private DeviceManager getDeviceManager() {
        try {
            return InitialNaming.lookup(DeviceManager.NAME);
        } catch (NameNotFoundException ex) {
            throw new SyntaxFailureException("DeviceManager not found. Check your system setup");
        }
    }

    @Override
    protected String state() {
        return super.state() + ",apiClass=" + apiClass.getName();
    }

    @Override
    protected String argumentKind() {
        if (apiClass == null) {
            return "device";
        } else {
            return "device(" + apiClass.getSimpleName() + ")";
        }
    }
}
